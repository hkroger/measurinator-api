package com.measurinator.api.dao.entities

import java.util.UUID

import com.datastax.driver.mapping.annotations.{PartitionKey, Table}

import scala.annotation.meta.field

/**
  * Created by hkroger on 10/4/2017.
  */
@Table(name = "clients")
case class Client(@(PartitionKey@field) id: UUID, signingKey: UUID) {
  def this() = this(null, null)

}
