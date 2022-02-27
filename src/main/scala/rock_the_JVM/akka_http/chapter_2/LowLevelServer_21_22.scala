package rock_the_JVM.akka_http.chapter_2

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

object LowLevelServer_21_22 extends App {
  implicit val system = ActorSystem("LowLevelServerAPI")

  import system.dispatcher

  // source of connections
  // ServerBinding - unbind or shut down the server
  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] = Http().bind("localhost", 8000)
  val connectionSink = Sink.foreach[IncomingConnection] { connection =>
    println(s"Accepted incoming connection from: ${connection.remoteAddress}")
  }

  // materialized value - server binding
  val serverBindingFuture = serverSource.to(connectionSink).run()

  // shutdown server
//  serverBindingFuture.flatMap(binding => binding.unbind())
//    .onComplete(_ => system.terminate())

  serverBindingFuture.onComplete {
    case Success(binding) => println("Server binding successful")
    case Failure(exception) => println(s"Server binding failed: $exception")
  }

  /**
   * #1 SYNCHRONOUSLY serve HTTP responses
   */
  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, _, _, _, _) => HttpResponse(
      StatusCodes.OK, // HTTP 200
      entity = HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        """
          |<html>
          | <body>
          |   Hello from Akka HTTP
          | </body>
          |</html>
          |""".stripMargin
      )
    )
    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound, // 404
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   The resource can't be found
            | </body>
            |</html>
            |""".stripMargin
        )
      )
  }

  val httpSyncConnectionHandler: Sink[IncomingConnection, Future[Done]] = Sink.foreach[IncomingConnection]{ connection =>
    connection.handleWithSyncHandler(requestHandler)
  }

  // Http().bind("localhost",8000).runWith(httpSinkConnectionHandler )
  // shorter way
  // Http().bindAndHandleSync(requestHandler,"localhost",8000)

  /**
   * #2 ASYNCHRONOUSLY serve HTTP responses
   */

  val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) => Future( // method, URI,HTTP headers, content, protocol (HTTP 1.1 / HTTP 2.0)
      HttpResponse(
        StatusCodes.OK, // HTTP 200
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Hello from Akka HTTP
            | </body>
            |</html>
            |""".stripMargin
        )
      )
    )
    case request: HttpRequest =>
      request.discardEntityBytes()
      // in PROD use dedicated ExecutionContext
      Future(
        HttpResponse(
          StatusCodes.NotFound, // 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |   The resource can't be found
              | </body>
              |</html>
              |""".stripMargin
          )
        )
      )
  }

  val httpAsyncConnectionHandler: Sink[IncomingConnection, Future[Done]] = Sink.foreach[IncomingConnection]{ connection =>
    connection.handleWithAsyncHandler(asyncRequestHandler)
  }

//  Http().bind("localhost",8081).runWith(httpAsyncConnectionHandler)
//
//  Http().bindAndHandleAsync(asyncRequestHandler,"localhost",8081)

  /**
   * #3 ASYNC via Akka Streams
   */

  val streamsBasedRequestHandler: Flow[HttpRequest,HttpResponse,_] = Flow[HttpRequest].map{
    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) => // method, URI,HTTP headers, content, protocol (HTTP 1.1 / HTTP 2.0)
      HttpResponse(
        StatusCodes.OK, // HTTP 200
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Hello from Akka HTTP
            | </body>
            |</html>
            |""".stripMargin
        )

    )
    case request: HttpRequest =>
      request.discardEntityBytes()
      // in PROD use dedicated ExecutionContext
        HttpResponse(
          StatusCodes.NotFound, // 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |   The resource can't be found
              | </body>
              |</html>
              |""".stripMargin
          )
      )
  }

//  Http().bind("localhost",8082).runForeach{connection =>
//    connection.handleWith(streamsBasedRequestHandler)
//  }

  Http().bindAndHandle(streamsBasedRequestHandler,"localhost",8082)

  // TODO: Create your own Http server on localhost port 8388, which replies
  // - with a welcome message on the "front door" localhost:8388
  // - with a proper HTML on localhost:8388/about
  // - with a 404 message otherwise

  val exerciseHandler: Flow[HttpRequest,HttpResponse,_] = Flow[HttpRequest].map{
    case HttpRequest(HttpMethods.GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(
        StatusCodes.OK, // HTTP 200
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Hello from Akka HTTP
            | </body>
            |</html>
            |""".stripMargin
        )
      )
    case HttpRequest(HttpMethods.GET, Uri.Path("/about"), _, _, _) =>
      HttpResponse(
        StatusCodes.OK, // HTTP 200
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |  About what??? :O
            | </body>
            |</html>
            |""".stripMargin
        )
      )
      // not connected with exercise
    case HttpRequest(HttpMethods.GET, Uri.Path("/search"), _, _, _) =>
      HttpResponse(
        StatusCodes.Found, // 302,
        headers = List(Location("http://google.com"))
      )
    case request: HttpRequest =>
      request.discardEntityBytes()
      // in PROD use dedicated ExecutionContext
      HttpResponse(
        StatusCodes.NotFound, // 404
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   The resource can't be found
            | </body>
            |</html>
            |""".stripMargin
        )
      )
  }

  //  Http().bind("localhost",8082).runForeach{connection =>
  //    connection.handleWith(streamsBasedRequestHandler)
  //  }

  Http().bindAndHandle(exerciseHandler,"localhost",8388)
}
