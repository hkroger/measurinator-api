package com.measurinator.api

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.measurinator.api.dao.Storage
import com.measurinator.api.entities.ClientMeasurement
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

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
        logRequest("POST measurements", Logging.DebugLevel) {
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
                   maybeLocation <- Future(maybeSensor.flatMap(s => storage.findLocationByClient(clientMeasurement.clientId, s.locationId)))
                   } yield {

                if (maybeClient.isEmpty) {
                  throw UnknownClient(clientMeasurement.clientId)
                }
                val client = maybeClient.get
                checkChecksum(clientMeasurement, client.signingKey.toString)

                if (maybeSensor.isEmpty) {
                  throw UnknownSensor(clientMeasurement.sensorId)
                }
                val sensor = maybeSensor.get

                if (maybeLocation.isEmpty) {
                  throw UnknownLocation(clientMeasurement.clientId, sensor.locationId)
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
        }
      } ~ (get & pathEndOrSingleSlash & parameters('location_id.as[Int], 'client_id, 'from, 'to)) { (locationId, clientId, from, to) =>
        handleExceptions(exceptionHandler) {
          val eventualMaybeClient = Future {
            storage.findClient(clientId)
          }
          val measurements: Future[List[entities.Measurement]] = for {maybeClient <- eventualMaybeClient
                                                                      maybeLocation <- Future(storage.findLocationByClient(clientId, locationId))
          } yield {
            if (maybeClient.isEmpty) {
              throw UnknownClient(clientId)
            }

            if (maybeLocation.isEmpty) {
              throw UnknownLocation(clientId, locationId)
            }

            val fromDatetime = DateTime.parse(from)
            val toDatetime = DateTime.parse(to)

            storage.findMeasurementsRange(locationId, fromDatetime, toDatetime).map(toDomainMeasurement)
          }

          complete(measurements)
        }
      }
    }
  }
}
