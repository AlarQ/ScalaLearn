package rock_the_JVM.akka_http.chapter_2

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.pattern.ask
import akka.util.Timeout
import rock_the_JVM.akka_http.chapter_2.GuitarDB.{CreateGuitar, FindAllGuitars, FindGuitar, GuitarCreated}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

case class Guitar(make: String, model: String)

object GuitarDB {
  case class CreateGuitar(guitar: Guitar)

  case class GuitarCreated(id: Int)

  case class FindGuitar(id: Int)

  case object FindAllGuitars
}

class GuitarDB extends Actor with ActorLogging {

  import GuitarDB._

  var guitars: Map[Int, Guitar] = Map()
  var currentGuitarId: Int = 0

  override def receive: Receive = {
    case FindAllGuitars =>
      log.info("Searching for all guitars")
      sender() ! guitars.values.toList
    case FindGuitar(id) =>
      log.info(s"Searching guitar by id = $id")
      sender() ! guitars.get(id)
    case CreateGuitar(guitar) =>
      log.info(s"Adding guitar $guitar with id = $currentGuitarId")
      guitars = guitars + (currentGuitarId -> guitar)
      sender() ! GuitarCreated(currentGuitarId)
      currentGuitarId += 1
  }
}

trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {

  implicit val guitarFormat = jsonFormat2(Guitar)
}

object LowLevelRest_23 extends App with GuitarStoreJsonProtocol {

  implicit val system = ActorSystem("LowLevelRest")

  import system.dispatcher // not use on PROD - write dedicated dispatcher

  /**
   * GET localhost:8080/api/guitar => ALL guitars in the store
   * GET localhost:8080/api/guitar?id=X => fetches the guitar associated  with id X
   * POST localhost:8080/api/guitar => insert the guitar into the store
   */

  // JSON - marshaling to JSON, unmarshalling from JSON

  val guitarDB = system.actorOf(Props[GuitarDB], "LowLevelGuitarDB")

  val guitarList = List(
    Guitar("Fender", "Stratocaster"),
    Guitar("Gibson", "Les Paul"),
    Guitar("Martin", "LX1")
  )

  guitarList.foreach { guitar => guitarDB ! CreateGuitar(guitar) }

  // server code

  def getGuitar(query: Query): Future[HttpResponse] = {
    val guitarId: Option[Int] = query.get("id").map(_.toInt) // can throw excpetion
    guitarId match {
      case None => Future(HttpResponse(StatusCodes.NotFound))
      case Some(id: Int) =>
        val guitarFuture: Future[Option[Guitar]] = (guitarDB ? FindGuitar(id)).mapTo[Option[Guitar]]
        guitarFuture.map{
          case None => HttpResponse(StatusCodes.NotFound)
          case Some(guitar) =>
            HttpResponse(
              entity = HttpEntity(
                ContentTypes.`application/json`,
                guitar.toJson.prettyPrint
              )
            )
        }
    }

  }

  implicit val defaultTimeout: Timeout = Timeout(2 seconds)
  val requestHandler: HttpRequest => Future[HttpResponse] = {
    /**
     * add some query parameter code here
     */
    case HttpRequest(HttpMethods.GET, uri@Uri.Path("/api/guitar"), _, _, _) =>
      val query = uri.query() // query object <=> Map[String,String]

      if (query.isEmpty) {
        val guitarFuture: Future[List[Guitar]] = (guitarDB ? FindAllGuitars).mapTo[List[Guitar]]
        guitarFuture.map { guitars =>
          HttpResponse(
            entity = HttpEntity(
              contentType = ContentTypes.`application/json`,
              string = guitars.toJson.prettyPrint
            )
          )
        }
      } else {
        // fetch guitar associated with given id
        getGuitar(query)
      }
    case HttpRequest(HttpMethods.POST, Uri.Path("/api/guitar"), _, entity, _) =>
      // entity is Source[ByteString]
      val strictEntityFuture = entity.toStrict(3 seconds)
      strictEntityFuture.flatMap { strictEntity =>
        val guitarJsonString = strictEntity.data.utf8String
        val guitar = guitarJsonString.parseJson.convertTo[Guitar]

        val guitarCreatedFuture: Future[GuitarCreated] = (guitarDB ? CreateGuitar(guitar)).mapTo[GuitarCreated]
        guitarCreatedFuture.map(_ => HttpResponse(status = StatusCodes.OK))
      }

    case request: HttpRequest =>
      request.discardEntityBytes()
      Future {
        HttpResponse(status = StatusCodes.NotFound)
      }
  }

  Http().newServerAt("localhost", 8080).bind(requestHandler)

   // TODO EB Exercise from 2.5
}
