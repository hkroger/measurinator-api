package com.measurinator.api.dao.entities

import com.datastax.driver.mapping.annotations.{PartitionKey, Table}

import scala.annotation.meta.field

/**
  * Created by hkroger on 10/4/2017.
  */
@Table(name = "sensors")
case class Sensor(@(PartitionKey@field) id: Int, locationId: Int) {
  def this() = this(0, 0)
}
