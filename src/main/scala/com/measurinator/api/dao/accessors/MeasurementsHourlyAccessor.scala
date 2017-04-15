package com.measurinator.api.dao.accessors

import java.util.Date

import com.datastax.driver.mapping.annotations.{Accessor, Param, Query}

/**
  * Created by hkroger on 15/4/2017.
  */
@Accessor
trait MeasurementsHourlyAccessor {
  @Query("UPDATE measurements_hourly_avg SET temperature_count = temperature_count + 1, temperature_sum_in_cents = temperature_sum_in_cents + :temperature_sum_in_cents WHERE location_id = :location_id AND year_month = :year_month AND hour = :hour")
  def update(@Param("location_id") locationId: Int,
             @Param("year_month") yearMonth: String,
             @Param("hour") hour: Date,
             @Param("temperature_sum_in_cents") temperatureSumInCents: Long)

}
