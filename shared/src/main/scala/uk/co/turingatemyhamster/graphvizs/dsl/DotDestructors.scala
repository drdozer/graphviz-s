package uk.co.turingatemyhamster.graphvizs.dsl

trait DotDestructors extends Dot {

  // decomposers

  def decompose_graph(g: T_Graph): (Boolean, T_GraphType, Option[T_ID], Seq[T_Statement])
  def decompose_nodeStatement(ns: T_NodeStatement): (T_NodeId, Option[T_AttributeList])
  def decompose_edgeStatement(es: T_EdgeStatement): (T_Node, Seq[(T_EdgeOp, T_Node)], Option[T_AttributeList])
  def decompose_attributeStatement(as: T_AttributeStatement): (T_StatementType, T_AttributeList)
  def decompose_assignmentStatement(as: T_AssignmentStatement): (T_ID, T_ID)
  def decompose_nodeId(nid: T_NodeId): (T_ID, Option[T_Port])
  def decompose_port(p: T_Port): (Option[T_ID], Option[T_CompassPt])
  def decompose_attributeList(al: T_AttributeList): (Seq[T_AttributeAssignment], Option[T_AttributeList])
  def decompose_attributeAssignment(as: T_AttributeAssignment): (T_ID, Option[T_ID])
  def decompose_subgraph(sg: T_Subgraph): (Option[T_ID], Seq[T_Statement])
}

trait DotRenderer extends DotDestructors {

  @inline private def space = out append " "
  @inline private def nl = out append "\n"

  def out: Appendable

  def render_graph(g: T_Graph) {
    val (strict, graphType, id, statements) = decompose_graph(g)

    if(strict) {
      out append "strict "
    }
    render_graphType(graphType)
    space
    id foreach (x => {
      render_id(x)
      space
    })
    out append "{"
    if(! statements.isEmpty) {
      nl
      for(s <- statements) {
        render_statement(s)
        nl
      }
    }
    out append "}"
    nl
  }

  def render_attributeList(al: T_AttributeList) {
    val (attrs, next) = decompose_attributeList(al)

    if(attrs.nonEmpty) {
      out append " ["
      for(a <- attrs) {
        space
        render_attributeAssignment(a)
      }
      out append " ]"
      for(n <- next) {
        space
        render_attributeList(n)
      }
    }
  }

  def render_nodeStatement(ns: T_NodeStatement) {
    val (node, attributes) = decompose_nodeStatement(ns)

    render_nodeId(node)
    for(as <- attributes) {
      render_attributeList(as)
    }
  }

  def render_edgeStatement(es: T_EdgeStatement) {
    val (node, nodes, attributes) = decompose_edgeStatement(es)

    render_node(node)
    for((op, n) <- nodes) {
      space
      render_edgeOp(op)
      space
      render_node(n)
    }
    attributes foreach render_attributeList
  }

  def render_attributeStatement(as: T_AttributeStatement) {
    val (statementType, attributes) = decompose_attributeStatement(as)

    render_statementType(statementType)
    render_attributeList(attributes)
  }
  
  def render_assignmentStatement(as: T_AssignmentStatement) {
    val (name, value) = decompose_assignmentStatement(as)
    
    render_id(name)
    out append " = "
    render_id(value)
  }

  def render_nodeId(nid: T_NodeId) {
    val (id, port) = decompose_nodeId(nid)

    render_id(id)
    for(p <- port) {
      space
      render_port(p)
    }
  }

  def render_port(p: T_Port) {
    val (id, compass_pt) = decompose_port(p)
    
    out append " : "
    for(i <- id) {
      render_id(i)
      out append " : "
    }
    for(cp <- compass_pt) {
      render_compass_pt(cp)
    }

  }

  def render_attributeAssignment(as: T_AttributeAssignment) {
    val (name, value) = decompose_attributeAssignment(as)

    render_id(name)
    for(v <- value) {
      out append " = "
      render_id(v)
    }
  }

  def render_statementList(statements: scala.Seq[T_Statement]) {
    for (s <- statements) {
      render_statement(s)
      nl
    }
  }

  def render_subgraph(sg: T_Subgraph) {
    val (id, statements) = decompose_subgraph(sg)

    out append "subgraph"
    for(i <- id) {
      space
      render_id(i)
    }
    space
    out append "{"
    if(! statements.isEmpty) {
      nl
      render_statementList(statements)
    }
    out append "}"
    nl
  }

  def render_graphType(gt: T_GraphType)
  def render_id(id: T_ID)
  def render_statement(st: T_Statement)
  def render_node(n: T_Node)
  def render_edgeOp(eo: T_EdgeOp)
  def render_statementType(st: T_StatementType)
  def render_compass_pt(cp: T_CompassPt)
}
