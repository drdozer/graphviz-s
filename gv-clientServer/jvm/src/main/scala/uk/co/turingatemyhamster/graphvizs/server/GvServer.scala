package uk.co.turingatemyhamster.graphvizs.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{MediaTypes, StatusCodes}
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
 *
 *
 * @author Matthew Pocock
 */
object GvServer {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("app-server")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val NotDot = """[^\.]*""".r

    val route: Route = path("graphviz" / NotDot ~ "." ~ NotDot) { case (layout, format) =>
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
      get {
        getFromResourceDirectory("")
      }

    val port = 10080
    val bindingsFuture = Http().bindAndHandle(route, "0.0.0.0", port)
    println(s"Service deployed to http://localhost:$port/")

//    Runtime.getRuntime.
//    println("exiting")
//    bindingsFuture.flatMap { b =>
//      println("unbinding")
//      b.unbind()
//    } onComplete {
//      _ =>
//        println("terminating")
//        system.terminate()
//        println("terminated")
//    }
  }

}