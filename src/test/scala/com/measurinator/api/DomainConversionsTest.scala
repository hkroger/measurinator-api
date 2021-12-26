package com.measurinator.api

import java.time.{LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}
import java.time.format.DateTimeFormatter

import com.measurinator.api.entities.ClientMeasurement

class DomainConversionsTest extends org.scalatest.FunSuite with DomainConversions {
  test("conversion from client measurement is done correctly") {
    val domainMeasurement = toDomainMeasurement(ClientMeasurement("10", "1592277300000000000", "12", "45", "2.2", "-10", 3, "1000"), 10)
    assert(domainMeasurement.locationId == 10)
    assert(domainMeasurement.signalStrength == -10)
    val date = parseDate("2020-06-16T06:15:00+03:00")
    assert(domainMeasurement.yearMonth == "2020-06")
    assert(domainMeasurement.getTimestamp.toDate == date)
    val dateAtHour = parseDate("2020-06-16T06:00:00+03:00")
    assert(domainMeasurement.getHour.toDate == dateAtHour)

    val domainMeasurement2 = toDomainMeasurement(ClientMeasurement("10", "1640531955000000000", "12", "45", "2.2", "-10", 3, "1000"), 10)
    val date2 = parseDate("2021-12-26T17:19:15+02:00")
    assert(domainMeasurement2.yearMonth == "2021-12")
    assert(domainMeasurement2.getTimestamp.toDate == date2)
    val date2AtHour = parseDate("2021-12-26T17:00:00+02:00")
    assert(domainMeasurement2.getHour.toDate == date2AtHour)
  }

  def parseDate(s: String): java.util.Date = {
    val zonedDate = ZonedDateTime.parse(s, DateTimeFormatter.ISO_ZONED_DATE_TIME)
    java.util.Date.from(zonedDate.toInstant)
  }

}
