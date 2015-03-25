package controllers

import model.Person
import model.ViewFormats._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

//TODO remove, just sample class!!
object Persons extends Controller {

  val dataSource = List(Person(0, "Peter", "Lustig", 24), Person(1, "Hans", "Otto", 53))

  def getPersons = Action {
    val data: List[Person] = dataSource
    val json = Json.obj("data" -> data)
    Ok(json)
  }

  def getPerson(id: Int) = Action {
    val data: Person = dataSource(0)
    val json = Json.obj("data" -> data)
    Ok(json)
  }
}
