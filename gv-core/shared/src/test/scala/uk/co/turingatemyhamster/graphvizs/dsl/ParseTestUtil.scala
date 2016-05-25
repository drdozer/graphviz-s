package uk.co.turingatemyhamster.graphvizs.dsl

import fastparse.core.{Parsed, Parser}

/**
  *
  *
  * @author Matthew Pocock
  */
object ParseTestUtil {

  def parseMustSucced[T](p: Parser[T], txt: String): T = p parse txt match {
    case Parsed.Success(t, at) if at != txt.length =>
      throw new Exception(s"Failed to completely parse. Consumed $at of ${txt.length} as $t")
    case Parsed.Success(t, _) =>
      t
    case f : Parsed.Failure => throw new Exception(s"Failed to parse: ${f.extra.traced.fullStack}")
  }

  def parseMustFail[T](p: Parser[T], txt: String): Unit = p parse txt match {
    case Parsed.Failure(_, _, _) => {}
  }

}
