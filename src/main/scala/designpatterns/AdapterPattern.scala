package designpatterns

/**
 * The adapter pattern converts interface of a class into expected interface,
 * allowing classes with incompatible interfaces to work together.
 */
object AdapterPattern {

  sealed trait Level
  case object WARNING extends Level
  case object ERROR extends Level

  trait Log {
    def warning(message: String)
    def error(message: String)
  }

  final class Logger {
    def log(level: Level, message: String) { /* ... */ }
  }

  implicit class LoggerToLogAdapter(logger: Logger) extends Log {
    def warning(message: String) { logger.log(WARNING, message) }
    def error(message: String) { logger.log(ERROR, message) }
  }

  val log: Log = new Logger()
}
