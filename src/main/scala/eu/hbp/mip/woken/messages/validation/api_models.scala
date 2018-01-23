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

package eu.hbp.mip.woken.messages.validation

import eu.hbp.mip.woken.messages.variables.VariableMetaData
import spray.json.JsObject
import cats.data.NonEmptyList
import eu.hbp.mip.woken.messages.RemoteMessage

case class ValidationQuery(
    fold: String,
    model: JsObject,
    data: List[String],
    varInfo: VariableMetaData
) extends RemoteMessage

case class ValidationResult(
    fold: String,
    varInfo: VariableMetaData,
    outputData: List[String],
    error: Option[String]
)

// TODO: the NonEmptyList[String] contain actually a Json value to deserialise and that maps usually to String or Double
case class ScoringQuery(algorithmOutput: NonEmptyList[String],
                        groundTruth: NonEmptyList[String],
                        targetMetaData: VariableMetaData)
    extends RemoteMessage

case class ScoringResult(
    scores: JsObject
)
