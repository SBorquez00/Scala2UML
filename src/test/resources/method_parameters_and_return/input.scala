class Inventario (participantes: List[String], totalParticipantes: Int, private val totalEstimado: Int, protected val sucursal: String){
  val encargado = "Jefe"

  var total = 0

  var horaTermino: String = "07:00"

  def contarUno(producto: String, participante: String): Unit = {
    ???
  }

  def contarVarios(producto: String, participante: String, cantidad: Int): Unit = {
    ???
  }

  def obtenerDesempeño(participante: String): Int = {
    ???
  }

  def getHoraTermino() = {
    horaTermino
  }

}