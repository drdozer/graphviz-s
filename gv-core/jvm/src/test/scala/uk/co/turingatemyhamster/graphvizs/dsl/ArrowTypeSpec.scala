package uk.co.turingatemyhamster.graphvizs.dsl

import org.specs2.mutable.Specification
import scala.util.parsing.combinator.RegexParsers
import org.specs2.matcher.ParserMatchers

/**
 * Specification for arrow types.
 */

class ArrowTypeSpec extends Specification with ParserMatchers {

  val parsers = ArrowTypeParsers

  title("Arrow types")

  "modifier data structures" should {
    "nothing give the empty string" in {
      Modifiers().name must_== ""
    }

    "O gives o" in {
      (O : Modifiers).name must_== "o"
    }

    "L gives l" in {
      (L : Modifiers).name must_== "l"
    }

    "R gives r" in {
      (R : Modifiers).name must_== "r"
    }

    "OL gives ol" in {
      Modifiers(O, L).name must_== "ol"
    }
  }

  "arrow name data structures" should {
    "render Box to box" in {
      (Shape.Box : ArrowName).name must_== "box"
    }

    "render O:R:Tee to ortee" in {
      ArrowName(Modifiers(O, R), Shape.Tee).name must_== "ortee"
    }
  }

  "arrow type data structures" should {
    "render one arrow name" in {
      ArrowType(Shape.Box :: Nil).name must_== "box"
    }

    "render two arrow names" in {
      ArrowType(Shape.Box :: ArrowName(Modifiers(O, L), Shape.Dot) :: Nil).name must_== "boxoldot"
    }
  }

  "arrow type strings" should {
    "parse arrow name with one arrow type" in {
      parsers.arrowType must succeedOn("box").withResult(ArrowType(Shape.Box :: Nil))
    }

    "parse arrow name with two arrow types" in {
      parsers.arrowType must succeedOn("boxoldot").withResult(ArrowType(Shape.Box :: ArrowName(Modifiers(O, L), Shape.Dot) :: Nil))
    }
  }
}
