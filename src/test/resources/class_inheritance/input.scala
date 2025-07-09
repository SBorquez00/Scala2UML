class Vehiculo(val tipo: String, marca: String){
  def andar(): Unit = {
    ???
  }
}

class Camion(marca: String, modelo: String, kmXLitro: Double, dificultad: Int) extends Vehiculo("Camion", marca){

  def calcularGasPorViaje(kmAViajar: Double): Double = {
    ???
  }

}

class CamionConAcoplado(marca: String, modelo: String, kmXLitro: Double, tamañoAcoplado: Double) extends Camion(marca, modelo, kmXLitro, 10){

  def tamañoTotal(tamañoCabina: Double): Double = {
    ???
  }

}