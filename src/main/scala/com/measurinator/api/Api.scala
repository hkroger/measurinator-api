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

  implicit val storage : Storage

  implicit val measurementRoutes : MeasurementRoutes

  implicit val statusRoutes : StatusRoutes

  implicit val locationRoutes : LocationRoutes

  lazy val routes: Route = {
      measurementRoutes.routes ~
      locationRoutes.routes ~
      statusRoutes.routes ~
      (get & pathEndOrSingleSlash) {
        complete("Measurinator Scala API")
      }
  }
}

object Api extends App with Service {
  System.setProperty("user.timezone", "UTC")
  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val storage: Storage = new Storage
  implicit val measurementRoutes: MeasurementRoutes = new MeasurementRoutes(storage)
  implicit val locationRoutes: LocationRoutes = new LocationRoutes(storage)
  implicit val statusRoutes: StatusRoutes = new StatusRoutes(storage)

  Http().bindAndHandle(routes, "0.0.0.0", 8899)
}
