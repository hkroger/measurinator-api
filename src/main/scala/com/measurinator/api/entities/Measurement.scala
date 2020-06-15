package com.measurinator.api.entities

import java.util.UUID

import com.datastax.driver.core.utils.UUIDs
import org.joda.time.DateTime

case class Measurement(locationId: Int,
                       yearMonth: String,
                       id: UUID,
                       measurement: Double,
                       signalStrength: BigDecimal,
                       voltage: BigDecimal) {
  def getDay: java.util.Date = {
    // TODO: Figure out a better way to truncate
    val ts = new DateTime(UUIDs.unixTimestamp(id))
    ts.withTimeAtStartOfDay().toDate
  }

  def getMeasurementInCents: Long = (measurement*100).round

  def getTimestamp: DateTime = {
    new DateTime(UUIDs.unixTimestamp(id))
  }

  def getHour: DateTime = {
    // TODO: Figure out a better way to truncate
    getTimestamp.
      withMinuteOfHour(0).
      withSecondOfMinute(0).
      withMillisOfSecond(0)
  }

}
