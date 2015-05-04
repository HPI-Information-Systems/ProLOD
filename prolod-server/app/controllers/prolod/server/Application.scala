package controllers.prolod.server

import play.api.mvc.{Action, Controller}

object Application extends Controller {

   def index = Action {
     Ok("test"/*views.html.Index()*/)
   }

 }
