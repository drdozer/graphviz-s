package uk.co.turingatemyhamster.graphvizs

import java.io.File

/**
 *
 *
 * @author Matthew Pocock
 */
package object exec extends Exec {

  /** The platform-, system-dependendent location of the dot binary. */

  var dotBinary: File = new File("/usr/bin/dot")

}
