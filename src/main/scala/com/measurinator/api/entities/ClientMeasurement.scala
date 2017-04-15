package com.measurinator.api.entities

/**
  * Created by hkroger on 28/3/2017.
  */
case class ClientMeasurement(clientId: String,
                             timestamp: String,
                             sensorId: String,
                             measurement: String,
                             voltage: String,
                             signalStrength: String,
                             version: Int,
                             checksum: String)
