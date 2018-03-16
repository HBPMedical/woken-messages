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

package ch.chuv.lren.woken.messages.validation

import akka.serialization.Serializer
import spray.json._
import validationProtocol._

// Message serializers for Akka

class ValidationQuerySerializer extends Serializer {

  override def identifier: Int = 93561924

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val query              = o.asInstanceOf[ValidationQuery]
    val bytes: Array[Byte] = query.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[ValidationQuery].asInstanceOf[AnyRef]

}

class ValidationResultSerializer extends Serializer {

  override def identifier: Int = 64562462

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val result             = o.asInstanceOf[ValidationResult]
    val bytes: Array[Byte] = result.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[ValidationResult].asInstanceOf[AnyRef]

}

class ScoringQuerySerializer extends Serializer {

  override def identifier: Int = 97369034

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val query              = o.asInstanceOf[ScoringQuery]
    val bytes: Array[Byte] = query.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[ScoringQuery].asInstanceOf[AnyRef]

}

class ScoringResultSerializer extends Serializer {

  override def identifier: Int = 12230498

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val result             = o.asInstanceOf[ScoringResult]
    val bytes: Array[Byte] = result.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[ScoringResult].asInstanceOf[AnyRef]

}
