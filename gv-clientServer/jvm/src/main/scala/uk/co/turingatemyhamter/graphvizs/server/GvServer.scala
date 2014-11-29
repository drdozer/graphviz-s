package uk.co.turingatemyhamter.graphvizs.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

import scala.util.{Success, Failure}


/**
 *
 *
 * @author Matthew Pocock
 */
object GvServer extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("app-server")

  val NotDot = """[^\.]*""".r

  startServer(interface = "localhost", port = 8080) {
    path("graphviz" / NotDot ~ "." ~ NotDot) { (name, extension) =>
      (get | post) {
        complete(
          s"""
             | name: $name
             | extension: $name
           """.stripMargin)
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