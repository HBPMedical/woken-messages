package eu.hbp.mip.messages.external

import java.time.OffsetDateTime


case class VariableId(
  /** Unique variable code, used to request */
  code: String
)

case class Algorithm(
  /** */
  code: String,
  /** */
  name: String,
  /** */
  parameters: Map[String, String]
)

case class Validation(
  /** */
  code: String,
  /** */
  name: String,
  /** */
  parameters: Map[String, String]
)

object Operators extends Enumeration {
  type Operators = Value
  val eq = Value("eq")
  val lt = Value("lt")
  val gt = Value("gt")
  val lte = Value("lte")
  val gte = Value("gte")
  val neq = Value("neq")
  val in = Value("in")
  val notin = Value("notin")
  val between = Value("between")
}

case class Filter(
  variable: VariableId,
  operator: Operators.Operators,
  values: Seq[String]
)

case class MethodsQuery()

case class Methods(methods: Seq[String])

abstract class Query() {
  def variables: Seq[VariableId]
  def covariables: Seq[VariableId]
  def grouping: Seq[VariableId]
  def filters: Seq[Filter]
}

case class MiningQuery(
  variables: Seq[VariableId],
  covariables: Seq[VariableId],
  grouping: Seq[VariableId],
  filters: Seq[Filter],
  algorithm: Algorithm
) extends Query

case class ExperimentQuery(
  variables: Seq[VariableId],
  covariables: Seq[VariableId],
  grouping: Seq[VariableId],
  filters: Seq[Filter],
  algorithms: Seq[Algorithm],
  validations: Seq[Validation]
) extends Query

// PFA
case class QueryError(
  message: String
)

// PFA
case class QueryResult(
  jobId: String, node: String, timestamp: OffsetDateTime,
  data: Option[String] = None, error: Option[String] = None,
  shape: String, function: String
)

/*

  Filter:
    type: object
    description: A filter in a query
    properties:
      variable:
        description: |
          Variable used to filter, only code value is sent
        '$ref': '#/definitions/VariableId'
      operator:
        description: |
          Filter operator : eq, lt, gt, gte, lte, neq, in, notin, between.
        type: string
        enum:
          - eq
          - lt
          - gt
          - gte
          - lte
          - neq
          - in
          - notin
          - between
      values:
        description: |
          List of values used to filter.
          With operators “eq”, “lt”, “gt”, “gte”, “lte”, ”neq”, the filter mode “OR” is used.
          With operator “between”, only two values are sent, they represents the range limits.
        type: array
        items:
          type: string

  Query:
    type: object
    description: |
      A query object represents a request to the CHUV API.
      This object contains all information required by the API to compute a result (dataset) and return it.
    properties:
      variables:
        description: |
          List of variables used by the request, only code values are sent
        type: array
        items:
          $ref: '#/definitions/VariableId'
      covariables:
        description: |
          List of covariables used by the request, only code values are sent.
          These variables are returned in dataset object header.
        type: array
        items:
          $ref: '#/definitions/VariableId'
      grouping:
        description: |
          List of grouping variables used by the request, only code values are sent.
        type: array
        items:
          $ref: '#/definitions/VariableId'
      filters:
        description: |
          List of filters objects used by the request.
        type: array
        items:
          $ref: '#/definitions/Filter'
      request:
        description: Plot type
        type: string

  Variable:
    type: object
    description: A variable object represents a business variable. All variable information should be stored in this object.
    properties:
      code:
        type: string
        description: |
          Unique variable code, used to request
      label:
        type: string
        description: |
          Variable label, used to display
      group:
        description: |
          Variable group (only the variable path)
        '$ref': '#/definitions/Group'
      type:
        type: string
        description: |
          I: Integer, T: Text, N: Decimal, D: Date, B: Boolean.
        enum:
          - I # Integer
          - T # Text
          - N # Decimal
          - D # Date
          - B # Boolean
      length:
        type: integer
        description: |
          For text, number of characters of value
      minValue:
        type: number
        description: |
          Minimum allowed value (for integer or numeric)
      maxValue:
        type: number
        description: |
          Maximum allowed value (for integer or numeric)
      units:
        type: string
        description: Variable unit
      isVariable:
        type: boolean
        description: Can the variable can be used as a variable
      isGrouping:
        type: boolean
        description: Can the variable can be used as a group
      isCovariable:
        type: boolean
        description: Can the variable can be used as a covariable
      isFilter:
        type: boolean
        description: Can the variable can be used as a filter
      values:
        description: |
          List of variable values (if is an enumeration variable).
        type: array
        items:
          $ref: '#/definitions/Value'
  Value:
    type: object
    description: A value object is a business variable value. All value information should be stored in this object.
    properties:
      code:
        type: string
        description: |
          Unique code of value (for variable), used to request
      label:
        type: string
        description: |
          Label of value, used to display

 */