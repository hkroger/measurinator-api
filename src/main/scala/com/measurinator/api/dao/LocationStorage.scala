package com.measurinator.api.dao

import java.util.UUID

import com.datastax.driver.mapping.MappingManager
import com.measurinator.api.dao.entities.Location

/**
  * Created by hkroger on 10/4/2017.
  */
trait LocationStorage {
  implicit val manager: MappingManager

  lazy val locationMapper = manager.mapper(classOf[Location])

  def findLocation(clientId: String, locationId: Int): Option[Location] =
    Option(locationMapper.get(UUID.fromString(clientId), new Integer(locationId)))
}
