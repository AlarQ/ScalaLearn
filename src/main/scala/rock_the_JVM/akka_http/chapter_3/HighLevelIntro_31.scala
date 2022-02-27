package rock_the_JVM.akka_http.chapter_3

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object HighLevelIntro_31 extends App {
  implicit val system = ActorSystem("HighlevelIntro")

  import system.dispatcher

  // directives - building blocks of high-level akka-http server logic
  // specify - what happens under certain conditions

  // Route <=> RequestContext => Future[RouteResult]
  // RequestContext:
  // - HttpRequest being handled
  // - the actor system, logging adapter etc
  // - almost newer needed to be created explicitly

  // Routing tree:
  // - filtering and nesting,
  // - chaining with ~
  // - extracting data

  

  val simpleRoute: Route =
  path("home") { // DIRECTIVE
    complete(StatusCodes.OK) // DIRECTIVE
  }

  val pathGetRoute: Route =
    path("home") {
      get {
        complete(StatusCodes.OK)
      }
    }

  // chaining directives with ~
  val chainedRoute: Route = {
    path("myEndpoint") {
      get {
        complete(StatusCodes.OK)
      } ~ // VERY IMPORTANT ~, without tilda only last directives will be used!!!
        post {
          complete(StatusCodes.Forbidden)
        }
    } ~
      path("home"){
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |   Hello from high level Akka HTTP!
              | </body>
              |</html>
              |""".stripMargin
          )
        )
      }
  } // Routing tree


  Http().newServerAt("localhost", 8080).bind(chainedRoute)
}
