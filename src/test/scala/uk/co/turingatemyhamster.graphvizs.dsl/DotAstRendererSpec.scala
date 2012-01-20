package uk.co.turingatemyhamster.graphvizs.dsl

import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.specs2.specification.Scope

class DotAstRendererSpec extends Specification {

  val parsers = new DotAstParser
  import parsers._

  "the dot ast renderer" ^
    idSpec ^
    attributeSpec ^
    statementSpec ^
    statementListSpec ^
    subgraphSpec ^
    graphSpec

  lazy val idSpec = new Specification {
    "identifier renderer" should {

      "render letter-only identifiers" in new renderer {
        renderWith(render_id, "abcd" : ID) must_== "abcd"
      }
      
      "render alphanumerics starting with a letter" in new renderer {
        renderWith(render_id, "ab786876" : ID) must_== "ab786876"
      }
      
      "render numeric negative double" in new renderer {
        renderWith(render_id, -1234.3453 : ID) must_== "-1234.3453"
      }

      "renduer numeric for 1" in new renderer {
        renderWith(render_id, 1 : ID) must_== "1.0"
      }
    }
  }

  lazy val attributeSpec = new Specification {
    "attribute renderer" should {

      "render attribute with name only" in new renderer {
        renderWith(render_attributeAssignment, "a" : AttributeAssignment) must_== "a"
      }

      "render attribute with name and identifier value" in new renderer {
        renderWith(render_attributeAssignment, "a" -> "b" : AttributeAssignment) must_== "a = b"
      }

      "render attribute with name and numeric value" in new renderer {
        renderWith(render_attributeAssignment, "a" -> 1 : AttributeAssignment) must_== "a = 1.0"
      }
    }
  }
  
  lazy val statementSpec = new Specification {
    "statement renderer" should {
      
      "render node with no attributes" in new renderer {
        renderWith(render_statement, "n1" : Statement) must_== "n1"
      }

      "render node with one numeric attribute" in new renderer {
        renderWith(render_statement, NodeStatement("n1", AttributeList("a" -> 3))) must_== "n1 [ a = 3.0 ]"
      }

      "render directed edge statement with no attributes" in new renderer {
        renderWith(render_statement, EdgeStatement("a", Seq(EdgeOp.-> -> "b", EdgeOp.-> -> "c"), None)) must_==
          "a -> b -> c"
      }

      "render an attribute statement" in new renderer {
        renderWith(render_statement, AttributeStatement(StatementType.Graph, AttributeList("color" -> "red"))) must_==
          "graph [ color = red ]"
      }

      "render an assignment statement" in new renderer {
        renderWith(render_statement, AssignmentStatement("color", "red")) must_== "color = red"
      }
    }
  }

  lazy val statementListSpec = new Specification {
    "statement list renderer" should {

      "render an empty list" in new renderer {
        renderWith(render_statementList, Seq()) must_== ""
      }

      "render one node statement" in new renderer {
        renderWith(render_statementList, Seq(NodeStatement("n1"))) must_== "n1\n"
      }

      "render one assignment statement" in new renderer {
        renderWith(render_statementList, Seq(AssignmentStatement("a", "b") : Statement)) must_== "a = b\n"
      }

      "render two assignment statements" in new renderer {
        renderWith(render_statementList, Seq(AssignmentStatement("a", "b") : Statement, AssignmentStatement("c", "d"))) must_== "a = b\nc = d\n"
      }
    }
  }

  lazy val subgraphSpec = new Specification {
    "subgraph renderer" should {

      "render an empty subgraph" in new renderer {
        renderWith(render_subgraph, Subgraph(None, Seq())) must_== "subgraph {}\n"
      }

      "render a named empty subgraph" in new renderer {
        renderWith(render_subgraph, Subgraph("g")) must_== "subgraph g {}\n"
      }

      "render a subgraph with one node" in new renderer {
        renderWith(render_subgraph, Subgraph("a" -> "b")) must_== "subgraph {\na = b\n}\n"
      }

      "render a subgraph with two nodes" in new renderer {
        renderWith(render_subgraph, Subgraph("a" -> "b", "c" -> "d")) must_== "subgraph {\na = b\nc = d\n}\n"
      }

    }
  }

  lazy val graphSpec = new Specification {
    "graph renderer" should {

      "render an empty graph" in new renderer {
        renderWith(render_graph, Graph(false, GraphType.Graph, None, Seq())) must_== "graph {}\n"
      }

      "render an empty digraph" in new renderer {
        renderWith(render_graph, Graph(false, GraphType.Digraph, None, Seq())) must_== "digraph {}\n"
      }

      "render a strict, empty graph" in new renderer {
        renderWith(render_graph, Graph(true, GraphType.Graph, None, Seq())) must_== "strict graph {}\n"
      }

      "render a named, strict, empty graph" in new renderer {
        renderWith(render_graph, Graph(true, GraphType.Graph, Some("G"), Seq())) must_== "strict graph G {}\n"
      }

      "render a named digraph with one edge" in new renderer {
        renderWith(render_graph, Graph(false, GraphType.Digraph, Some("G"), Seq(EdgeStatement("start", Seq(EdgeOp.-> -> "a0"), None)))) must_==
          "digraph G {\nstart -> a0\n}\n"
      }

    }
  }


  trait renderer extends Scope {
    val out = new java.lang.StringBuilder
    val renderer = new DotAstRenderer(out)

    def output = out.toString
    
    protected def renderWith[T](rt: T => Unit, t: T):String = {
      rt(t)
      output
    }

    protected val render_id = renderer.render_id _
    protected val render_attributeAssignment = renderer.render_attributeAssignment _
    protected val render_statement = renderer.render_statement _
    protected val render_statementList = renderer.render_statementList _
    protected val render_subgraph = renderer.render_subgraph _
    protected val render_graph = renderer.render_graph _
  }
}