package scalauml
package models

sealed trait Handle {
  def value: String
}

object Handle{
  case object Top1 extends Handle {
    val value = "top-handle-1"
  }

  case object Top2 extends Handle {
    val value = "top-handle-2"
  }

  case object Top3 extends Handle {
    val value = "top-handle-3"
  }

  case object Bottom1 extends Handle {
    val value = "bottom-handle-1"
  }

  case object Bottom2 extends Handle {
    val value = "bottom-handle-2"
  }

  case object Bottom3 extends Handle {
    val value = "bottom-handle-3"
  }

  case object Right1 extends Handle {
    val value = "right-handle-1"
  }

  case object Right2 extends Handle {
    val value = "right-handle-2"
  }

  case object Right3 extends Handle {
    val value = "right-handle-3"
  }

  case object Left1 extends Handle {
    val value = "left-handle-1"
  }

  case object Left2 extends Handle {
    val value = "left-handle-2"
  }

  case object Left3 extends Handle {
    val value = "left-handle-3"
  }
}
