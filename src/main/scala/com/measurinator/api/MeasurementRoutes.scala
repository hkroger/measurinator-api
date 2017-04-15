package com.measurinator.api

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.measurinator.api.dao.Storage
import com.measurinator.api.entities.{ClientMeasurement, Measurement}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
  * Created by hkroger on 28/3/2017.
  */
class MeasurementRoutes(storage: Storage)(implicit ec: ExecutionContext) extends Protocols with SnakifiedProtocols with ChecksumCalculator with DomainConversions {
  case class UnknownClient(clientId: String) extends Exception
  case class UnknownLocation(clientId: String, locationId: Int) extends Exception
  case class UnknownSensor(sensorId: String) extends Exception

  val exceptionHandler = ExceptionHandler {
    case checksumError: ChecksumErrorException =>
      complete(StatusCodes.Unauthorized,
        ErrorMessage("Checksums don't match: " + checksumError.clientChecksum + " vs. " + checksumError.calculatedChecksum))
    case clientError: UnknownClient =>
      complete(StatusCodes.Forbidden,
        ErrorMessage("Unknown client: " + clientError.clientId))
    case locationError: UnknownLocation =>
      complete(StatusCodes.Forbidden,
        ErrorMessage("Unknown location " + locationError.locationId + " for client " + locationError.clientId))
    case sensorError: UnknownSensor =>
      complete(StatusCodes.Forbidden,
        ErrorMessage("Unknown sensor" + sensorError.sensorId))
  }

  val routes: Route = pathPrefix("measurements") {
    handleExceptions(exceptionHandler) {
      (post & pathEndOrSingleSlash) {
        entity(as[ClientMeasurement]) { clientMeasurement =>
          val eventualMaybeClient = Future {
            storage.findClient(clientMeasurement.clientId)
          }

          val eventualMaybeSensor = Future {
            storage.findSensor(clientMeasurement.sensorId)
          }

          val futureMeasurement =
            for {maybeClient <- eventualMaybeClient
                 maybeSensor <- eventualMaybeSensor
                 maybeLocation <- Future(maybeSensor.map(s => storage.findLocation(clientMeasurement.clientId, s.locationId)).flatten)
            } yield {

              if (maybeClient.isEmpty) {
                throw new UnknownClient(clientMeasurement.clientId)
              }
              val client = maybeClient.get
              checkChecksum(clientMeasurement, client.signingKey.toString)

              if (maybeSensor.isEmpty) {
                throw new UnknownSensor(clientMeasurement.sensorId)
              }
              val sensor = maybeSensor.get

              if (maybeLocation.isEmpty) {
                throw new UnknownLocation(clientMeasurement.clientId, sensor.locationId)
              }

              val measurement = toDomainMeasurement(clientMeasurement, sensor.locationId)

              storage.saveMeasurement(measurement)
              storage.updateMeasurementStats(measurement)
              storage.updateMeasurementsHourlyAvg(measurement)
              storage.updateMeasurementsDailyMinMax(measurement)
              storage.updateMeasurementsDailyAvg(measurement)
              storage.updateMeasurementsMonthlyMinMax(measurement)
              storage.updateMeasurementsMonthlyAvg(measurement)
              clientMeasurement
            }

          complete(futureMeasurement)
        }
      } ~ (get & path(Segment) & pathEndOrSingleSlash) { ip =>
        complete(Measurement(1, "temp", UUID.randomUUID(), 1.0, 1.0, 1.0))
      }
    }
  }
}



