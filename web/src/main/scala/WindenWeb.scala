package io.github.mkotsur

import winden.model.Prompt.dict._
import winden.web.json.encoders._

import cats.effect.{ExitCode, IO, IOApp}
import io.circe.Encoder.encodeYearMonth
import io.circe._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global
object WindenWeb extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val helloWorldService = HttpRoutes.of[IO] {
      case GET -> Root / "hello" =>
        Ok(prevMonthPrompt)
    }

    val httpApp = Router("/" -> helloWorldService).orNotFound
    val serverBuilder =
      BlazeServerBuilder[IO](global).bindHttp(8080, "localhost").withHttpApp(httpApp)
    serverBuilder.resource.use(_ => IO.never).uncancelable.map(_ => ExitCode.Success)
  }
}
