package uk.co.turingatemyhamster.graphvizs.dsl

import scala.util.parsing.combinator.RegexParsers
import scala.util.matching.Regex

/**
 * Constructors defining the DOT data structure.
 *
 * @author Matthew Pocock
 */

trait DotConstructors extends Dot {

  // handlers

  def handle_graph(strict: Boolean, graphType: T_GraphType, id: Option[T_ID], statements: Seq[T_Statement]): T_Graph

  def handle_nodeStatement(node: T_NodeId, attributes: Option[T_AttributeList]): T_NodeStatement
  def handle_edgeStatement(node: T_Node, nodes: Seq[(T_EdgeOp, T_Node)], attributes: Option[T_AttributeList]): T_EdgeStatement
  def handle_attributeStatement(statementType: T_StatementType, attributes: T_AttributeList): T_AttributeStatement
  def handle_assignmentStatement(name: T_ID, value: T_ID): T_AssignmentStatement

  def handle_nodeId(id: T_ID, port: Option[T_Port]): T_NodeId
  def handle_port(id: Option[T_ID], compassPt: Option[T_CompassPt]): T_Port
  def handle_attributeList(attrs: Seq[T_AttributeAssignment], next: Option[T_AttributeList]): T_AttributeList
  def handle_attributeAssignment(name: T_ID, value: Option[T_ID]): T_AttributeAssignment
  def handle_subgraph(id: Option[T_ID], statements: Seq[T_Statement]): T_Subgraph
}

/**
 * Parser for the DOT file format.
 *
 * @author Matthew Pocock
 */
trait DotParser extends DotConstructors with RegexParsers {

  // symbol literals
  val l_bracket: Parser[String] = "{"
  val r_bracket: Parser[String] = "}"
  val l_box: Parser[String] = "["
  val r_box: Parser[String] = "]"
  val colon: Parser[String] = ":"
  val semi_colon: Parser[String] = ";"
  val comma: Parser[String] = ","
  val equ: Parser[String] = "="
  val hash: Parser[String] = "#"
  val notNewline: Parser[String] = """[^\n]""".r

  val directed_edge: Parser[String] = "->"
  val undirected_edge: Parser[String] = "--"
  
  // keywords
  val STRICT: Parser[String] = "(?i)strict".r
  val GRAPH: Parser[String] = "(?i)graph".r
  val DIGRAPH: Parser[String] = "(?i)digraph".r
  val NODE: Parser[String] = "(?i)node".r
  val EDGE: Parser[String] = "(?i)edge".r
  val SUBGRAPH: Parser[String] = "(?i)subgraph".r

  override protected val whiteSpace = """\s*((#[^\n]*)?\s+)+""".r

  // identifier types
  val identifier: Parser[String] = ID.IdRx
  val numeral: Parser[String] = ID.NumeralRx
  val dblquoted: Parser[String] = """"([^"]|(\\"))*"""".r -> 1

  // compass points
  val n:  Parser[String] = "(?i)n".r
  val ne: Parser[String] = "(?i)ne".r
  val e:  Parser[String] = "(?i)e".r
  val se: Parser[String] = "(?i)se".r
  val s:  Parser[String] = "(?i)s".r
  val sw: Parser[String] = "(?i)sw".r
  val w:  Parser[String] = "(?i)w".r
  val nw: Parser[String] = "(?i)nw".r
  val c:  Parser[String] = "(?i)c".r

  // productions
  lazy val graph: Parser[T_Graph] = STRICT.? ~ graph_type ~ id.? ~ l_bracket ~ statement_list ~ r_bracket ^^ {
    case (str ~ gt ~ id ~ _ ~ stmts ~ _) => handle_graph(str.isDefined, gt, id, stmts)
  }
  
  lazy val statement_list: Parser[Seq[T_Statement]] = (statement <~ semi_colon.?).*

  lazy val statement: Parser[T_Statement]
  = assignment_statement |
    attribute_statement |
    subgraph |
    edge_statement |
    node_statement

  lazy val attribute_statement: Parser[T_AttributeStatement] = statement_type ~ attribute_list ^^ {
    case (st ~ al) => handle_attributeStatement(st, al)
  }

  lazy val attribute_list: Parser[T_AttributeList] =
  l_box ~ attributes ~ r_box ~ attribute_list.? ^^ { case (_ ~ al1 ~ _ ~ nxt) => handle_attributeList(al1, nxt) }
  
  lazy val attributes: Parser[Seq[T_AttributeAssignment]] = repsep(attribute_assignment, comma)

  lazy val attribute_assignment: Parser[T_AttributeAssignment] = id ~ (equ ~> id).? ^^ {
    case name ~ value => handle_attributeAssignment(name, value)
  }

  lazy val edge_statement: Parser[T_EdgeStatement] = node ~ edge_rhs ~ attribute_list.? ^^
    { case n ~ ns ~ as => handle_edgeStatement(n, ns, as) }
  
  lazy val edge_rhs: Parser[Seq[(T_EdgeOp, T_Node)]] = ((edge_op ~ node) ^^ { case eo ~ n => (eo, n) }).+

  lazy val node_statement: Parser[T_NodeStatement] = node_id ~ attribute_list.? ^^ { case id ~ as => handle_nodeStatement(id, as) }
  
  lazy val node_id: Parser[T_NodeId] = id ~ port.? ^^ { case id ~ port => handle_nodeId(id, port) }

  lazy val node: Parser[T_Node] = node_id | subgraph
  
  lazy val port: Parser[T_Port]
  = (colon ~> id  ~ ( colon ~> compass_pt).? ^^ { case id ~ cp => handle_port(Some(id), cp) }) |
    (colon ~> compass_pt ^^ { case cp => handle_port(None, Some(cp)) } )
  
  lazy val subgraph: Parser[T_Subgraph] = SUBGRAPH ~ id.? ~ l_bracket ~ statement_list ~ r_bracket ^^
    { case _ ~ id ~ _ ~ ss ~ _ => handle_subgraph(id, ss) }
  
  lazy val assignment_statement: Parser[T_AssignmentStatement] = id ~ equ ~ id ^^
    { case name ~ _ ~ value => handle_assignmentStatement(name, value) }

  def id: Parser[T_ID] // (identifier | numeral | quoted_string | html)
  def statement_type: Parser[T_StatementType] // (GRAPH | NODE | EDGE)
  def graph_type: Parser[T_GraphType] // (GRAPH | DIGRAPH)
  def edge_op: Parser[T_EdgeOp] // (directed_edge | undirected_edge)
  def compass_pt: Parser[T_CompassPt] // (n | ne | e | se | s | sw | w | nw | id)

  private def noneOnEmpty[T](ts: Seq[T]): Option[Seq[T]] = if(ts.isEmpty) None else Some(ts)

  implicit def regex(rg: (Regex, Int)): Parser[String] = new Parser[String] {
    def apply(in: Input) = {
      val (r, g) = rg
      val source = in.source
      val offset = in.offset
      val start = handleWhiteSpace(source, offset)
      (r findPrefixMatchOf (source.subSequence(start, source.length))) match {
        case Some(matched) =>
          Success(matched.group(g),
                  in.drop(start + matched.end - offset))
        case None =>
          val found = if (start == source.length()) "end of source" else "`"+source.charAt(start)+"'"
          Failure("string matching regex `"+r+"' expected but "+found+" found", in.drop(start - offset))
      }
    }
  }

}