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
        "title".tag[String] apply ("Graphviz Interactive Editor"),
        script(src := "/gv-clientserver-fastopt.js")
      ),
      body(
        div(id := "topBar"),
        h1("Graphviz Interactive Editor"),
        div(
          p("Edit your graphviz dot text here"),
          textarea(id := "dotTextArea", rows := 20, cols :=80),
          select(id := "layouts")
        ),
        div(
          p("SVG rendering"),
          iframe(id := "dotSvg", width := 800, height := 600)
        ),
        div(
          p("Dot rendered to SVG appears here"),
          textarea(id := "renderedDot", rows := 20, cols :=80, readonly := true)
        ),
        script(
          """
            |Graphviz().wire(
            |   document.getElementById('dotTextArea'),
            |   document.getElementById('layouts'),
            |   document.getElementById('dotSvg'),
            |   document.getElementById('renderedDot'))
          """.stripMargin)
      )
    )
  }
}
