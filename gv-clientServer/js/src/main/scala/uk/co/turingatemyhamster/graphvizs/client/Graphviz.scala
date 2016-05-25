package uk.co.turingatemyhamster.graphvizs
package client

import uk.co.turingatemyhamster.graphvizs.dsl._
import exec._
import org.widok._
import org.widok.bindings.HTML
import org.widok.html._
import pl.metastack.metarx.{Buffer, ReadChannel, Var}

/**
 *
 *
 * @author Matthew Pocock
 */
object Graphviz extends PageApplication {
  implicit val ec = scala.scalajs.concurrent.JSExecutionContext.queue

  val defaultText =
            """
              |digraph {
              |  "hello" -> "world"
              |}
            """.stripMargin.trim

  val layouts = Buffer(DotLayout.allLayouts :_*)
  val layout = Var(Some(DotLayout.dot) : Option[DotLayout])
  val gvUrl = layout.filter(_.isDefined) map { l => s"/graphviz/${l.get.name}.svg" }
  val dotText = Var(defaultText)
  val svgText = Var("")
  private val dotTextDistinct = dotText.distinct
  val parsed = dotTextDistinct map { txt =>
    (txt, DotAstParser.graphFile.parse(txt))
  }
  val status = parsed map {
    case (_, fastparse.all.Parsed.Success(_, _)) => "success"
    case (_, f : fastparse.all.Parsed.Failure) => f.msg
  }
  val parseStatus = parsed map {
    case (t, fastparse.all.Parsed.Success(_, _)) => (t, true)
    case (t, fastparse.all.Parsed.Failure(_, _, _)) => (t, false)
  }
  val parseStatusBool = parseStatus map (_._2)

  gvUrl zip parseStatus filter {
    case(_, (_, s)) => s
  } attach {
    case(url, (txt, _)) =>
      val req = HTTP.post(url, txt)
      println(s"req: url: $url")
      println(s"req: txt: $txt")
      req.onSuccess { case t => svgText := t }
      req.onFailure { case t => println(t) }
  }

  dotText.attach(t => println(s"dotText: $t"))
  dotTextDistinct.attach(t => println(s"dotTextDistinct: $t"))
  layout.attach(l => println(s"layout: $l"))
  gvUrl.attach(u => println(s"gvUrl: $u"))
  parsed.attach(p => println(s"parsed: $p"))
  status.attach(s => println(s"status: $s"))
  parseStatus.attach(s => println(s"parseStatus: $s"))

  def view() = div(
    h1("Graphviz interactive editor"),
    textarea()
      .css("dotEditor")
      .rows(20)
      .cols(60)
      .bind(dotText),
    div(status)
      .css("status")
      .cssState(parseStatusBool, "pass")
      .cssState(!parseStatusBool, "fail"),
    select()
      .css("dotLayout")
      .bind(layouts, (l: DotLayout) => HTML.Input.Select.Option(l.name), layout),
    raw("")
      .css("svgArea")
      .bind(svgText),
    textarea()
      .css("svgText")
      .enabled(false)
      .rows(20)
      .cols(60)
      .bind(svgText)
  )

  override def ready(): Unit = {}
}
