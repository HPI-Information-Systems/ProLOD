package controllers

import play.api.mvc.{Action, Controller}
import prolod.server.{Test, OtherTest}

object Application extends Controller {

  def index = Action {
    Test.helloWorld()
    OtherTest.helloWorld()
    Ok(views.html.Index())
  }

}


