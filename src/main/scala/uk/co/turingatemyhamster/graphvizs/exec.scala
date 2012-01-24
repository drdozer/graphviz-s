package uk.co.turingatemyhamster.graphvizs

import exec.{DotOpts, DotApp, DotFormat, DotLayout}
import io.Source
import java.io._
import sys.process.{ProcessIO, Process}

/**
 * Dot application execution.
 * 
 * @author Matthew Pocock
 */

package object exec extends Exec {
  

}

trait Exec {

  var dotBinary: File = new File("dot")

  def dot2dot(dot: String): String = {

    val app = DotApp(dotBinary, DotOpts(Some(DotLayout.dot), Option(DotFormat.dot)))

    var out: String = _

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

}