package akkaRJVM

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


// Passing configuration in Akka
object IntroAkkaConfig extends App {

  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  // #1 - inline configuration
  val configString: String =
    """
      |akka{
      | loglevel = ERROR
      |}
      |""".stripMargin
  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", config)
  val actor = system.actorOf(Props[SimpleLoggingActor])

  actor ! "A message...."

  // #2 = configuration file
  val defaultConfigSystem = ActorSystem("DefaultConfigFileDemo")
  val defaultConfigActor = defaultConfigSystem.actorOf(Props[SimpleLoggingActor])

  defaultConfigActor ! "Message from default config actor"

  // #3 - separate config in the same file
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigActorSystem = ActorSystem("specialConfigDemo", specialConfig)
  val specialConfigActor = specialConfigActorSystem.actorOf(Props[SimpleLoggingActor])

  specialConfigActor ! "special config"

  // #4 - separate config in another file
  val separateConfig = ConfigFactory.load("secretFolder/secretConfiguration.conf")
  println(s"Separate config logLevel: ${separateConfig.getString("akka.loglevel")}")

  // #5 - how to parse different file formats
  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  println(s"Json config loglevel: ${jsonConfig.getString("akka.loglevel")}")

  val propConfig = ConfigFactory.load("props/propsConfig.properties")
  println(s"Props config loglevel: ${propConfig.getString("akka.loglevel")}")
}
