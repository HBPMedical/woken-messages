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

package eu.hbp.mip.woken.messages.variables

import eu.hbp.mip.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import spray.json._
import variablesProtocol._
import eu.hbp.mip.woken.messages.variables.{ EnumeratedValue => EV }
import VariableType._
import SqlType._
import eu.hbp.mip.woken.messages.datasets.DatasetId

class VariableMetaDataTest extends WordSpec with Matchers with JsonUtils {

  val apoe4Meta = VariableMetaData(
    code = "apoe4",
    label = "ApoE4",
    `type` = polynominal,
    sqlType = Some(int),
    description = Some("Apolipoprotein E (APOE) e4 allele"),
    methodology = Some("mip-cde"),
    units = None,
    enumerations = Some(List(EV("0", "0"), EV("1", "1"), EV("2", "2"))),
    length = None,
    minValue = None,
    maxValue = None,
    datasets = Set("setA", "setB").map(DatasetId)
  )

  val apoe4Json: JsValue           = loadJson("/messages/variables/apoe4-variable.json")
  val sampleVariablesJson: JsValue = loadJson("/messages/variables/sample_variables.json")
  val mipCDEVariablesJson: JsValue = loadJson("/messages/variables/mip_cde_variables.json")

  "VariableMetaData" should {

    "be serializable to Json" in {
      apoe4Meta.toJson shouldBe apoe4Json
    }

    "be deserializable from Json" in {
      apoe4Json.convertTo[VariableMetaData] shouldBe apoe4Meta
    }
  }

  "GroupMetaData" should {

    "be deserializable from sample Json and back" in {
      val root = sampleVariablesJson.convertTo[GroupMetaData]

      root.code shouldBe "root"
      root.label shouldBe "root"
      root.groups.size shouldBe 1
      root.variables.isEmpty shouldBe true
      root.parent.isEmpty shouldBe true

      val all = root.groups.head

      all.code shouldBe "all"
      all.label shouldBe "All"
      all.variables.size shouldBe 10
      all.groups.isEmpty shouldBe true
      all.parent shouldBe List("root")

      val IQ: VariableMetaData = all.variables(3)

      IQ.code shouldBe "IQ"
      IQ.label shouldBe "IQ"

      root.toJson shouldBe sampleVariablesJson
    }

    "be deserializable from CDE Json" in {
      val root = mipCDEVariablesJson.convertTo[GroupMetaData]

      root.code shouldBe "root"
      root.label shouldBe "/"
      root.groups.size shouldBe 6
      root.variables.isEmpty shouldBe true
      root.parent.isEmpty shouldBe true

      val genetic = root.groups.head

      genetic.code shouldBe "genetic"
      genetic.label shouldBe "Genetic"
      genetic.groups.size shouldBe 1
      genetic.variables.isEmpty shouldBe true
      genetic.parent shouldBe List("root")

      val polymorphism = genetic.groups.head

      polymorphism.code shouldBe "polymorphism"
      polymorphism.label shouldBe "polymorphism"
      polymorphism.variables.size shouldBe 14
      polymorphism.groups.isEmpty shouldBe true
      polymorphism.parent shouldBe List("root", "genetic")

      val apoe4: VariableMetaData = polymorphism.variables.head

      apoe4.code shouldBe "apoe4"
      apoe4.label shouldBe "ApoE4"
      apoe4.sqlType shouldBe Some(int)

      root.toJson shouldBe mipCDEVariablesJson

    }
  }
}
