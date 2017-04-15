package com.measurinator.api.dao.accessors

import java.util.Date

import com.datastax.driver.mapping.annotations.{Accessor, Param, Query}

/**
  * Created by hkroger on 15/4/2017.
  */
@Accessor
trait MeasurementsDailyAccessor {
  @Query("UPDATE measurements_daily_min_max SET min = :measurement, min_at = :timestamp WHERE location_id = :location_id AND day = :day")
  def updateMin(@Param("location_id") locationId: Int,
                @Param("day") day: Date,
                @Param("measurement") measurement: Double,
                @Param("timestamp") timestamp: Date)

  @Query("UPDATE measurements_daily_min_max SET max = :measurement, max_at = :timestamp WHERE location_id = :location_id AND day = :day")
  def updateMax(@Param("location_id") locationId: Int,
                @Param("day") day: Date,
                @Param("measurement") measurement: Double,
                @Param("timestamp") timestamp: Date)

  @Query("UPDATE measurements_daily_avg SET temperature_count = temperature_count + 1, temperature_sum_in_cents = temperature_sum_in_cents + :temperature_sum_in_cents WHERE location_id = :location_id AND year_month = :year_month AND day = :day")
  def update(@Param("location_id") locationId: Int,
             @Param("year_month") yearMonth: String,
             @Param("day") day: Date,
             @Param("temperature_sum_in_cents") temperatureSumInCents: Long)

}
