package com.measurinator.api.dao.accessors

import java.util.Date

import com.datastax.driver.core.ResultSet
import com.datastax.driver.mapping.annotations.{Accessor, Param, Query}

/**
  * Created by hkroger on 10/4/2017.
  */

@Accessor
trait MeasurementStatsAccessor {
  @Query("UPDATE measurements_stats SET current = :measurement, signal_strength = :signal_strength, voltage = :voltage, last_read_at = :last_read_at WHERE location_id = :location_id")
  def updateBasics(@Param("location_id") locationId: Int,
                   @Param("measurement") measurement: Double,
                   @Param("signal_strength") signalStrength: java.math.BigDecimal,
                   @Param("voltage") voltage: java.math.BigDecimal,
                   @Param("last_read_at") lastReadAt: java.util.Date): ResultSet

  @Query("UPDATE measurements_stats SET first_read_at = :first_read_at WHERE location_id = :location_id")
  def updateFirstReadAt(@Param("location_id") locationId: Int,
                        @Param("first_read_at") firstReadAt: java.util.Date)

  @Query("UPDATE measurements_stats SET max = :measurement, max_at = :date WHERE location_id = :location_id")
  def updateMax(@Param("location_id") locationId: Int,
                @Param("measurement") measurement: Double,
                @Param("date") date: Date)

  @Query("UPDATE measurements_stats SET min = :measurement, min_at = :date WHERE location_id = :location_id")
  def updateMin(@Param("location_id") locationId: Int,
                @Param("measurement") measurement: Double,
                @Param("date") date: Date)


}
