package rock_the_JVM.akka_http.chapter_3

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object DirectivesBreakdown_32 extends App {
  implicit val system = ActorSystem("DirectivesBreakdown")

  /**
   * Type #1 - filtering directives
   */
  val simpleHttpMethodRoute =
    post { // equivalent directives: get, put, patch, delete, head, options
      complete(StatusCodes.Forbidden)
    }

  val simplePathRoute =
    path("about") {
      complete(
        HttpEntity(
          ContentTypes.`application/json`,
          """
            |<html>
            | <body>
            |   Hello from about page!
            | </body>
            |</html>
            |""".stripMargin
        )
      )
    }

  val complexPathRoute =
    path("api" / "myEndpoint") {
      complete(StatusCodes.OK)
    }

  // '/' will be URL encoded (/ == %2F)
  val dontConfuse =
    path("api/myEndpoint") {
      complete(StatusCodes.OK)
    }

  val pathEndRoute =
    pathEndOrSingleSlash { // localhost:8080/ OR localhost:8080
      complete(StatusCodes.OK)
    }

  /**
   * Type #2 - extraction directives
   */

  // GET on /api/item/42
  val pathExtractionRoute = {
    path("api" / "item" / IntNumber) { (itemNumber: Int) =>
      // other directives

      println(s"number in my path: $itemNumber")
      complete(StatusCodes.OK)
    }
  }

  val pathMultiExtractRoute =
    path("api" / "order" / IntNumber / IntNumber) { (id, inventory) =>
      println(s"order: $id, inventory: $inventory")
      complete(StatusCodes.OK)
    }

  val queryParamExtractionRoute =
  // apit/item?id=34
    path("api" / "item") {
      // 'id - symbols, perform benefits
      parameter('id.as[Int]) { (itemId: Int) =>
        println(s"Extracted parameter id: $itemId")
        complete(StatusCodes.OK)
      }
    }

  val extractRequestRoute =
    path("controlEndpoint") {
      extractRequest { httpRequest: HttpRequest =>
        extractLog { (log: LoggingAdapter) =>
          log.info(s"httpRequest: $httpRequest")
          complete(StatusCodes.OK)
        }
      }
    }

  /**
   * Type #3 - composite directives
   */
  val simpleNestedRoute = {
    path("api" / "item") {
      get {
        complete(StatusCodes.OK)
      }
    }
  }

  val compactSimpleNestedRoute = (path("api" / "item") & get) {
    complete(StatusCodes.OK)
  }

  val compactExtractRequestRoute =
    (path("controlEndpoint") & extractRequest & extractLog) { (request, log) =>
      log.info(s"httpRequest: $request")
      complete(StatusCodes.OK)
    }

  // /about and /aboutUs
  val repeatedRoute =
    path("about") {
      complete(StatusCodes.OK)
    } ~
      path("aboutAs") {
        complete(StatusCodes.OK)
      }

  val dryRoute =
    (path("about") | path("aboutAs")) {
      complete(StatusCodes.OK)
    }

  // /yorblog.com/42 AND yourblog.com?postId=42
  val blogByIdRoute =
    path(IntNumber) { (blogId: Int) =>
      // complex server logic
      complete(StatusCodes.OK)
    }

  val blogByQueryParamRoute =
    parameter("postId".as[Int]) { (blogPostId: Int) =>
      // the SAME server logic
      complete(StatusCodes.OK)
    }

  val combinedBlogByIdRoute =
    (path(IntNumber) | parameter("postId".as[Int])) { (blogPostId: Int) =>
      // the SAME server logic
      complete(StatusCodes.OK)
    }

  /**
   * Type #4 - "actionable" directives
   */
  val completeOkRoute = complete(StatusCodes.OK)

  val failedRoute =
    path("notSupported") {
      failWith(new RuntimeException("Unsupported")) // complete with HTTP 500
    }

  val routeWithRejection =
    path("home") {
      reject
    } ~
      path("index") {
        completeOkRoute
      }


  Http().newServerAt("localhost", 8080).bind(pathMultiExtractRoute)


}
