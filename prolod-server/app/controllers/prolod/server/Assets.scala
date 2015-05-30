package controllers.prolod.server

import play.api.http.{LazyHttpErrorHandler, DefaultHttpErrorHandler}


object Assets extends controllers.AssetsBuilder(DefaultHttpErrorHandler)