name := "ScalaLearn"

version := "0.1"

scalaVersion := "2.13.4"

val akkaVersion = "2.6.12"
val akkaHttpVersion = "10.1.11"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.6.1"
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.15.4"
libraryDependencies += scalacheck % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
libraryDependencies ++= Seq(
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-stream" % akkaVersion,


"com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

)

