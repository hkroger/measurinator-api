package com.measurinator.api.dao

import java.util.UUID

import com.datastax.driver.core.Session
import com.datastax.driver.mapping.MappingManager
import com.measurinator.api.dao.entities.Client

/**
  * Created by hkroger on 10/4/2017.
  */
trait ClientStorage {
  implicit val cassandraSession: Session
  implicit val manager: MappingManager

  lazy val clientMapper = manager.mapper(classOf[Client])

  def findClient(clientId: String) : Option[Client] = {
    Option(clientMapper.get(UUID.fromString(clientId)))
  }
}
