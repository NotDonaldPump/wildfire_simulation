import cats.effect._
import cats.effect.unsafe.implicits.global
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.Json
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import com.comcast.ip4s._

import model.*
import simulation.*

case class Message(content: String)
case class ServerResult(status: String)

given EntityEncoder[IO, Message] = jsonEncoderOf[IO, Message]
given EntityEncoder[IO, ServerResult] = jsonEncoderOf[IO, ServerResult]
given EntityEncoder[IO, Json] = jsonEncoderOf[IO, Json] // pour le résultat de simulation

var currentGrid: Option[Grid] = None

val routes = HttpRoutes.of[IO] {

  // http route : grid initialization
  case req @ POST -> Root / "submit-grid-params" =>
    for {
      gridParams <- req.as[GridParams]

      direction = gridParams.windDirection match {
        case "N"  => N
        case "S"  => S
        case "E"  => E
        case "W"  => W
        case "NE" => NE
        case "NW" => NW
        case "SE" => SE
        case "SW" => SW
        case _    => throw new IllegalArgumentException("Invalid wind direction")
      }

      newGrid = Grid.generateRandom(
        gridParams.gridSize,
        Condition(
          Wind(direction, gridParams.windSpeed),
          gridParams.humidity,
          gridParams.temperature
        )
      )

      _ <- IO { currentGrid = Some(newGrid) }
      res <- Ok(ServerResult("Grille initialisée"))
    } yield res

  // http route : simulation
  case req @ POST -> Root / "submit-simu-params" =>
    for {
      simuParams <- req.as[SimuParams]

      result <- currentGrid match
        case Some(grid) =>
          IO {
            Simulation.run(grid, simuParams.duration, simuParams.blitzX, simuParams.blitzY) // retourne Json
          }
        case None =>
          IO.raiseError(new RuntimeException("Aucune grille initialisée"))

      res <- Ok(result)
    } yield res
}.orNotFound

@main def runServer(): Unit =
  val server = EmberServerBuilder
    .default[IO]
    .withHost(host"localhost")
    .withPort(port"8080")
    .withHttpApp(routes)
    .build
    .useForever

  server.unsafeRunSync()
