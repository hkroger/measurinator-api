package com.measurinator.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{parameters, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.measurinator.api.dao.Storage

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

class LocationRoutes(storage: Storage)(implicit ec: ExecutionContext) extends Protocols with SnakifiedProtocols with DomainConversions {
  case class UnknownClient(clientId: String) extends Exception

  val exceptionHandler = ExceptionHandler {
    case clientError: UnknownClient =>
      complete(StatusCodes.Forbidden,
        ErrorMessage("Unknown client: " + clientError.clientId))
    case genericError: Exception =>
      complete(StatusCodes.InternalServerError,
        ErrorMessage("Generic error: " + genericError.toString + "\n Stack trace: " + genericError.getStackTrace.mkString("\n")))
  }

  val routes: Route = pathPrefix("locations") {
    handleExceptions(exceptionHandler) {
      (get & pathEndOrSingleSlash& parameters('client_id)) { (clientId) =>
        val eventualMaybeClient = Future { storage.findClient(clientId) }
        val locations: Future[List[entities.Location]] = for {maybeClient <- eventualMaybeClient } yield {
          if (maybeClient.isEmpty) {
            throw UnknownClient(clientId)
          }

          storage.findLocations(clientId).map(toDomainLocation(_))
        }

        complete(locations)
      }
    }
  }
}



