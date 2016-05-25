package uk.co.turingatemyhamster.graphvizs
package dsl

import fastparse.core.{Parsed, Parser}
import utest._

import ParseTestUtil._

/**
 *
 *
 * @author Matthew Pocock
 */
object DotAstTestSuite extends TestSuite {

  val tests = TestSuite {
    "implicit ast conversions" - {

      "promote string to ID" - {
        val id = stringAsId("a"): ID
        "implicit conversion invoked" - id
        "implicit conversion gave the expected value" - assert(id == ID.Identifier("a"))
      }


      "promote double to ID" -
        assert((1.0 : ID) == ID.Numeral(1.0))

      "prmote int to ID" -
        assert((1 : ID) == ID.Numeral(1.0))

      "promote ID to NodeId" -
        assert((ID.Identifier("a") : NodeId) == NodeId(ID.Identifier("a"), None))

      "prmote string to NodeId" -
        assert(("a" : NodeId) == NodeId(ID.Identifier("a"), None))

      "promote string to NodeStatement" -
        assert(("n1" : NodeStatement) ==  NodeStatement("n1", None))

      "promote ID to an AttributeAssignment" -
        assert(("a" : AttributeAssignment) == AttributeAssignment("a"))

      "promote ID pair to an AttributeAssignment" -
        assert((("a" := "b"): AttributeAssignment) == AttributeAssignment("a", "b"))

    }

    "utility constructors" - {
      "build attribute list from no args" -
        assert(AttributeList() == AttributeList(Seq(), None))

      "build subgraph from ID only" -
        assert(Subgraph("a") == Subgraph(Some("a" : ID), Seq()))
    }

    "identifier parser" - {

      "parse letter-only identifiers" - {
        val res = parseMustSucced(DotAstParser.id_identifier, "abcd")
        "parse to the expected value" -
          assert(res == (ID.Identifier("abcd") : ID))
      }

      "parse letter-only identifiers" - {
        val res = parseMustSucced(DotAstParser.id, "abcd")
        "parse to the expected value" -
          assert(res == (ID.Identifier("abcd") : ID))
      }

      "parse alphanumerics starting with a letter" - {
        val res = parseMustSucced(DotAstParser.id, "ab786876")
        "parse to the expected value" -
          assert(res == (ID.Identifier("ab786876") : ID))
      }

      "parse numeric negative double" - {
        val res = parseMustSucced(DotAstParser.id, "-1234.3453")
        "parse to the expected value" -
          assert(res == (ID.Numeral(-1234.3453) : ID))
      }

      "parse numeric for 1" - {
        val res = parseMustSucced(DotAstParser.id, "1")
        "parse to the expected value" -
          assert(res == (ID.Numeral(1.0) : ID))
      }

      "be unable to parse numeric for 1 with the id_identifier parser" - {
        val res = parseMustFail(DotAstParser.id_identifier, "1")
      }

      "parse numeric for 1 with the id_numeral parser" - {
        val res = parseMustSucced(DotAstParser.id_numeral , "1")
        "parse to the expected value" -
          assert(res == (ID.Numeral(1.0) : ID))
      }

      "be unable to parse numeric for 1 with the id_quoted_string parser" - {
        val res = parseMustFail(DotAstParser.id_quoted_string , "1")
      }

    }

    "attribute assignment parser" - {

      "parse attribute with name only" - {
        val res = parseMustSucced(DotAstParser.attribute_assignment , "a")
        "parse to the expected value" -
          assert(res == AttributeAssignment("a", None))
      }

      "parse attribute with name and identifier value without spaces" - {
        val res = parseMustSucced(DotAstParser.attribute_assignment , "a=b")
        "parse to the expected value" -
          assert(res == AttributeAssignment("a", "b"))
      }

      "parse attribute with name and identifier value" - {
        val res = parseMustSucced(DotAstParser.attribute_assignment , "a = b")
        "parse to the expected value" -
          assert(res == AttributeAssignment("a", "b"))
      }

      "parse attribute with name and numeric value" - {
        val res = parseMustSucced(DotAstParser.attribute_assignment , "a = 1")
        "parse to the expected value" -
          assert(res == AttributeAssignment("a", 1))
      }

    }

    "individual statement parser" - {

      "parse a node statement with no attributes" - {
        val res = parseMustSucced(DotAstParser.node_statement, "n1")
        "parse to the expected value" -
          assert(res == NodeStatement("n1", None))
      }

      "parse a node statement with one numeric attribute" - {
        val res = parseMustSucced(DotAstParser.node_statement, "n1 [a = 3]")
        "parse to the expected value" -
          assert(res == NodeStatement("n1", Some(AttributeList(Seq("a" := 3)))))
      }

      "parse a directed edge statement with no attributes" - {
        val res = parseMustSucced(DotAstParser.edge_statement, "a -> b -> c")
        "parse to the expected value" -
          assert(res == EdgeStatement("a", Seq(EdgeOp.-> -> "b", EdgeOp.-> -> "c"), None))
      }

      "fail to parse a node as an edge" - {
        val res = parseMustFail(DotAstParser.edge_statement, "n1")
      }

      "parse an attribute statement" - {
        val res = parseMustSucced(DotAstParser.attribute_statement, "graph [color = red]")
        "parse to the expected value" -
          assert(res == AttributeStatement(StatementType.Graph, AttributeList(Seq("color" := "red"))))
      }

      "parse an assignment statement" - {
        val res = parseMustSucced(DotAstParser.assignment_statement, "color = red")
        "parse to the expected value" -
          assert(res == AssignmentStatement("color", "red"))
      }

    }

    "statement_list parser" - {

      "parse the empty string" - {
        val res = parseMustSucced(DotAstParser.statement_list, "")
        "parse to the expected value" -
          assert(res == Seq[Statement]())
      }

      "parse one node statement" - {
        val res = parseMustSucced(DotAstParser.statement_list, "n1")
        "parse to the expected value" -
          assert(res == Seq(NodeStatement("n1", None) : Statement))
      }

      "parse one assignment statement" - {
        val res = parseMustSucced(DotAstParser.statement_list, "a = b")
        "parse to the expected value" -
          assert(res == Seq(AssignmentStatement("a", "b") : Statement))
      }

      "parse one assignment statement with a trailing semi-colon" - {
        val res = parseMustSucced(DotAstParser.statement_list, "a = b;")
        "parse to the expected value" -
          assert(res == Seq(AssignmentStatement("a", "b") : Statement))
      }

      "parse two assignment statements - |;|" - {
        val res = parseMustSucced(DotAstParser.statement_list, "a = b;c = d")
        "parse to the expected value" -
          assert(res == Seq(AssignmentStatement("a", "b") : Statement, AssignmentStatement("c", "d")))
      }

      "parse two assignment statements - | ;|" - {
        val res = parseMustSucced(DotAstParser.statement_list, "a = b ;c = d")
        "parse to the expected value" -
          assert(res == Seq(AssignmentStatement("a", "b") : Statement, AssignmentStatement("c", "d")))
      }

      "parse two assignment statements - |; |" - {
        val res = parseMustSucced(DotAstParser.statement_list, "a = b; c = d")
        "parse to the expected value" -
          assert(res == Seq(AssignmentStatement("a", "b") : Statement, AssignmentStatement("c", "d")))
      }

      "parse two assignment statements - | ; |" - {
        val res = parseMustSucced(DotAstParser.statement_list, "a = b ; c = d")
        "parse to the expected value" -
          assert(res == Seq(AssignmentStatement("a", "b") : Statement, AssignmentStatement("c", "d")))
      }
    }


    "subgraph parser" - {

      "parse an empty subgraph" - {
        val res = parseMustSucced(DotAstParser.subgraph, "subgraph {}")
        "parse to the expected value" -
          assert(res == Subgraph(None, Seq()))
      }

      "parse a named empty subgraph with space in body" - {
        val res = parseMustSucced(DotAstParser.subgraph, "subgraph g { }")
        "parse to the expected value" -
          assert(res == Subgraph("g"))
      }

      "parse a named empty subgraph" - {
        val res = parseMustSucced(DotAstParser.subgraph, "subgraph g {}")
        "parse to the expected value" -
          assert(res == Subgraph("g"))
      }

      "parse a subgraph with one node" - {
        val res = parseMustSucced(DotAstParser.subgraph, "subgraph { a = b }")
        "parse to the expected value" -
          assert(res == Subgraph(AssignmentStatement("a", "b")))
      }

      "parse a subgraph with two nodes" - {
        val res = parseMustSucced(DotAstParser.subgraph, "subgraph { a = b; c = d }")
        "parse to the expected value" -
          assert(res == Subgraph(AssignmentStatement("a", "b"), AssignmentStatement("c", "d")))
      }
    }


    "graph parser" - {

      "parse an empty graph" - {
        val res = parseMustSucced(DotAstParser.graph, """graph {}""")
        "parse to the expected value" -
          assert(res == Graph(
            strict = false,
            graphType = GraphType.Graph,
            id = None,
            statements = Seq()))
      }

      "parse an empty graph with leading whitespace" - {
        val res = parseMustSucced(DotAstParser.graph, """ graph {}""")
        "parse to the expected value" -
          assert(res == Graph(
            strict = false,
            graphType = GraphType.Graph,
            id = None,
            statements = Seq()))
      }

      "parse an empty named graph" - {
        val res = parseMustSucced(DotAstParser.graph, """graph g {}""")
        "parse to the expected value" -
          assert(res == Graph(
            strict = false,
            graphType = GraphType.Graph,
            id = Some(ID("g")),
            statements = Seq()))
      }

      "parse an empty digraph" - {
        val res = parseMustSucced(DotAstParser.graph, """digraph {}""")
        "parse to the expected value" -
          assert(res == Graph(
            strict = false,
            graphType = GraphType.Digraph,
            id = None,
            statements = Seq()))
      }

      "parse an empty named digraph" - {
        val res = parseMustSucced(DotAstParser.graph, """digraph g {}""")
        "parse to the expected value" -
          assert(res == Graph(
            strict = false,
            graphType = GraphType.Digraph,
            id = Some(ID("g")),
            statements = Seq()))
      }

      "parse a strict, empty graph" - {
        val res = parseMustSucced(DotAstParser.graph, """strict graph {}""")
        "parse to the expected value" -
          assert(res == Graph(
            strict = true,
            graphType = GraphType.Graph,
            id = None,
            statements = Seq()))
      }

      "parse a named, strict, empty graph" - {
        val res = parseMustSucced(DotAstParser.graph, """strict graph G {}""")
        "parse to the expected value" -
          assert(res == Graph(strict = true, GraphType.Graph, Some("G"), Seq()))
      }

      "parse a named digraph with one edge" - {
        val res = parseMustSucced(DotAstParser.graph, """
                        digraph G {
                        	start -> a0;
                        }""")
        "parse to the expected value" -
          assert(res == Graph(false, GraphType.Digraph, Some("G"), Seq(EdgeStatement("start") --> "a0")))
      }
    }

    "parse example graphs" - {

      "parse the trimmed hello world example" - {
        val res = parseMustSucced(DotAstParser.graph,
          """
            |
            | digraph g {
            |   "hello" -> "world"
            | }
            |
          """.stripMargin.trim)
      }

      "parse the cluster example" - {
        val res = parseMustSucced(DotAstParser.graph,
          """
            |digraph G {
            |
            |        	subgraph cluster_0 {
            |        		style=filled;
            |        		color=lightgrey;
            |        		node [style=filled,color=white];
            |        		a0 -> a1 -> a2 -> a3;
            |        		label = "process #1";
            |        	}
            |
            |        	subgraph cluster_1 {
            |        		node [style=filled];
            |        		b0 -> b1 -> b2 -> b3;
            |        		label = "process #2";
            |        		color=blue
            |        	}
            |        	start -> a0;
            |        	start -> b0;
            |        	a1 -> b3;
            |        	b2 -> a3;
            |        	a3 -> a0;
            |        	a3 -> end;
            |        	b3 -> end;
            |
            |        	start [shape=Mdiamond];
            |        	end [shape=Msquare];
            |        }
          """.stripMargin.trim)
      }

      "parse the genetic programming example" - {
        val res = parseMustSucced(DotAstParser.graph,
          """
            |##"This is an example from a real-world application, where we were using Genetic Programming to do simple symbolic regression. We needed a good way to visualize the trees that were being created, and it didn't take long to code up some lisp to create a dot file that visualized multiple individuals. The next step was to provide color-coding of key nodes so that we could illustrate crossover and mutation of individuals before-and-after-style. This is a sample dot file from some early debugging." Contributed by Wayne Folta.
            |
            |##Command to get the layout: "dot  -Tpng thisfile > thisfile.png"
            |
            |
            |graph ""
            |   {
            |#   node [fontsize=10,width=".2", height=".2", margin=0];
            |#   graph[fontsize=8];
            |   label="((+ (* (X) (- (- (X) (X)) (X))) (% (+ (X) (X)) (COS (- (X) (X))))) (EXP (* (X) (X))) (+ (% (EXP (SIN (+ (X) (X)))) (SIN (* (X) (EXP (* (X) (X)))))) (* (X) (X))) (% (EXP (% (X) (% (X) (X)))) (EXP (SIN (X)))))"
            |
            |   subgraph cluster01
            |   {
            |   label="(+ (* (X) (- (- (X) (X)) (X))) (% (+ (X) (X)) (COS (- (X) (X)))))"
            |   n002 ;
            |   n002 [label="+"] ;
            |   n002 -- n003 ;
            |   n003 [label="*"] ;
            |   n003 -- n004 ;
            |   n004 [label="X"] ;
            |   n003 -- n005 ;
            |   n005 [label="-"] ;
            |   n005 -- n006 ;
            |   n006 [label="-"] ;
            |   n006 -- n007 ;
            |   n007 [label="X"] ;
            |   n006 -- n008 ;
            |   n008 [label="X"] ;
            |   n005 -- n009 ;
            |   n009 [label="X"] ;
            |   n002 -- n010 ;
            |   n010 [label="%"] ;
            |   n010 -- n011 ;
            |   n011 [label="+"] ;
            |   n011 -- n012 ;
            |   n012 [label="X"] ;
            |   n011 -- n013 ;
            |   n013 [label="X"] ;
            |   n010 -- n014 ;
            |   n014 [label="COS"] ;
            |   n014 -- n015 ;
            |   n015 [label="-"] ;
            |   n015 -- n016 ;
            |   n016 [label="X"] ;
            |   n015 -- n017 ;
            |   n017 [label="X"] ;
            |   }
            |
            |   subgraph cluster17
            |   {
            |   label="(EXP (* (X) (X)))"
            |   n018 ;
            |   n018 [label="EXP"] ;
            |   n018 -- n019 ;
            |   n019 [label="*"] ;
            |   n019 -- n020 ;
            |   n020 [label="X"] ;
            |   n019 -- n021 ;
            |   n021 [label="X"] ;
            |   }
            |
            |   subgraph cluster21
            |   {
            |   label="(+ (% (EXP (SIN (+ (X) (X)))) (SIN (* (X) (EXP (* (X) (X)))))) (* (X) (X)))"
            |   n022 ;
            |   n022 [label="+"] ;
            |   n022 -- n023 ;
            |   n023 [label="%"] ;
            |   n023 -- n024 ;
            |   n024 [label="EXP"] ;
            |   n024 -- n025 ;
            |   n025 [label="SIN"] ;
            |   n025 -- n026 ;
            |   n026 [label="+"] ;
            |   n026 -- n027 ;
            |   n027 [label="X"] ;
            |   n026 -- n028 ;
            |   n028 [label="X"] ;
            |   n023 -- n029 ;
            |   n029 [label="SIN"] ;
            |   n029 -- n030 ;
            |   n030 [label="*"] ;
            |   n030 -- n031 ;
            |   n031 [label="X"] ;
            |   n030 -- n032 ;
            |   n032 [label="EXP"] ;
            |   n032 -- n033 ;
            |   n033 [label="*"] ;
            |   n033 -- n034 ;
            |   n034 [label="X"] ;
            |   n033 -- n035 ;
            |   n035 [label="X"] ;
            |   n022 -- n036 ;
            |   n036 [label="*"] ;
            |   n036 -- n037 ;
            |   n037 [label="X"] ;
            |   n036 -- n038 ;
            |   n038 [label="X"] ;
            |   }
            |
            |   subgraph cluster38
            |   {
            |   label="(% (EXP (% (X) (% (X) (X)))) (EXP (SIN (X))))"
            |   n039 ;
            |   n039 [label="%"] ;
            |   n039 -- n040 ;
            |   n040 [label="EXP"] ;
            |   n040 -- n041 ;
            |   n041 [label="%"] ;
            |   n041 -- n042 ;
            |   n042 [label="X"] ;
            |   n041 -- n043 ;
            |   n043 [label="%"] ;
            |   n043 -- n044 ;
            |   n044 [label="X"] ;
            |   n043 -- n045 ;
            |   n045 [label="X"] ;
            |   n039 -- n046 ;
            |   n046 [label="EXP"] ;
            |   n046 -- n047 ;
            |   n047 [label="SIN"] ;
            |   n047 -- n048 ;
            |   n048 [label="X"] ;
            |   }
            |   }
            |
          """.stripMargin.trim)
      }
    }
  }
}

