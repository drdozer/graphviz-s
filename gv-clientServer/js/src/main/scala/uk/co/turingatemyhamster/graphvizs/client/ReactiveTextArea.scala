package uk.co.turingatemyhamster.graphvizs.client

import org.scalajs.dom.{Event, HTMLTextAreaElement}
import rx.core.Var

import scalatags.JsDom.TypedTag
import scalatags.ext.Framework._

case class ReactiveTextArea(textArea: HTMLTextAreaElement) {
  // fixme: lazify event Rx-s
  val value = Var(textArea.value)

  def updateValue(e: Event) = {
      value() = textArea.value
    }
  textArea.modifyWith(Events.input := updateValue _).render
}

object ReactiveTextArea {
  def apply(textArea: TypedTag[HTMLTextAreaElement]): ReactiveTextArea = ReactiveTextArea(textArea.render)
}