class Curso(nombre: String, horas: Int){

  var profesor: String;

  def asignarProfesor(profAsignado: String): Unit = {
    profesor = profAsignado;
  }

}

class Estudiante(nombre: String, cursos: List[Curso]){
  def estudiar(): Unit = {
    ???
  }
}