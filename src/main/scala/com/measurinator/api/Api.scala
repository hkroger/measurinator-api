package com.measurinator.api

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import com.measurinator.api.dao.Storage

import scala.concurrent.ExecutionContext

trait Service {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContext

  implicit val materializer: Materializer

  implicit val logger: LoggingAdapter

  implicit val storage : Storage

  implicit val measurementRoutes : MeasurementRoutes

  lazy val routes: Route = {
      measurementRoutes.routes ~
      (get & pathEndOrSingleSlash) {
        complete("Measurinator Scala API")
      }
  }
}

object Api extends App with Service {
  System.setProperty("user.timezone", "UTC")
  implicit val system = ActorSystem()
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val logger: LoggingAdapter = Logging(system, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val storage: Storage = new Storage
  implicit val measurementRoutes = new MeasurementRoutes(storage)

  Http().bindAndHandle(routes, "127.0.0.1", 8899)
}
