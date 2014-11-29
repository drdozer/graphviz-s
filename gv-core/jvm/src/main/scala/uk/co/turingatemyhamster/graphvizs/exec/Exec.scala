package uk.co.turingatemyhamster.graphvizs
package exec

import io.Source
import sys.process.ProcessIO
import uk.co.turingatemyhamster.graphvizs.dsl.Graph
import java.io._
import annotation.implicitNotFound
import javax.xml.parsers.SAXParserFactory

/**
 * Dot binary executor.
 *
 * Usually, you will use this through the exec package object, configured with the system-global dot binary location.
 * You will use this trait directly if you want to maintain multiple dot facades.
 *
 * @author Matthew Pocock
 */

trait Exec extends GraphHandlers with StringHandlers with FileHandlers {

  /**
   * Location of the dot binary.
   *
   * @return a File pointing to the dot binary
   */
  def dotBinary: File

  /**
   * Run the input through dot and acquire the output.
   *
   * @param from        the input value
   * @param inputFrom   implicit handler to present `from` to the stdin of dot
   * @param outputTo    implicit handler to process the stdout of dot into a `To`
   * @tparam From       the input type
   * @tparam To         the output type
   * @return            a `To` representing the output of dot
   */
  def dot2dot[From, To](from: From, format: DotFormat = DotFormat.dot)
                                       (implicit inputFrom: InputHandler[From], outputTo: OutputHandler[To]): To =
  {
    val app = DotApp(dotBinary, outputTo.processOpts(DotOpts(Some(DotLayout.dot), Some(format))))

    val errHandler = stringOutputHandler

    val io = new ProcessIO(inputFrom.handle(from), outputTo.handle, errHandler.handle, false)

    val proc = app.process run io
    proc.exitValue() match {
      case 0 => outputTo.value
      case x => sys.error("Dot exited with error code: " + x + " with output:\n" + errHandler.value)
    }
  }
}

@implicitNotFound("Unable to find input handler for ${A}")
trait InputHandler[A] {
  def handle(a: A)(input: OutputStream)
}

@implicitNotFound("Unable to find output handler that can convert the output to ${A}")
trait OutputHandler[A] {
  def processOpts(opts: DotOpts): DotOpts = opts
  def handle(output: InputStream)
  def value: A
}

trait StringHandlers {

  implicit object StringInputHandler extends InputHandler[String] {
    def handle(a: String)(os: OutputStream) {
      val pw = new PrintWriter(os)
      pw write a
      pw.close()
    }
  }

  implicit def stringOutputHandler: OutputHandler[String] = new OutputHandler[String] {
    var value: String = null

    def handle(out: InputStream) {
      value = Source.fromInputStream(out).mkString
      out.close()
    }
  }
  
}

trait GraphHandlers {
  
  implicit object GraphInputHandler extends InputHandler[Graph] {
    def handle(g: Graph)(os: OutputStream) {
      val pw = new PrintWriter(os)
      dsl.renderGraph(g, pw)
      pw.close()
    }
  }

  implicit def graphOutputHandler: OutputHandler[Graph] = new OutputHandler[Graph] {
    var value: Graph = null

    def handle(out: InputStream) {
      value = dsl.parseAsGraph(new InputStreamReader(out))
      out.close()
    }
  }
  
}

//trait XmlHandlers {
//
//  implicit def xmlOutputHandler: OutputHandler[Elem] = new OutputHandler[Elem] {
//    var value: Elem = null
//
//    val f = SAXParserFactory.newInstance()
//    f.setValidating(false)
//    f.setFeature("http://xml.org/sax/features/validation", false)
//    f.setFeature("http://apache.org/xml/features/validation/dynamic", false)
//    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
//
//    def handle(out: InputStream) {
//      val parser = f.newSAXParser()
//      value = XML.withSAXParser(parser).load(out)
//    }
//  }
//
//}

trait FileHandlers {
  implicit def fileOutputHandler: OutputHandler[File] = new OutputHandler[File] {

    var value: File = null

    override def processOpts(opts: DotOpts) = {
      value = File.createTempFile("dot_", "." + opts.format.flatMap(_.format.headOption).getOrElse("dot_out"))
      opts.copy(outFile = Some(value))
    }

    def handle(output: InputStream) = {}
  }
}
