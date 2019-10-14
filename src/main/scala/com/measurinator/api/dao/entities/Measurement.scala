package com.measurinator.api.dao.entities

import java.util.UUID

import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.mapping.annotations.{ClusteringColumn, PartitionKey, Table}

import scala.annotation.meta.field

@Table(name = "measurements")
case class Measurement(@(PartitionKey@field)(0) locationId: Int,
                       @(PartitionKey@field)(1) yearMonth: String,
                       @(ClusteringColumn@field) id: UUID,
                       measurement: java.lang.Double,
                       signalStrength: java.math.BigDecimal,
                       voltage: java.math.BigDecimal) {
  def this() = this(0, "", UUIDs.timeBased(), 0.0d, BigDecimal.valueOf(0).bigDecimal, BigDecimal.valueOf(0).bigDecimal)
}
