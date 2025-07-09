trait SerVivo {

  def energia: Int

  def energia_(valor: Int): Unit

  def alimentarse(): Unit

  def usarEnergia(cantidad: Int): Unit = {
    energia -= cantidad
  }

}