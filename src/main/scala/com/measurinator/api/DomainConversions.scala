package com.measurinator.api

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import com.datastax.driver.core.utils.UUIDs
import com.measurinator.api.entities.{ClientMeasurement, Location, Measurement}

import scala.util.Random

trait DomainConversions {
  val yearMonthDateFormat = new SimpleDateFormat("YYYY-MM")

  def toDomainLocation(l: dao.entities.Location): Location = {
    Location(
      l.clientId,
      l.id,
      if (l.quantity.isEmpty) "temperature" else l.quantity,
      l.description
    )
  }

  def toDomainMeasurement(m: dao.entities.Measurement): Measurement = {
    Measurement(
      m.locationId,
      m.yearMonth,
      m.id,
      m.measurement,
      m.signalStrength,
      m.voltage
    )
  }

  def toDomainMeasurement(c: ClientMeasurement, locationId: Int): Measurement = {
    // Needs to be ms
    val longTimestamp =
      if (c.version >= 3) {
        // Nanoseconds since version 3
        c.timestamp.toLong / 1000000
      } else {
        // Seconds until version 2
        c.timestamp.toLong * 1000
      }
    val timestamp = new Date(longTimestamp)
    val yearMonth = yearMonthDateFormat.format(timestamp)
    val random = new Random()
    val uuid = new UUID(UUIDs.startOf(longTimestamp).getMostSignificantBits, random.nextLong())

    Measurement(
      locationId,
      yearMonth,
      uuid,
      c.measurement.toDouble,
      BigDecimal(c.signalStrength),
      BigDecimal(c.voltage)
    )
  }
}
