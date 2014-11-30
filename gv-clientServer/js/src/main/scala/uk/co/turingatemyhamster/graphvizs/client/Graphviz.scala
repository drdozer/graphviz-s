package uk.co.turingatemyhamster.graphvizs
package client

import dsl._
import org.scalajs.dom.extensions.Ajax

import org.scalajs.dom.{HTMLIFrameElement, HTMLTextAreaElement}
import rx._
import rx.ops._

import scala.concurrent.duration._
import scala.scalajs.js.Dynamic
import scala.scalajs.js.annotation.JSExport
import scala.util.Success
import scalatags.ext.Framework._
import scalatags.JsDom.all.{width => _, height => _, _}
import scalatags.JsDom.svgTags._
import scalatags.JsDom.svgAttrs._

/**
 *
 *
 * @author Matthew Pocock
 */
@JSExport
object Graphviz {

  @JSExport
  def wire(dotTextArea: HTMLTextAreaElement, dotSvg: HTMLIFrameElement, renderedDot: HTMLTextAreaElement): Unit = {

    implicit val scheduler = new DomScheduler
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

    val rxDTA = ReactiveTextArea(dotTextArea)

    val gvTextDebursted = rxDTA.value.debounce(5 seconds)

    val gvParsed = Rx {
      dsl.parseAsGraph(gvTextDebursted())
    }

    Obs(gvParsed) {
      Ajax.post("/graphviz/dot.svg", gvTextDebursted()) onSuccess { case req =>
        println(s"Response: ${req.responseText}")
        renderedDot.value = req.responseText
        dotSvg.src="data:image/svg+xml;charset=utf-8," + Dynamic.global.escape(req.responseText)
      }
    }

    dotTextArea.value match {
      case v if v.isEmpty =>
        dotTextArea.value =
          """
            | digraph g {
            |   "hello" -> "world"
            | }
          """.stripMargin
    }
  }

}
