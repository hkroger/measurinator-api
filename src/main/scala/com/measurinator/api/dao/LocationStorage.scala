package com.measurinator.api.dao

import java.util.UUID
import scala.collection.JavaConverters._

import com.datastax.driver.mapping.{Mapper, MappingManager}
import com.measurinator.api.dao.accessors.LocationsByClientAccessor
import com.measurinator.api.dao.entities.LocationByClient
import com.measurinator.api.dao.entities.Location

/**
  * Created by hkroger on 10/4/2017.
  */
trait LocationStorage {
  implicit val manager: MappingManager

  lazy val locationByClientMapper: Mapper[LocationByClient] = manager.mapper(classOf[LocationByClient])
  lazy val locationMapper: Mapper[Location] = manager.mapper(classOf[Location])
  lazy val locationByClientAccessor: LocationsByClientAccessor = manager.createAccessor(classOf[LocationsByClientAccessor])

  def findLocationByClient(clientId: String, locationId: Int): Option[LocationByClient] =
    Option(locationByClientMapper.get(UUID.fromString(clientId), new Integer(locationId)))

  def findLocations(clientId: String): List[Location] =
    locationByClientAccessor.findByClient(UUID.fromString(clientId)).asScala.map(l => locationMapper.get(new java.lang.Integer(l.id))).toList
}
