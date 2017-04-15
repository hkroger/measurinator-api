package com.measurinator.api.entities

import java.util.UUID

import com.datastax.driver.core.utils.UUIDs

case class Measurement(locationId: Int,
                       yearMonth: String,
                       id: UUID,
                       measurement: Double,
                       signalStrength: BigDecimal,
                       voltage: BigDecimal) {
  def getDay: java.util.Date = {
    // TODO: Figure out a better way to truncate
    val ts = new java.util.Date(UUIDs.unixTimestamp(id))
    ts.setHours(0)
    ts.setMinutes(0)
    ts.setSeconds(0)
    ts
  }


  def getMeasurementInCents: Long = (measurement*100).round

  def getTimestamp = {
    new java.util.Date(UUIDs.unixTimestamp(id))
  }

  def getHour = {
    // TODO: Figure out a better way to truncate
    val ts = new java.util.Date(UUIDs.unixTimestamp(id))
    ts.setMinutes(0)
    ts.setSeconds(0)
    ts
  }

}
