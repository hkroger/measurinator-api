package com.measurinator.api.dao

import com.datastax.driver.mapping.MappingManager
import com.measurinator.api.dao.entities.Sensor

/**
  * Created by hkroger on 10/4/2017.
  */
trait SensorStorage {
  implicit val manager: MappingManager

  lazy val sensorMapper = manager.mapper(classOf[Sensor])

  def findSensor(sensorId: String): Option[Sensor] = {
    Option(sensorMapper.get(new Integer(sensorId.toInt)))
  }
}
