package uk.co.turingatemyhamster.graphvizs.dsl

import org.specs2.mutable._
import org.specs2.matcher.ParserMatchers
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DotAstParserSpec extends Specification with ParserMatchers {

  val parsers = new DotAstParser
  import parsers._

  "the dot ast parser" ^
    astSpec ^
    cstrSpec ^
    idSpec ^
    attributeSpec ^
    statementSpec ^
    statementListSpec ^
    subgraphSpec ^
    graphSpec


  lazy val astSpec = new Specification {
    "implicit ast conversions" should {

      "promote string to ID" in {
        ("a" : ID) must_== ID.Identifier("a")
      }
      
      "promote double to ID" should {
        (1.0 : ID) must_== ID.Numeral(1.0)
      }

      "prmote int to ID" should {
        (1 : ID) must_== ID.Numeral(1.0)
      }

      "promote ID to NodeId" in {
        (ID.Identifier("a") : NodeId) must_== NodeId(ID.Identifier("a"), None)
      }

      "prmote string to NodeId" in {
        ("a" : NodeId) must_== NodeId(ID.Identifier("a"), None)
      }

      "promote string to NodeStatement" in {
        ("n1" : NodeStatement) must_==  NodeStatement("n1", None)
      }

      "promote ID to an AttributeAssignment" in {
        ("a" : AttributeAssignment) must_== AttributeAssignment("a")
      }

      "promote ID pair to an AttributeAssignment" in {
        (("a" -> "b"): AttributeAssignment) must_== AttributeAssignment("a", "b")
      }

      "promote ID pair to an AttributeStatement" in {
        (("a" -> "b") : AssignmentStatement) must_== AssignmentStatement("a", "b")
      }

    }
  }

  lazy val cstrSpec = new Specification {
    "utility constructors" should {

      "build attribute list from no args" in {
        AttributeList() must_== AttributeList(None, None)
      }

      "build attribute list from varargs length 1" in {
        AttributeList("a" -> "b") must_== AttributeList(Some(Seq(AttributeAssignment("a", "b"))))
      }

      "build attribute list from varargs length 2" in {
        AttributeList("a" -> "b", "c" -> "d") must_==
          AttributeList(Some(Seq(AttributeAssignment("a", "b"), AttributeAssignment("c", "d"))))
      }

      "build subgraph from ID only" in {
        Subgraph("a") must_== Subgraph(Some("a" : ID), Seq())
      }

      "build subgraph from statements only" in {
        Subgraph("a" -> "b") must_== Subgraph(None, Seq(AssignmentStatement("a", "b")))
      }

    }
  }

  lazy val idSpec = new Specification {
    "identifier parser" should {

      "parse letter-only identifiers" in {
        id must succeedOn("abcd").withResult(ID.Identifier("abcd") : ID)
      }

      "parse alphanumerics starting with a letter" in {
        id must succeedOn("ab786876").withResult(ID.Identifier("ab786876") : ID)
      }

      "parse numeric negative double" in {
        id must succeedOn("-1234.3453").withResult(ID.Numeral(-1234.3453) : ID)
      }

      "parse numeric for 1" in {
        id must succeedOn("1").withResult(ID.Numeral(1.0) : ID)
      }

      "be unable to parse numeric for 1 with the id_identifier parser" in {
        id_identifier must failOn("1")
      }

      "parse numeric for 1 with the id_numeral parser" in {
        id_numeral must succeedOn("1").withResult(ID.Numeral(1.0))
      }

      "be unable to parse numeric for 1 with the id_quoted_string parser" in {
        id_quoted_string must failOn("1")
      }

    }
  }

  lazy val attributeSpec = new Specification {
    "attribute assignment parser" should {

      "parse attribute with name only" in {
       attribute_assignment must succeedOn("a").withResult(AttributeAssignment("a", None))
      }

      "parse attribute with name and identifier value" in {
        attribute_assignment must succeedOn("a = b").withResult(AttributeAssignment("a", "b"))
      }

      "parse attribute with name and numeric value" in {
        attribute_assignment must succeedOn("a = 1").withResult(AttributeAssignment("a", 1))
      }

    }
  }


  lazy val statementSpec = new Specification {
    "individual statement parser" should {

      "parse a node statement with no attributes" in {
        node_statement must succeedOn("n1").withResult(NodeStatement("n1", None))
      }
      
      "parse a node statement with one numeric attribute" in {
        node_statement must succeedOn("n1 [a = 3]").withResult(
          NodeStatement("n1", AttributeList("a" -> 3)))
      }
      
      "parse a directed edge statement with no attributes" in {
        edge_statement must succeedOn("a -> b -> c").withResult(
          EdgeStatement("a", Seq(EdgeOp.-> -> "b", EdgeOp.-> -> "c"), None))
      }

      "fail to parse a node as an edge" in {
        edge_statement must failOn("n1")
      }

      "parse an attribute statement" in {
        attribute_statement must succeedOn("graph [color = red]").withResult(
          AttributeStatement(StatementType.Graph, AttributeList("color" -> "red")))
      }

      "parse an assignment statement" in {
        assignment_statement must succeedOn("color = red").withResult(AssignmentStatement("color", "red"))
      }

    }
  }

  lazy val statementListSpec = new Specification {
    "statement_list parser" should {

      "parse the empty string" in {
        statement_list must succeedOn("").withResult(Seq[Statement]())
      }

      "parse one node statement" in {
        statement_list must succeedOn("n1").withResult(Seq(NodeStatement("n1", None) : Statement))
      }

      "parse one assignment statement" in {
        statement_list must succeedOn("a = b").withResult(Seq(AssignmentStatement("a", "b") : Statement))
      }

      "parse one assignment statement with a trailing semi-colon" in {
        statement_list must succeedOn("a = b;").withResult(Seq(AssignmentStatement("a", "b") : Statement))
      }

      "parse two assignment statements" in {
        statement_list must succeedOn("a = b ; c = d").withResult(Seq(AssignmentStatement("a", "b") : Statement, AssignmentStatement("c", "d")))
      }
    }
  }

  lazy val subgraphSpec = new Specification {
    "subgraph parser" should {

      "parse an empty subgraph" in {
        subgraph must succeedOn("subgraph {}").withResult(Subgraph(None, Seq()))
      }

      "parse a named empty subgraph" in {
        subgraph must succeedOn("subgraph g {}").withResult(Subgraph("g"))
      }

      "parse a subgraph with one node" in {
        subgraph must succeedOn("subgraph { a = b }").withResult(Subgraph("a" -> "b"))
      }

      "parse a subgraph with two nodes" in {
        subgraph must succeedOn("subgraph { a = b; c = d }").withResult(Subgraph("a" -> "b", "c" -> "d"))
      }
    }
  }


  lazy val graphSpec = new Specification {
    "graph parser" should {

      "parse an empty graph" in {
        graph must succeedOn("""graph {}""").withResult(Graph(false, GraphType.Graph, None, Seq()))
      }
      
      "parse an empty digraph" in {
        graph must succeedOn("""digraph {}""").withResult(Graph(false, GraphType.Digraph, None, Seq()))
      }

      "parse a strict, empty graph" in {
        graph must succeedOn("""strict graph {}""").withResult(Graph(true, GraphType.Graph, None, Seq()))
      }

      "parse a named, strict, empty graph" in {
        graph must succeedOn("""strict graph G {}""").withResult(Graph(true, GraphType.Graph, Some("G"), Seq()))
      }

      "parse a named digraph with one edge" in {
        graph must succeedOn("""
                digraph G {
                	start -> a0;
                }""").withResult(Graph(false, GraphType.Digraph, Some("G"), Seq(EdgeStatement("start", Seq(EdgeOp.-> -> "a0"), None))))
      }

      "parse the cluster example" in {
        graph must succeedOn("""
        digraph G {
      
        	subgraph cluster_0 {
        		style=filled;
        		color=lightgrey;
        		node [style=filled,color=white];
        		a0 -> a1 -> a2 -> a3;
        		label = "process #1";
        	}
      
        	subgraph cluster_1 {
        		node [style=filled];
        		b0 -> b1 -> b2 -> b3;
        		label = "process #2";
        		color=blue
        	}
        	start -> a0;
        	start -> b0;
        	a1 -> b3;
        	b2 -> a3;
        	a3 -> a0;
        	a3 -> end;
        	b3 -> end;
      
        	start [shape=Mdiamond];
        	end [shape=Msquare];
        }""")
      }

    }
  }
}