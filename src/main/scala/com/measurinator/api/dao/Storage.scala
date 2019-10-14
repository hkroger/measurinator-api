package com.measurinator.api.dao

import com.datastax.driver.core.{Cluster, Session}
import com.datastax.driver.mapping._
import org.joda.time.DateTime

/**
  * Created by hkroger on 10/4/2017.
  */
class Storage extends MeasurementStorage with ClientStorage with SensorStorage with LocationStorage {
  val hosts = sys.env.getOrElse("CASSANDRA_HOSTS", "127.0.0.1,192.168.10.3").split(',')

  val cluster : Cluster = Cluster.builder()
    .addContactPoints(hosts: _*)
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
