package controllers

import model.{DatabaseSource, Group, Data}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import play.api.http.MimeTypes

object Application extends Controller {

  def index = Action {
    Ok(views.html.Index())
  }

  implicit val groupFormat = Json.format[Group]
  implicit val databaseSourceFormat = Json.format[DatabaseSource]

  def jsonTest = Action {
    val json = Json.obj("data" -> Data.list)
    Ok(json)
  }
}
