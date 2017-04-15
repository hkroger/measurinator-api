package com.measurinator.api.dao.entities

import java.util.Date

import com.datastax.driver.mapping.annotations.{PartitionKey, Table}

import scala.annotation.meta.field

/**
  * Created by hkroger on 11/4/2017.
  */
@Table(name = "measurements_stats")
case class MeasurementsStats(@(PartitionKey@field) locationId: Int,
                             alarmedAt: Date,
                             current: java.lang.Double,
                             firstReadAt: Date,
                             lastReadAt: Date,
                             max: java.lang.Double,
                             maxAt: Date,
                             min: java.lang.Double,
                             minAt: Date,
                             signalStrength: java.math.BigDecimal,
                             voltage: java.math.BigDecimal) {

  def this() = this(0, null, 0, null, null, 0, null, 0, null, null, null)

}
