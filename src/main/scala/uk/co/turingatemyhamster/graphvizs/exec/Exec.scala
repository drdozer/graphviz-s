package uk.co.turingatemyhamster.graphvizs
package exec

import io.Source
import sys.process.ProcessIO
import uk.co.turingatemyhamster.graphvizs.dsl.Graph
import java.io._

/**
 * Dot binary executor.
 *
 * Usually, you will use this through the exec package object, configured with the system-global dot binary location.
 * You will use this trait directly if you want to maintain multiple dot facades.
 *
 * @author Matthew Pocock
 */

trait Exec {

  /**
   * Location of the dot binary.
   *
   * @return a File pointing to the dot binary
   */
  def dotBinary: File

  /**
   * Run dot in dot2dot mode. This will produce a dot with layout information added.
   *
   * @param dot the dot input text
   * @return  dot output text with layout added
   */
  def dot2dotS(dot: String): String = {

    val app = DotApp(dotBinary, DotOpts(Some(DotLayout.dot), Option(DotFormat.dot)))

    var out: String = ""

    def inHandler(os: OutputStream) {
      val pw = new PrintWriter(os)
      pw.write(dot)
      pw.close()
    }

    def outHandler(is: InputStream) {
      out = Source.fromInputStream(is).mkString
      is.close()
    }

    def errHandler(es: InputStream) {
      es.close() // ignore it
    }

    val io = new ProcessIO(inHandler, outHandler, errHandler, false)

    val proc = app.process run io
    proc.exitValue() match {
      case 0 => out
      case x => sys.error("Dot exited with error code: " + x)
    }

  }


  def dot2dotG(g: Graph): Graph = {

    val app = DotApp(dotBinary, DotOpts(Some(DotLayout.dot), Option(DotFormat.dot)))

    var gOut: Graph = null

    def inHandler(os: OutputStream) {
      val pw = new PrintWriter(os)
      dsl.renderGraph(g, pw)
      pw.close()
    }

    def outHandler(is: InputStream) {
      gOut = dsl.parseAsGraph(new InputStreamReader(is))
    }

    def errHandler(es: InputStream) {
      es.close() // ignore it
    }

    val io = new ProcessIO(inHandler, outHandler, errHandler, false)

    val proc = app.process run io
    proc.exitValue() match {
      case 0 => gOut
      case x => sys.error("Dot exited with error code: " + x)
    }

  }
}