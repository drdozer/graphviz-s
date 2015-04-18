package uk.co.turingatemyhamster.graphvizs.server

import akka.util.Timeout
import spray.http.{MediaTypes, MediaType, StatusCodes}

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

import scala.util.{Success, Failure}
import scala.concurrent.duration._

/**
 *
 *
 * @author Matthew Pocock
 */
object GvServer extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("app-server")

  val NotDot = """[^\.]*""".r

  implicit val timeout: Timeout = Timeout(5 seconds)

  startServer(interface = "0.0.0.0", port = 9100) {
    path(NotDot ~ "." ~ NotDot) { (layout, format) =>
      post {
        entity(as[String]) { dotString =>
          import uk.co.turingatemyhamster.graphvizs._
          import exec._

          complete {
            dot2dot[String, String](
              from = dotString,
              layout = DotLayout(layout),
              format = DotFormat(format::Nil))
          }
        }
      }
    } ~
    path("editor") {
      get {
        respondWithMediaType(MediaTypes.`text/html`) {
          complete {
            Content.editor.render
          }
        }
      }
    } ~
    pathPrefix("public") {
      get {
        getFromResourceDirectory("public")
      }
    }
  }.onComplete {
    case Success(b) =>
      println(s"Successfully bound to ${b.localAddress}")
    case Failure(ex) =>
      println(ex.getMessage)
      system.shutdown()
  }
}