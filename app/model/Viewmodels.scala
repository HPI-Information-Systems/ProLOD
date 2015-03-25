package model

import play.api.libs.json.Json

case class Predicate(predicate:String, count: Int, percentage: Double)

// for graphs
case class LinkRatio(literals:Int, externalLinks:Int, internalLinks:Int)
case class DatasetInfo(distribution:List[Predicate], ratio:LinkRatio)

case class InversePredicate(predicateA:String, predicateB:String, correlation:Double, support:Double)

case class AssociationRule(condition:String, Consequence: String, frequency:Double, confidence:Double, correlation:Double)

//TODO ontology alignment classes

case class Synonym(first:String, second:String, frequency:Double, correlation:Double)

case class Fact(subject:String, predicate:String, obj:String)

case class Suggestion(subject:String)

case class Uniqueness(predicate:String, uniqueness: Double, density:Double, keyness:Double, values: Int, uniqueValues:Int)

//TODO remove, just sample data
case class Person(id: Int, firstName: String, lastName: String, age: Int, group: Int)

object ViewFormats {
  implicit val predicateFormat = Json.format[Predicate]
  implicit val linkRatioFormat = Json.format[LinkRatio]
  implicit val datasetInfoFormat = Json.format[DatasetInfo]
  implicit val inversePredicateFormat = Json.format[InversePredicate]
  implicit val associationRuleFormat = Json.format[AssociationRule]
  implicit val synonymFormat = Json.format[Synonym]
  implicit val factFormat = Json.format[Fact]
  implicit val suggestionFormat = Json.format[Suggestion]
  implicit val personFormat = Json.format[Person]
  implicit val uniquenessFormat = Json.format[Uniqueness]
}