package controllers.prolod.server

import play.api.mvc.{Action, Controller}


object Application extends Controller {

  def index = Action {
    Ok(views.html.Server())
  }
  def index2 = Action {
    Ok(views.html.prolod.server.Server2())
  }

 }
