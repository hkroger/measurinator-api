package com.measurinator.api.dao

import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.mapping._
import com.measurinator.api.dao.accessors.{MeasurementStatsAccessor, MeasurementsAccessor, MeasurementsDailyAccessor, MeasurementsHourlyAccessor, MeasurementsMonthlyAccessor}
import com.measurinator.api.dao.entities.{Measurement, MeasurementDailyMinMax, MeasurementMonthlyMinMax, MeasurementsStats}
import org.joda.time.DateTime

import scala.collection.JavaConverters._

trait MeasurementStorage extends DAOConversions {
  implicit val manager: MappingManager

  lazy val measurementMapper: Mapper[Measurement] = manager.mapper(classOf[Measurement])
  lazy val measurementsAccessor: MeasurementsAccessor = manager.createAccessor(classOf[MeasurementsAccessor])
  lazy val measurementDailyMinMaxMapper: Mapper[MeasurementDailyMinMax] = manager.mapper(classOf[MeasurementDailyMinMax])
  lazy val measurementMonthlyMinMaxMapper: Mapper[MeasurementMonthlyMinMax] = manager.mapper(classOf[MeasurementMonthlyMinMax])
  lazy val measurementStatsMapper: Mapper[MeasurementsStats] = manager.mapper(classOf[MeasurementsStats])
  lazy val measurementStatsAccessor: MeasurementStatsAccessor = manager.createAccessor(classOf[MeasurementStatsAccessor])
  lazy val measurementsHourlyAccessor: MeasurementsHourlyAccessor = manager.createAccessor(classOf[MeasurementsHourlyAccessor])
  lazy val measurementsDailyAccessor: MeasurementsDailyAccessor = manager.createAccessor(classOf[MeasurementsDailyAccessor])
  lazy val measurementsMonthlyAccessor: MeasurementsMonthlyAccessor = manager.createAccessor(classOf[MeasurementsMonthlyAccessor])

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  def toYearMonth(time: DateTime): String = {
    time.formatted("yyyy-MM")
  }

  def findMeasurementsRange(locationId: Int, fromDatetime: DateTime, toDatetime: DateTime): List[Measurement] = {
    val startDateTime = List(fromDatetime, toDatetime).min
    val endDateTime = List(fromDatetime, toDatetime).max

    val startMonth = startDateTime.withDayOfMonth(1)
    val endMonth = endDateTime.withDayOfMonth(1)

    // single month case
    val measurements = if (startMonth.equals(endMonth)) {
      Iterator(measurementsAccessor.findMeasurementsRange(locationId, toYearMonth(startDateTime), UUIDs.startOf(startDateTime.getMillis), UUIDs.startOf(endDateTime.getMillis)))
    } else {

      val months = Iterator.iterate(endMonth) {
        _.minusMonths(1)
      }.takeWhile(!_.isBefore(startMonth))

      months.map(currentMonth =>
        if (startMonth.equals(currentMonth)) {
          measurementsAccessor.findMeasurementsStartingAt(locationId, toYearMonth(currentMonth), UUIDs.startOf(startDateTime.getMillis))
        } else if (endMonth.equals(currentMonth)) {
          measurementsAccessor.findMeasurementsBefore(locationId, toYearMonth(currentMonth), UUIDs.startOf(endDateTime.getMillis))
        } else {
          measurementsAccessor.findMeasurements(locationId, toYearMonth(currentMonth))
        }
      )
    }

    measurements.map(_.all()).map(_.asScala).flatMap(_.toList).toList
  }

  def saveMeasurement(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementMapper.save(measurement)
  }

  def saveMeasurementStatsCurrent(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementStatsAccessor.updateBasics(measurement.locationId,
      measurement.measurement,
      measurement.signalStrength.bigDecimal,
      measurement.voltage.bigDecimal,
      measurement.getTimestamp.toDate)
  }

  def updateMeasurementStats(measurement: com.measurinator.api.entities.Measurement): Unit = {
    val maybeStats: Option[MeasurementsStats] = Option(measurementStatsMapper.get(new Integer(measurement.locationId)))

    saveMeasurementStatsCurrent(measurement)

    maybeStats match {
      case Some(stats) =>
        // TODO: consider using LWT to make this "atomic"
        if (stats.min == null || stats.min > measurement.measurement) {
          measurementStatsAccessor.updateMin(measurement.locationId, measurement.measurement, measurement.getTimestamp.toDate)
        }

        // TODO: consider using LWT to make this "atomic"
        if (stats.max == null || stats.max < measurement.measurement) {
          measurementStatsAccessor.updateMax(measurement.locationId, measurement.measurement, measurement.getTimestamp.toDate)
        }

      case None =>
        measurementStatsAccessor.updateFirstReadAt(measurement.locationId, measurement.getTimestamp.toDate)
        measurementStatsAccessor.updateMax(measurement.locationId, measurement.measurement, measurement.getTimestamp.toDate)
        measurementStatsAccessor.updateMin(measurement.locationId, measurement.measurement, measurement.getTimestamp.toDate)
    }
  }

  def updateMeasurementsHourlyAvg(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementsHourlyAccessor.update(measurement.locationId, measurement.yearMonth, measurement.getHour.toDate, measurement.getMeasurementInCents)
  }

  def updateMeasurementsDailyMinMax(measurement: com.measurinator.api.entities.Measurement): Unit = {
    val maybeDailyMinMax = Option(measurementDailyMinMaxMapper.get(new java.lang.Integer(measurement.locationId), measurement.getDay))

    maybeDailyMinMax match {
      case Some(dailyMinMax) =>
        // TODO: consider using LWT to make this "atomic"
        if (dailyMinMax.min == null || dailyMinMax.min > measurement.measurement) {
          measurementsDailyAccessor.updateMin(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp.toDate)
        }

        // TODO: consider using LWT to make this "atomic"
        if (dailyMinMax.max == null || dailyMinMax.max < measurement.measurement) {
          measurementsDailyAccessor.updateMax(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp.toDate)
        }
      case None =>
        measurementsDailyAccessor.updateMin(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp.toDate)
        measurementsDailyAccessor.updateMax(measurement.locationId, measurement.getDay, measurement.measurement, measurement.getTimestamp.toDate)
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
          measurementsMonthlyAccessor.updateMin(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp.toDate)
        }

        // TODO: consider using LWT to make this "atomic"
        if (monthlyMinMax.max == null || monthlyMinMax.max < measurement.measurement) {
          measurementsMonthlyAccessor.updateMax(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp.toDate)
        }
      case None =>
        measurementsMonthlyAccessor.updateMin(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp.toDate)
        measurementsMonthlyAccessor.updateMax(measurement.locationId, measurement.yearMonth, measurement.measurement, measurement.getTimestamp.toDate)
    }

  }

  def updateMeasurementsMonthlyAvg(measurement: com.measurinator.api.entities.Measurement): Unit = {
    measurementsMonthlyAccessor.update(measurement.locationId, measurement.yearMonth, measurement.getMeasurementInCents)
  }
}
