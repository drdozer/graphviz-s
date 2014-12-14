package uk.co.turingatemyhamster.graphvizs.dsl

import utest._

/**
 *
 *
 * @author Matthew Pocock
 */
object ArrowTypeTestSuite extends TestSuite {
  val tests = TestSuite {
    "arrow types" - {
      "modifier data structures" - {
        "nothing give the empty string" -
          assert(Modifiers().name == "")
        "O gives o" -
          assert((O : Modifiers).name == "o")
        "L gives l" -
          assert((L : Modifiers).name == "l")
        "R gives r" -
          assert((R : Modifiers).name == "r")
        "OL gives ol" -
          assert(Modifiers(O, L).name == "ol")
      }
    }

    "arrow name data structures" - {
      "render Box to box" -
        assert((Shape.Box : ArrowName).name == "box")
      "render O:R:Tee to ortee" -
        assert(ArrowName(Modifiers(O, R), Shape.Tee).name == "ortee")
    }

    "arrow type data structures" - {
      "render one arrow name" -
        assert(ArrowType(Shape.Box :: Nil).name == "box")
      "render two arrow names" -
        assert(ArrowType(Shape.Box :: ArrowName(Modifiers(O, L), Shape.Dot) :: Nil).name == "boxoldot")
    }

    "arrow type strings" - {
      "parse arrow name with one arrow type" - {
        val res = ArrowTypeParsers.parseAll(ArrowTypeParsers.arrowType, "box")
        "parses successfully" -
          assert(res.successful)
        "parses to the expected value" -
          assert(res.get == ArrowType(Shape.Box :: Nil))
      }
      "parse arrow name with two arrow types" - {
        val res = ArrowTypeParsers.parseAll(ArrowTypeParsers.arrowType, "boxoldot")
        "parses successfully" -
          assert(res.successful)
        "parses to the expected value" -
          assert(res.get == ArrowType(Shape.Box :: ArrowName(Modifiers(O, L), Shape.Dot) :: Nil))

//        ArrowTypeParsers parsesAllOf
//          "boxoldot" using
//          ArrowTypeParsers.arrowType succeedsWith
//          ArrowType(Shape.Box :: ArrowName(Modifiers(O, L), Shape.Dot) :: Nil)
      }
    }
  }

//  implicit class ParserDSL[P <: RegexParsers](val _ps: P) {
//    def parsesAllOf(text: String): ParsersTextDSL[P] = new ParsersTextDSL[P] {
//      override def using[T](p: P#Parser[T]) = new ParsersTextParserDSL[P, T] {
//        override def succeedsWith(t: T) = {
//          val res = _ps.parseAll((p.asInstanceOf[_ps.Parser[T]]), text)
//          "parses value successfully" -
//            assert(res.successful)
//          "parses to the expected value" -
//            assert(res.get == t)
//        }
//      }
//    }
//  }
//
//  trait ParsersTextDSL[P <: RegexParsers] {
//    def using[T](p: P#Parser[T]): ParsersTextParserDSL[P, T]
//  }
//
//  trait ParsersTextParserDSL[P <: RegexParsers, T] {
//    def succeedsWith(t: T): TestSuite
//  }
}
