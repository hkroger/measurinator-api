package com.measurinator.api.dao

import com.datastax.driver.mapping._
import com.measurinator.api.dao.accessors.{MeasurementStatsAccessor, MeasurementsDailyAccessor, MeasurementsHourlyAccessor, MeasurementsMonthlyAccessor}
import com.measurinator.api.dao.entities.{Measurement, MeasurementDailyMinMax, MeasurementMonthlyMinMax, MeasurementsStats}

trait MeasurementStorage extends DAOConversions {
  implicit val manager: MappingManager

  lazy val measurementMapper = manager.mapper(classOf[Measurement])
  lazy val measurementDailyMinMaxMapper = manager.mapper(classOf[MeasurementDailyMinMax])
  lazy val measurementMonthlyMinMaxMapper = manager.mapper(classOf[MeasurementMonthlyMinMax])
  lazy val measurementStatsMapper = manager.mapper(classOf[MeasurementsStats])
  lazy val measurementStatsAccessor = manager.createAccessor(classOf[MeasurementStatsAccessor])
  lazy val measurementsHourlyAccessor = manager.createAccessor(classOf[MeasurementsHourlyAccessor])
  lazy val measurementsDailyAccessor = manager.createAccessor(classOf[MeasurementsDailyAccessor])
  lazy val measurementsMonthlyAccessor = manager.createAccessor(classOf[MeasurementsMonthlyAccessor])

  def saveMeasurement(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementMapper.save(measurement)
  }

  def saveMeasurementStatsCurrent(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementStatsAccessor.updateBasics(measurement.locationId,
      measurement.measurement,
      measurement.signalStrength.bigDecimal,
      measurement.voltage.bigDecimal,
      measurement.getTimestamp)
  }

  def updateMeasurementStats(measurement: com.measurinator.api.entities.Measurement): Unit = {
    val maybeStats: Option[MeasurementsStats] = Option(measurementStatsMapper.get(new Integer(measurement.locationId)))

    saveMeasurementStatsCurrent(measurement)

    maybeStats match {
      case Some(stats) =>
        // TODO: consider using LWT to make this "atomic"
        if (stats.min == null || stats.min > measurement.measurement) {
          measurementStatsAccessor.updateMin(measurement.locationId, measurement.measurement, measurement.getTimestamp)
        }

        // TODO: consider using LWT to make this "atomic"
        if (stats.max == null || stats.max < measurement.measurement) {
          measurementStatsAccessor.updateMax(measurement.locationId, measurement.measurement, measurement.getTimestamp)
        }

      case None =>
        measurementStatsAccessor.updateFirstReadAt(measurement.locationId, measurement.getTimestamp)
        measurementStatsAccessor.updateMax(measurement.locationId, measurement.measurement, measurement.getTimestamp)
        measurementStatsAccessor.updateMin(measurement.locationId, measurement.measurement, measurement.getTimestamp)
    }
  }

  def updateMeasurementsHourlyAvg(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementsHourlyAccessor.update(measurement.locationId, measurement.yearMonth, measurement.getHour, measurement.getMeasurementInCents)
  }

  def updateMeasurementsDailyMinMax(measurement: com.measurinator.api.entities.Measurement): Unit = {
    val maybeDailyMinMax = Option(measurementDailyMinMaxMapper.get(new java.lang.Integer(measurement.locationId), measurement.getDay))

    maybeDailyMinMax match {
      case Some(dailyMinMax) =>
        // TODO: consider using LWT to make this "atomic"
        if (dailyMinMax.min == null || dailyMinMax.min > measurement.measurement) {
          measurementsDailyAccessor.updateMin(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp)
        }

        // TODO: consider using LWT to make this "atomic"
        if (dailyMinMax.max == null || dailyMinMax.max < measurement.measurement) {
          measurementsDailyAccessor.updateMax(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp)
        }
      case None =>
        measurementsDailyAccessor.updateMin(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp)
        measurementsDailyAccessor.updateMax(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp)
    }

  }

  def updateMeasurementsDailyAvg(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementsDailyAccessor.update(measurement.locationId, measurement.yearMonth, measurement.getDay, measurement.getMeasurementInCents)
  }

  def updateMeasurementsMonthlyMinMax(measurement: com.measurinator.api.entities.Measurement): Unit = {
    val maybeMonthlyMinMax = Option(measurementMonthlyMinMaxMapper.get(new java.lang.Integer(measurement.locationId), measurement.yearMonth))

    maybeMonthlyMinMax match {
      case Some(monthlyMinMax) =>
        // TODO: consider using LWT to make this "atomic"
        if (monthlyMinMax.min == null || monthlyMinMax.min > measurement.measurement) {
          measurementsMonthlyAccessor.updateMin(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp)
        }

        // TODO: consider using LWT to make this "atomic"
        if (monthlyMinMax.max == null || monthlyMinMax.max < measurement.measurement) {
          measurementsMonthlyAccessor.updateMax(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp)
        }
      case None =>
        measurementsMonthlyAccessor.updateMin(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp)
        measurementsMonthlyAccessor.updateMax(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp)
    }

  }

  def updateMeasurementsMonthlyAvg(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementsMonthlyAccessor.update(measurement.locationId, measurement.yearMonth, measurement.getMeasurementInCents)
  }
}
