package com.measurinator.api

import java.text.SimpleDateFormat
import java.util.Date

import com.datastax.driver.core.utils.UUIDs
import com.measurinator.api.entities.{ClientMeasurement, Measurement}

/**
  * Created by hkroger on 10/4/2017.
  */
trait DomainConversions {
  def toDomainMeasurement(c: ClientMeasurement, locationId: Int): Measurement = {
    val longTimestamp = c.timestamp.toLong * 1000
    val timestamp = new Date(longTimestamp)
    val format = new SimpleDateFormat("YYYY-MM")
    val yearMonth = format.format(timestamp)

    new Measurement(
      locationId,
      yearMonth,
      UUIDs.startOf(longTimestamp),
      c.measurement.toDouble,
      BigDecimal(c.signalStrength),
      BigDecimal(c.voltage)
    )
  }
}
