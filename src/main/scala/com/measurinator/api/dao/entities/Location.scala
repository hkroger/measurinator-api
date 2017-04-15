package com.measurinator.api.dao.entities

import java.util.UUID

import com.datastax.driver.mapping.annotations.{ClusteringColumn, PartitionKey, Table}

import scala.annotation.meta.field

/**
  * Created by hkroger on 10/4/2017.
  */
@Table(name = "locations_by_client")
case class Location(@(PartitionKey@field) clientId: UUID, @(ClusteringColumn@field) id: Int) {
  def this() = this(null, 0)
}
