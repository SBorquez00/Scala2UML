class Usuario(val nombre: String, val correo: Option[String], val telefonos: List[String], val preferencias: Map[String, String]) {

  val empresa: Option[String] = obtenerEmpresa(nombre);

  def mostrarResumen(): String = {
    //tabla con toda la info
    ???
  }
}
