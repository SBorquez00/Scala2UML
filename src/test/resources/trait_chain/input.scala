trait SerVivo{
  val nombre: String;

  def usaEnergia(cantidad: Int): Unit
}

trait Animal extends SerVivo {
  val esCarnivoro: Boolean;

  def moverse(distancia: Double): Unit
}