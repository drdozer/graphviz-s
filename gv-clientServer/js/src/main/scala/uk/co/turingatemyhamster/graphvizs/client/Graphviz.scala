package uk.co.turingatemyhamster.graphvizs
package client

import uk.co.turingatemyhamster.graphvizs.dsl._
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

    // prime the pumps
    dotTextArea.value match {
      case v if v.isEmpty =>
        dotTextArea.value =
          """
            |digraph {
            |  "hello" -> "world"
            |}
          """.stripMargin.trim
    }

    layouts.modifyWith(
      DotLayout.allLayouts map (l => option(value := l.name, l.name)) : _*
    ).render

    layouts.value = "dot"

    val rxDTA = ReactiveTextArea(dotTextArea)

    val gvTextDebursted = rxDTA.value.debounce(5 seconds)

    Obs(gvTextDebursted) {
      println("gvTextDebursted")
    }

    val gvParsed = Rx {
      DotAstParser.parseAll(DotAstParser.graph, gvTextDebursted().trim)
    }

    Obs(gvParsed) {
      println(s"gvParsed")
    }

    val gvValid = gvParsed.map {
      case DotAstParser.NoSuccess(_, _) => None
      case DotAstParser.Success(res, _) => Some(res)
    }

    Obs(gvValid) {
      println(s"gvValid: ${gvValid()}")
    }

    val gvInvalid = gvParsed.map {
      case DotAstParser.NoSuccess(msg, next) => Some(msg, next.pos)
      case DotAstParser.Success(_, _) => None
    }

    Obs(gvInvalid) {
      println(s"gvInvalid: ${gvInvalid()}")
    }

    val rxLayout = ReactiveSelect(layouts).value.map(Some apply _ filterNot (_.isEmpty))

    Obs(rxLayout) {
      println(s"rxLayout: ${rxLayout()}")
    }

    val gvParsedLayout = Rx {
      for {
        p <- gvValid()
        l <- rxLayout()
      } yield (p, l)
    }

    Obs(gvParsedLayout) {
      println(s"gvParsedLayout: ${gvParsedLayout()}")
    }

    Obs(gvParsedLayout) {
      for {
        _ <- gvParsedLayout()
        text = gvTextDebursted()
        lo <- rxLayout()
      } {
        println(s"Triggering gv call with ${gvParsedLayout()}")
        Ajax.post(s"/graphviz/${lo}.svg", text) onSuccess { case req =>
          println("Got response")
          renderedDot.value = req.responseText
          dotSvg.src = "data:image/svg+xml;charset=utf-8," + Dynamic.global.escape(req.responseText)
        }
      }
    }
  }

}
