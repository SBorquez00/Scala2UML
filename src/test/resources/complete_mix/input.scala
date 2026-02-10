object X {
  trait SerVivo {
    var name: String

    def alimentar(): Unit
  }

  class Animal(val Nombre: String, val Edad: Int) extends SerVivo {
    protected var peso: Double
    val especie: String

    def alimentar(): Unit = {
      ???
    }

    private def truco(orden: String, premio: String, tiempo: Double): Boolean = {
      ???
    }

    def isIn(ranchos: List[Rancho]): Boolean = {
      ???
    }
  }

  class Perro() extends Animal {
    private val chipCode: String

    def ladrar(): Unit = {
      ???
    }

    def validate(scanedCode: String): Boolean = {
      ???
    }

  }

  class Vaca() extends Animal {
    def ordeñar(): Unit = {
      ???
    }
  }

  class Rancho(val Nombre: String, val residentes: Map[Int, Animal]) {
    private val xCod = "2.34"
    private val yCod = "-132.4"

    def GetUbicacion(): (String, String) = {
      ???
    }
  }

}
