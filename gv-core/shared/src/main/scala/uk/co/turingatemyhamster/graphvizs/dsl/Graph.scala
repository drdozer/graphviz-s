package uk.co.turingatemyhamster.graphvizs.dsl

/**
 * The Graph datatype.
 *
 * @author Matthew Pocock
 */

case class Graph(strict: Boolean, graphType: GraphType, id: Option[ID], statements: Seq[Statement])

sealed trait GraphType
object GraphType {
  case object Graph extends GraphType
  case object Digraph extends GraphType
}


sealed trait Statement

case class NodeStatement(node: NodeId, attributes: Option[AttributeList] = None) extends Statement

case class EdgeStatement(node: Node, nodes: Seq[(EdgeOp, Node)], attributes: Option[AttributeList] = None) extends Statement {
  def --> (n: Node): EdgeStatement = copy(nodes = nodes :+ (EdgeOp.->, n))
  def --- (n: Node): EdgeStatement = copy(nodes = nodes :+ (EdgeOp.--, n))
}

sealed trait EdgeOp
object EdgeOp {
  case object -> extends EdgeOp
  case object -- extends EdgeOp
}


sealed trait Node

case class NodeId(id: ID,  port: Option[Port]) extends Node

case class Port(id: Option[ID], compassPt: Option[CompassPt]) // can't both be None

sealed trait CompassPt
object CompassPt {
  case object N extends CompassPt
  case object NE extends CompassPt
  case object E extends CompassPt
  case object SE extends CompassPt
  case object S extends CompassPt
  case object SW extends CompassPt
  case object W extends CompassPt
  case object NW extends CompassPt
  case object C extends CompassPt
  case class or(id: ID) extends CompassPt
}

case class AttributeStatement(statementType: StatementType, attributeList: AttributeList) extends Statement

case class AttributeList(attrs: Seq[AttributeAssignment] = Seq(), next: Option[AttributeList] = None) {
  def ++ (ass: Seq[AttributeAssignment]) = copy(attrs = attrs ++ ass)
}

sealed trait StatementType
object StatementType {
  case object Graph extends StatementType
  case object Node extends StatementType
  case object Edge extends StatementType
}


sealed trait AttributeAssignment {
  def name: ID
  def value: Option[ID]   // value=None ~> value=Some(true)  
}

object AttributeAssignment {
  def apply(name: ID): AttributeAssignment = AnyAttributeAssignment(name, None)
  def apply(name: ID, value: Option[ID]): AttributeAssignment = AnyAttributeAssignment(name, value)
  def apply(name: ID, value: ID): AttributeAssignment = AnyAttributeAssignment(name, Some(value))
  
  case class AnyAttributeAssignment(name: ID, value: Option[ID]) extends AttributeAssignment
  
  abstract class DoubleAttribute extends AttributeAssignment {
    def doubleValue: Double
    def value: Option[ID] = Some(ID.Numeral(doubleValue))
  }
  
  abstract class StringAttribute extends AttributeAssignment {
    def stringValue: String
    def value: Option[ID] = Some(ID.Identifier(stringValue))
  }
  
  case class Damping(doubleValue: Double) extends DoubleAttribute { def name: ID = "Damping" } // G
  case class K(doubleValue: Double)       extends DoubleAttribute { def name: ID = "K" }       // GC
  case class Url(stringValue: String)     extends StringAttribute { def name: ID = "URL" }     // ENGC
  case class Area(doubleValue: Double)    extends DoubleAttribute { def name: ID = "area" }    // NC
  //case class Arrowhead(
}


case class AssignmentStatement(name: ID,  value: ID) extends Statement

case class Subgraph(id: Option[ID], statements: Seq[Statement]) extends Statement with Node {
  def addStatements(stmts: Seq[Statement]) = copy(statements = statements ++ stmts)
}

object Subgraph {
  def apply(name: ID, aa: AssignmentStatement*): Subgraph = Subgraph(Some(name), aa)
  def apply(name: ID): Subgraph = Subgraph(Some(name), Seq())
  def apply(aa: AssignmentStatement*): Subgraph = Subgraph(None, aa)
}


sealed trait ID
object ID {
  case class Identifier(value: String) extends ID // Any string of alphabetic ([a-zA-Z'200-'377]) characters, underscores ('_') or digits ([0-9]), not beginning with a digit
  case class Numeral(value: Double) extends ID // a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? );
  case class Quoted(value: String) extends ID // any double-quoted string ("...") possibly containing escaped quotes ('")
  //case class Html(value: Elem) extends ID // an HTML block

  val IdRx = """([\w&&[\D]][\w]*)""".r
  val NumeralRx = """-?((\.\d+)|(\d+(\.\d*)?))""".r

  def apply(id: String): ID = {
    id match {
      case IdRx(s) =>
        Identifier(s)
      case s =>
        Quoted(s.replace("\n", """\n"""))
    }
  }
}