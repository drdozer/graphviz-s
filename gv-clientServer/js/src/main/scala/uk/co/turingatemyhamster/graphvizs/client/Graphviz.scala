package uk.co.turingatemyhamster.graphvizs
package client

import dsl._
import exec._

import org.scalajs.dom.extensions.Ajax

import org.scalajs.dom.{HTMLSelectElement, HTMLIFrameElement, HTMLTextAreaElement}
import rx._
import rx.ops._

import scala.concurrent.duration._
import scala.scalajs.js.Dynamic
import scala.scalajs.js.annotation.JSExport
import scala.util.Success
import scalatags.ext.Framework._
import scalatags.JsDom.all.{width => _, height => _, _}

/**
 *
 *
 * @author Matthew Pocock
 */
@JSExport
object Graphviz {

  @JSExport
  def wire(dotTextArea: HTMLTextAreaElement,
           layouts: HTMLSelectElement,
           dotSvg: HTMLIFrameElement,
           renderedDot: HTMLTextAreaElement): Unit =
  {
    implicit val scheduler = new DomScheduler
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

    val rxDTA = ReactiveTextArea(dotTextArea)

    val gvTextDebursted = rxDTA.value.debounce(5 seconds)

    val gvParsed = Rx {
      dsl.parseAsGraph(gvTextDebursted())
    }

    val rxLayouts = ReactiveSelect(layouts)

    val gvParsedLayout = Rx {
      gvParsed() -> rxLayouts.value()
    }

    Obs(gvParsedLayout) {
      println("Values changed. Calling dot.")
      Ajax.post(s"/graphviz/${rxLayouts.value()}.svg", gvTextDebursted()) onSuccess { case req =>
        println(s"Response: ${req.responseText}")
        renderedDot.value = req.responseText
        dotSvg.src="data:image/svg+xml;charset=utf-8," + Dynamic.global.escape(req.responseText)
      }
    }

    layouts.modifyWith(
      DotLayout.allLayouts map (l => option(value := l.name, l.name)) : _*
    ).render

    dotTextArea.value match {
      case v if v.isEmpty =>
        dotTextArea.value =
          """
            | digraph g {
            |   "hello" -> "world"
            | }
          """.stripMargin
    }

    layouts.value = "dot"
  }

}
