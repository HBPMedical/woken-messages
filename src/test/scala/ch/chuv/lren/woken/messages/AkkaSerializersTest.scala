/*
 * Copyright (C) 2017  LREN CHUV for Human Brain Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.chuv.lren.woken.messages

import java.time.{ OffsetDateTime, ZoneOffset }

import ch.chuv.lren.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import ch.chuv.lren.woken.messages.datasets._
import ch.chuv.lren.woken.messages.query._
import ch.chuv.lren.woken.messages.validation.{ ValidationQuery, ValidationResult }
import ch.chuv.lren.woken.messages.variables.{
  VariableId,
  VariableMetaData,
  VariablesForDatasetsQuery,
  VariablesForDatasetsResponse
}
import spray.json._
import queryProtocol._

class AkkaSerializersTest extends WordSpec with Matchers with JsonUtils {

  val ser = new AkkaSerializer()

  "Datasets API" should {

    "serialize Datasets query" in {
      val q = DatasetsQuery(Some("table"))
      ser.fromBinary(ser.toBinary(q), Some(q.getClass)) shouldBe q
    }

    "serialize Datasets response" in {
      val r = DatasetsResponse(
        Set(
          Dataset(DatasetId("test"),
                  "Test",
                  "Description",
                  List("table"),
                  AnonymisationLevel.Identifying,
                  None)
        )
      )
      ser.fromBinary(ser.toBinary(r), Some(r.getClass)) shouldBe r
    }

  }

  "Query API" should {

    "serialize Methods query" in {
      val q = MethodsQuery
      ser.fromBinary(ser.toBinary(q), Some(q.getClass)) shouldBe q
    }

    "serialize Methods response" in {
      val r = MethodsResponse(JsObject("knn" -> JsString("algo")))
      ser.fromBinary(ser.toBinary(r), Some(r.getClass)) shouldBe r
    }

    "serialize Mining query" in {
      val jsonAst = loadJson("/messages/query/mining_query.json").asJsObject
      val q       = jsonAst.convertTo[MiningQuery]
      ser.fromBinary(ser.toBinary(q), Some(q.getClass)) shouldBe q
    }

    "serialize Experiment query" in {
      val jsonAst = loadJson("/messages/query/experiment_query.json").asJsObject
      val q       = jsonAst.convertTo[ExperimentQuery]
      ser.fromBinary(ser.toBinary(q), Some(q.getClass)) shouldBe q
    }

    "serialize Query response" in {
      val r = QueryResult("1",
                          "local",
                          OffsetDateTime.of(2018, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC),
                          "fuzzy",
                          "text/plain",
                          Some(JsString("Hi!")),
                          None)
      ser.fromBinary(ser.toBinary(r), Some(r.getClass)) shouldBe r
    }
  }

  "Validation API" should {

    "serialize Validation query" in {
      val apoe4Json: JsValue = loadJson("/messages/variables/apoe4-variable.json")
      val apoe4              = apoe4Json.convertTo[VariableMetaData]
      val q                  = ValidationQuery(1, JsObject("input" -> JsArray()), List(JsNumber(1.2)), apoe4)
      ser.fromBinary(ser.toBinary(q), Some(q.getClass)) shouldBe q
    }

    "serialize Validation response" in {
      val apoe4Json: JsValue = loadJson("/messages/variables/apoe4-variable.json")
      val apoe4              = apoe4Json.convertTo[VariableMetaData]
      val r                  = ValidationResult(1, apoe4, Right(List(JsNumber(.5))))
      ser.fromBinary(ser.toBinary(r), Some(r.getClass)) shouldBe r
    }

  }

  "Variables API" should {

    "serialize VariablesForDatasets query" in {
      val q = VariablesForDatasetsQuery(Set(DatasetId("setA")), includeNulls = false)
      ser.fromBinary(ser.toBinary(q), Some(q.getClass)) shouldBe q
    }

    "serialize VariablesForDatasets response" in {
      val r = VariablesForDatasetsResponse(Set(VariableId("apoe4")))
      ser.fromBinary(ser.toBinary(r), Some(r.getClass)) shouldBe r
    }

  }

}
