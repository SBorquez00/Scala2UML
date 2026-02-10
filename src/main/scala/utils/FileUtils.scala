package scalauml
package utils

import java.io.File

object FileUtils {

  def listAllFiles(directory: File): List[File] = {
    val content = directory.listFiles()

    if(content == null){
      List()
    } else{
      content.toList.flatMap{ file =>
        if(file.isFile){
          List(file)
        } else if (file.isDirectory){
          listAllFiles(file)
        } else{
          List()
        }
      }
    }
  }
}
