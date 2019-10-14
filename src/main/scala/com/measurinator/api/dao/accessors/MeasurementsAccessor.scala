package com.measurinator.api.dao.accessors

import java.util.UUID

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, Query}
import com.measurinator.api.dao.entities.Measurement

@Accessor
trait MeasurementsAccessor {

  @Query("SELECT * FROM measurements WHERE location_id = ? AND year_month = ? ORDER BY id DESC LIMIT ?")
  def findMeasurements(locationId: Int, yearMonth: String): Result[Measurement]

  @Query("SELECT * FROM measurements WHERE location_id = ? AND year_month = ? AND id >= ? ORDER BY id DESC LIMIT ?")
  def findMeasurementsStartingAt(locationId: Int, yearMonth: String, startDate: UUID): Result[Measurement]

  @Query("SELECT * FROM measurements WHERE location_id = ? AND year_month = ? AND id < ? ORDER BY id DESC LIMIT ?")
  def findMeasurementsBefore(locationId: Int, yearMonth: String, endDate: UUID): Result[Measurement]

  @Query("SELECT * FROM measurements WHERE location_id = ? AND year_month = ? AND id >= startDate AND id < endDate ORDER BY id DESC LIMIT ?")
  def findMeasurementsRange(locationId: Int, yearMonth: String, startDate: UUID, endDate: UUID): Result[Measurement]
}
