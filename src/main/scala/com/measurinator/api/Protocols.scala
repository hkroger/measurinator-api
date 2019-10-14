package com.measurinator.api

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.scalapenos.spray._
import com.measurinator.api.entities.{ClientMeasurement, Location, Measurement}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat}

/**
  * Created by hkroger on 28/3/2017.
  */
trait Protocols extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ => throw new DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit val measurementJsonFormat = jsonFormat6(Measurement)
  implicit val locationJsonFormat = jsonFormat4(Location)
  implicit val errorMessageJsonFormat = jsonFormat1(ErrorMessage)
}

trait SnakifiedProtocols extends SprayJsonSupport with SnakifiedSprayJsonSupport {
  implicit val clientMeasurementJsonFormat = jsonFormat8(ClientMeasurement.apply)
}
