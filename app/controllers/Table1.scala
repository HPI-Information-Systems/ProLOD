package controllers

import model.Person
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

//TODO remove, just sample data
object Table1 extends Controller {

  implicit val personFormat = Json.format[Person]

  val data = List(Person(0, "Peter", "Lustig", 24), Person(1, "Hans", "Otto", 53))

  def getAll = Action {
    val json = Json.obj("data" -> data)
    Ok(json)
  }

  def getPerson(id: Int) = Action {
    val json = Json.obj("data" -> data(id))
    Ok(json)
  }
}
