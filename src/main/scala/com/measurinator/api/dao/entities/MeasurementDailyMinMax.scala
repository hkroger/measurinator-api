package com.measurinator.api.dao.entities

import com.datastax.driver.mapping.annotations.{ClusteringColumn, PartitionKey, Table}

import scala.annotation.meta.field

/**
  * Created by hkroger on 15/4/2017.
  */
@Table(name = "measurements_daily_min_max")
case class MeasurementDailyMinMax(@(PartitionKey@field) locationId: Int,
                                  @(ClusteringColumn@field) day: java.util.Date,
                                  max: java.lang.Double,
                                  maxAt: java.util.Date,
                                  min: java.lang.Double,
                                  minAt: java.util.Date) {
  def this() = this(0, null, null, null, null, null)
}
