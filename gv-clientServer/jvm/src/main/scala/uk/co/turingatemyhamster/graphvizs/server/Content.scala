package uk.co.turingatemyhamster.graphvizs.server

/**
 *
 *
 * @author Matthew Pocock
 */
object Content {
  def editor = {
    import scalatags.Text.all._

    html(
      head(
        "title".tag[String] apply ("Graphviz Interactive Editor")
      ),
      body(
        div(id := "topBar"),
        h1("Graphviz Interactive Editor"),
        div(
          p("Edit your graphviz dot text here"),
          textarea(id := "dotTextArea", rows := 20, cols :=80)
        ),
        div(
          p("SVG rendering"),
          div(id := "dotSvg")
        ),
        div(
          p("Dot rendered to SVG appears here"),
          textarea(id := "renderedDot", rows := 20, cols :=80, readonly := true)
        ),
        script(
          """
            |Graphviz().wire("topBar", "dotTextArea", "dotSvg")
          """.stripMargin)
      )
    )
  }
}
