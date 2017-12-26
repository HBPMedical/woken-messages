/*
 * Copyright 2017 Human Brain Project MIP by LREN CHUV
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hbp.mip.woken.messages.external

import spray.json._

object ExternalAPIProtocol extends DefaultJsonProtocol {

  case class CodeValue(code: String, value: String) {
    def toTuple: (String, String) = (code, value)
  }
  object CodeValue {
    def fromTuple(t: (String, String)) = CodeValue(t._1, t._2)
  }

  implicit val CodeValueJsonFormat: JsonFormat[CodeValue] = jsonFormat2(CodeValue.apply)

  implicit val VariableIdJsonFormat: JsonFormat[VariableId] = jsonFormat1(VariableId)

  private def paramMap(value: JsValue): Map[String, String] =
    value.asJsObject
      .fields("parameters")
      .convertTo[List[CodeValue]]
      .map(_.toTuple)
      .toMap

  implicit object AlgorithmSpecJsonFormat extends JsonFormat[AlgorithmSpec] {
    def write(a: AlgorithmSpec): JsValue =
      JsObject(
        "code"       -> JsString(a.code),
        "parameters" -> a.parameters.map(CodeValue.fromTuple).toJson
      )

    def read(value: JsValue): AlgorithmSpec =
      AlgorithmSpec(value.asJsObject.fields("code").convertTo[String], paramMap(value))
  }

  implicit object AlgorithmJsonFormat extends JsonFormat[Algorithm] {
    def write(a: Algorithm): JsValue =
      JsObject(
        "code"       -> JsString(a.code),
        "name"       -> JsString(a.name),
        "parameters" -> a.parameters.map(CodeValue.fromTuple).toJson
      )

    def read(value: JsValue): Algorithm =
      Algorithm(value.asJsObject.fields("code").convertTo[String],
                value.asJsObject.fields("name").convertTo[String],
                paramMap(value))
  }

  implicit object ValidationSpecJsonFormat extends JsonFormat[ValidationSpec] {
    def write(v: ValidationSpec): JsValue =
      JsObject(
        "code"       -> JsString(v.code),
        "parameters" -> v.parameters.map(CodeValue.fromTuple).toJson
      )

    def read(value: JsValue): ValidationSpec =
      ValidationSpec(value.asJsObject.fields("code").convertTo[String], paramMap(value))
  }

  implicit object ValidationJsonFormat extends JsonFormat[Validation] {
    def write(v: Validation): JsValue =
      JsObject(
        "code"       -> JsString(v.code),
        "name"       -> JsString(v.name),
        "parameters" -> v.parameters.map(CodeValue.fromTuple).toJson
      )

    def read(value: JsValue): Validation =
      Validation(value.asJsObject.fields("code").convertTo[String],
                 value.asJsObject.fields("name").convertTo[String],
                 paramMap(value))
  }

  def jsonEnum[T <: Enumeration](enu: T): JsonFormat[T#Value] = new JsonFormat[T#Value] {
    def write(obj: T#Value) = JsString(obj.toString)

    def read(json: JsValue): enu.Value = json match {
      case JsString(txt) => enu.withName(txt)
      case something =>
        deserializationError(s"Expected a value from enum $enu instead of $something")
    }
  }

  implicit val OperatorsJsonFormat: JsonFormat[Operators.Value] = jsonEnum(Operators)

}