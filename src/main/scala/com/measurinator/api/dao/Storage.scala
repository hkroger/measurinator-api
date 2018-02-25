package com.measurinator.api.dao

import com.datastax.driver.core.{Cluster, Session}
import com.datastax.driver.mapping._

/**
  * Created by hkroger on 10/4/2017.
  */
class Storage extends MeasurementStorage with ClientStorage with SensorStorage with LocationStorage {

  val hosts = Seq("127.0.0.1", "192.168.10.3")

  val cluster : Cluster = Cluster.builder()
    .addContactPoints(hosts.toArray: _*)
    .build()

  val cassandraSession: Session = cluster.connect("temperatures")

  val propertyMapper = new DefaultPropertyMapper()
    .setNamingStrategy(new DefaultNamingStrategy(
      NamingConventions.LOWER_CAMEL_CASE,
      NamingConventions.LOWER_SNAKE_CASE))

  val configuration =
    MappingConfiguration.builder()
      .withPropertyMapper(propertyMapper)
      .build()

  val manager = new MappingManager(cassandraSession, configuration)
}
