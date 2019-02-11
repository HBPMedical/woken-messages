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

package ch.chuv.lren.woken.messages.variables

import ch.chuv.lren.woken.messages.datasets.DatasetId
import io.swagger.annotations.ApiModel

sealed trait FeatureIdentifier {
  def id: String
}

/**
  * Id of a variable
  *
  * @param code Unique variable code, used to request
  */
@ApiModel(
  description = "Id of a variable"
)
case class VariableId(
    code: String
) extends FeatureIdentifier {
  override def id: String = code
}

/**
  * Id of a group
  *
  * @param path part of the path identifying this group relative to its parent
  */
@ApiModel(
  description = "Id of a group"
)
case class GroupId(path: List[PathSegment]) extends FeatureIdentifier {
  override def id: String = path.mkString("/")

  def parent: Option[GroupId] =
    if (path.lengthCompare(1) > 0)
      Some(GroupId(path.take(path.size - 1)))
    else
      None
}

case class EnumeratedValue(code: String, label: String)

object VariableType extends Enumeration {
  type VariableType = Value

  /** Integer value */
  val integer: Value = Value("integer")

  /** Real value */
  val real: Value = Value("real")

  /** Boolean value (true, false) */
  val boolean: Value = Value("boolean")

  /** Date value, */
  val date: Value = Value("date")

  /** A value selected from a list (enumeration) */
  val polynominal: Value = Value("polynominal")

  /** A value with one state or another */
  val binominal: Value = Value("binominal")

  /** Text value */
  val text: Value = Value("text")

  /** An image */
  val image: Value = Value("image")
}

object SqlType extends Enumeration {
  type SqlType = Value

  /** Integer value */
  val int: Value = Value("int")

  /** Numeric (decimal) value */
  val numeric: Value = Value("numeric")

  /** Char value, can be a fixed length string */
  val char: Value = Value("char")

  /** Varchar value, for variable length string */
  val varchar: Value = Value("varchar")
}

/**
  * Common measures of location, or central tendency
  *
  * @param mean Arithmetic mean
  * @param median Median
  */
// The common measure of dependence between paired random variables is the Pearson product-moment correlation coefficient, while a common alternative summary statistic is Spearman's rank correlation coefficient. A value of zero for the distance correlation implies independence.
// The interquartile mean (IQM) (or midmean) is a statistical measure of central tendency based on the truncated mean of the interquartile range.
case class LocationStatistics(
    mean: Double,
    median: Double
    //mode: Double,
    // interquartile mean: Double
)

/**
  * Common measures of statistical dispersion
  *
  * @param std standard deviation
  * @param min range minimum
  * @param max range maximum
  */
// variance, interquartile range, absolute deviation, mean absolute difference and the distance standard deviation. Measures that assess spread in comparison to the typical size of data values include the coefficient of variation.
case class DispersionStatistics(
    std: Double,
    min: Double,
    max: Double
)

/**
  * Summary statistics
  *
  * @param location Measures of location, or central tendency
  * @param dispersion Measures of statistical dispersion
  */
case class SummaryStatistics(
    location: LocationStatistics,
    dispersion: DispersionStatistics
)

import VariableType.VariableType
import SqlType.SqlType

/**
  * Metadata describing a variable
  *
  * @param code Code of the variable. Should be unique
  * @param label Label used when displaying the variable on the screen
  * @param `type` Type of the variable
  * @param sqlType Sql type of the variable
  * @param methodology Methodology used to acquire the variable
  * @param description Description of the variable
  * @param units Units
  * @param enumerations List of valid values for enumerations
  * @param length Maximum length for textual variables
  * @param minValue Minimum value for numerical variables, defined by a specialist
  * @param maxValue Maximum value for numerical variables, defined by a specialist
  * @param summaryStatistics Summary statistics collected from the datasets
  * @param datasets List of datasets where this variable appears
  */
case class VariableMetaData(
    code: String,
    label: String,
    `type`: VariableType,
    sqlType: Option[SqlType],
    methodology: Option[String],
    description: Option[String],
    units: Option[String],
    enumerations: Option[List[EnumeratedValue]],
    length: Option[Int],
    minValue: Option[Double],
    maxValue: Option[Double],
    summaryStatistics: Option[SummaryStatistics],
    datasets: Set[DatasetId]
) {
  def toId: VariableId = VariableId(code)

  /**
    * Return true if this variable is a candidate for merging with another variable.
    *
    * The merge criteria used here is quite strict, and most fields, except length, minValue, maxValue,
    * summaryStatistics and datasets must match exactly.
    *
    * @param other The other variable to check
    * @return true if this variable is a candidate for merging with another variable.
    */
  def isMergeable(other: VariableMetaData): Boolean =
    code == other.code &&
    label == other.label &&
    `type` == other.`type` &&
    sqlType == other.sqlType &&
    methodology == other.methodology &&
    description == other.description &&
    units == other.units &&
    enumerations == other.enumerations
  // ignore length (too subjective?)
  // ignore minValue, maxValue
  // ignore summaryStatistics
  // ignore datasets, that's the main point, to be able to merge variables between datasets

  def merge(other: VariableMetaData): Option[VariableMetaData] =
    if (isMergeable(other))
      Some(
        copy(
          datasets = this.datasets ++ other.datasets,
          length = this.length.flatMap(l => other.length.map(o => Math.max(l, o))),
          minValue = this.minValue.flatMap(v => other.minValue.map(o => Math.min(v, o))),
          maxValue = this.maxValue.flatMap(v => other.maxValue.map(o => Math.max(v, o))),
          summaryStatistics = None
        )
      )
    else
      None
}

/**
  * Metadata describing a group
  *
  * @param code Identifier for the group
  * @param description Human readable description of the group
  * @param label Label used to display the group in the user interface
  * @param groups List of sub-groups
  * @param variables List of variables contained in the group
  */
case class GroupMetaData(
    code: String,
    description: Option[String],
    label: String,
    parent: List[PathSegment],
    groups: List[GroupMetaData],
    variables: List[VariableMetaData]
) {
  def toId: GroupId = GroupId(parent :+ code)
}
