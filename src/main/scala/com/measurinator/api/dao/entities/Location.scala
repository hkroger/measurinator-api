package com.measurinator.api.dao.entities

import java.util.UUID

import com.datastax.driver.mapping.annotations.{PartitionKey, Table}

import scala.collection.JavaConverters._
import scala.annotation.meta.field

/**
  * Created by hkroger on 10/4/2017.
  */
@Table(name = "locations")
case class Location(@(PartitionKey@field) id: Int,
                    clientId: UUID,
                    description: String,
                    doNotAlarm: Boolean,
                    doNotShow: Boolean,
                    doNotShowPublically: Boolean,
                    quantity: String,
                    sensors: java.util.Set[java.lang.Integer],
                    username: String)
{
  def this() = this(0, null, "", false, false, false, "", Set[java.lang.Integer]().asJava, "")
}
