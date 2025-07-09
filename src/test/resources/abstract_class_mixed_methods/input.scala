abstract class Vehiculo(protected val numeroRuedas: Int, protected val patente: String){

  def andar(): Unit

  def getPatente(): String = {
    patente
  }

  def showPatente(): Unit = {
    print(patente)
  }

}