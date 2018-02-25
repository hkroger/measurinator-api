package com.measurinator.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.datastax.driver.core.exceptions.{NoHostAvailableException, QueryExecutionException, QueryValidationException}
import com.measurinator.api.dao.Storage

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

class StatusRoutes(storage: Storage)(implicit ec: ExecutionContext) extends Protocols with SnakifiedProtocols with ChecksumCalculator with DomainConversions {

  val exceptionHandler = ExceptionHandler {
    case noHostError: NoHostAvailableException =>
      complete(StatusCodes.InternalServerError,
        ErrorMessage("No Host Available Exception: " + noHostError.toString))
    case queryError: QueryExecutionException =>
      complete(StatusCodes.InternalServerError,
        ErrorMessage("Query Execution Failed Exception: " + queryError.toString))
    case queryValidationError: QueryValidationException =>
      complete(StatusCodes.InternalServerError,
        ErrorMessage("Query validation failed exception: " + queryValidationError.toString))
    case genericError: Exception =>
      complete(StatusCodes.InternalServerError,
        ErrorMessage("Generic error: " + genericError.toString))
  }

  val routes: Route = pathPrefix("status") {
    handleExceptions(exceptionHandler) {
      (get & pathEndOrSingleSlash) {
        storage.cassandraSession.execute("select * from system_schema.keyspaces where keyspace_name = 'temperatures'");
        complete("OK")
      }
    }
  }
}



