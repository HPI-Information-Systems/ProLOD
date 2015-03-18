package controllers

import model.{DatabaseSource, Group, Data}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import play.api.http.MimeTypes

object Application extends Controller {


  implicit val databaseSourceFormat = Json.format[DatabaseSource]
  implicit val groupFormat = Json.format[Group]

  def index = Action {
    Ok(views.html.Index())
  }


  def jsonTest = Action {
    val json = Json.toJson(Data.list)
    Ok(json)
  }
}
