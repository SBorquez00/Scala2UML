package scalauml
package utils

import io.circe.Json
import ujson.Value

object JsonInterop {
  def circeToUjson(json: Json): Value = {
    json.fold(
      jsonNull = ujson.Null,
      jsonBoolean = ujson.Bool(_),
      jsonNumber = num => num.toBigDecimal match {
        case Some(bd) => ujson.Num(bd.toDouble)
        case None => throw new Exception("Invalid number")
      },
      jsonString = ujson.Str(_),
      jsonArray = arr => ujson.Arr(arr.map(circeToUjson): _*),
      jsonObject = obj => ujson.Obj.from(obj.toMap.map { case (k, v) => (k, circeToUjson(v)) })
    )
  }
}
