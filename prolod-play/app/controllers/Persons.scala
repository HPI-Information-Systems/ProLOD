package controllers

import models.{Link, Person}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

//TODO remove, just sample data
object Persons extends Controller {

  implicit val personFormat = Json.format[Person]
  implicit val linkFormat = Json.format[Link]

  val dataPerson = List(Person(0, "Gianluigi", "Buffon", 28,1),
                        Person(1, "Fabio", "Grosso", 30,2),
                        Person(2, "Marco", "Materazzi", 31,2),
                        Person(3, "Fabio", "Cannavaro", 29,2),
                        Person(4, "Gianluca", "Zambrotta", 31,2),
                        Person(5, "Andrea", "Pirlo", 27,3),
                        Person(6, "Gennarino", "Gattuso", 29,3),
                        Person(7, "Mauro", "Camoranesi", 27,3),
                        Person(8, "Simone", "Perrotta", 30,3),
                        Person(9, "Francesco", "Totti", 31,4),
                        Person(10, "Luca", "Toni", 31,4))

  val dataLink = List(Link(0, 0, 1, 1),Link(0, 0, 2, 1),Link(0, 0, 3, 1),Link(0, 0, 4, 1), Link(0,1,0,1),
                      Link(1, 1, 5, 1),Link(1, 1, 6, 1),Link(1, 1, 7, 1),Link(1, 1, 8, 1),
                      Link(1, 2, 5, 1),Link(1, 2, 6, 1),Link(1, 2, 7, 1),Link(1, 2, 8, 1),
                      Link(1, 3, 5, 1),Link(1, 3, 6, 1),Link(1, 3, 7, 1),Link(1, 3, 8, 1),
                      Link(1, 4, 5, 1),Link(1, 4, 6, 1),Link(1, 4, 7, 1),Link(1, 4, 8, 1),
                      Link(1, 5, 9, 1),Link(1, 5, 10, 1),
                      Link(1, 6, 9, 1),Link(1, 6, 10, 1),
                      Link(1, 7, 9, 1),Link(1, 7, 10, 1),
                      Link(1, 8, 9, 1),Link(1, 8, 10, 1))

  def getPersons = Action {
    val json = Json.obj("data" -> dataPerson)
    Ok(json)
  }

  def getPerson(id: Int) = Action {
    val json = Json.obj("data" -> dataPerson(id))
    Ok(json)
  }

  def getPersonLinks = Action {
    val json = Json.obj("nodes" -> dataPerson,
      "links" -> dataLink)
    Ok(json)
  }
}
