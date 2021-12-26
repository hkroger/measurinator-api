package com.measurinator.api

import com.measurinator.api.entities.ClientMeasurement
import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

/**
  * Created by hkroger on 10/4/2017.
  */
trait ChecksumCalculator {

  def calculateChecksum(clientMeasurement: ClientMeasurement, secret: String): String = {
    val checksummedString = clientMeasurement.version match {
      case 3 => clientMeasurement.version + "&" +
        clientMeasurement.timestamp + "&" +
        clientMeasurement.voltage + "&" +
        clientMeasurement.signalStrength + "&" +
        clientMeasurement.clientId + "&" +
        clientMeasurement.sensorId + "&" +
        clientMeasurement.measurement + "&" +
        secret
      // "#{hsh[:version]}&#{hsh[:timestamp]}&#{hsh[:voltage]}&#{hsh[:signal_strength]}&#{hsh[:client_id]}&#{hsh[:location_id]||hsh[:sensor_id]}&#{hsh[:measurement]}&#{secret}"
      case 2 => clientMeasurement.version + "&" +
        clientMeasurement.timestamp + "&" +
        clientMeasurement.voltage + "&" +
        clientMeasurement.signalStrength + "&" +
        clientMeasurement.clientId + "&" +
        clientMeasurement.sensorId + "&" +
        clientMeasurement.measurement + "&" +
        secret
        // "#{hsh[:version]}&#{hsh[:timestamp]}&#{hsh[:voltage]}&#{hsh[:signal_strength]}&#{hsh[:client_id]}&#{hsh[:location_id]||hsh[:sensor_id]}&#{hsh[:measurement]}&#{secret}"
      case 1 => clientMeasurement.version + "&" +
        clientMeasurement.voltage + "&" +
        clientMeasurement.signalStrength + "&" +
        clientMeasurement.clientId + "&" +
        clientMeasurement.sensorId + "&" +
        clientMeasurement.measurement + "&" +
        secret
        // "#{hsh[:version]}&#{hsh[:voltage]}&#{hsh[:signal_strength]}&#{hsh[:client_id]}&#{hsh[:location_id]||hsh[:sensor_id]}&#{hsh[:measurement]}&#{secret}"
      case _ => ""
    }

    checksummedString.sha1
  }

  def checkChecksum(clientMeasurement: ClientMeasurement, secret: String) {
    val calculatedChecksum = calculateChecksum(clientMeasurement, secret)
    if (!clientMeasurement.checksum.equals(calculatedChecksum)) {
      throw ChecksumErrorException(clientMeasurement.checksum, calculatedChecksum)
    }
  }

  case class ChecksumErrorException(clientChecksum: String, calculatedChecksum: String) extends Exception

}
