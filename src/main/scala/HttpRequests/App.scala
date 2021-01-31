package HttpRequests

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import java.net.URLEncoder
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
object App {

  // akka acotrs
  implicit val system = ActorSystem()

  // akka streams
  implicit val materilizer = ActorMaterializer()
  import system.dispatcher // "thread pool"

  val source =
    """
      |object SimpleApp{
      | val aField = 2
      | def add5(x : Int) = x + 5
      |
      | def main(args: Array[String]): Unit = {
      |   println(add5(aField))
      | }
      |}
      |""".stripMargin

  val request = HttpRequest(
    method = HttpMethods.POST,
    uri = "http://markup.su/api/highlighter",
    entity = HttpEntity(
      ContentTypes.`application/x-www-form-urlencoded`,
      s"source=${URLEncoder.encode(source,"UTF-8")}&language=Scala&theme=Sunburst"
    )
  )

  def sendRequest() = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(_.entity.toStrict(2.seconds))
    entityFuture.map(_.data.utf8String)
  }

  def main(args: Array[String]): Unit = {
      sendRequest().foreach(println)
  }
}
