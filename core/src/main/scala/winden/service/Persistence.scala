package io.github.mkotsur
package winden.service

import cats.effect.IO
import cats.implicits._
import io.github.mkotsur.winden.model.TimePiece
import better.files.{File => BFile}
import BFile._

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import io.circe.Encoder.encodeLocalDate
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser.decode

object Persistence {

  val workDir: BFile = home / ".winden"

  private val format = DateTimeFormatter.ofPattern("yyyy.MM")

  def load(ym: YearMonth): IO[Option[List[TimePiece]]] =
    for {
      file <- IO((workDir / s"${format.format(ym)}.wnd"))
      pieces <-
        if (file.notExists) None.pure[IO]
        else
          for {
            txt <- IO(file.contentAsString)
            pp  <- IO.fromEither(decode[List[TimePiece]](txt))
          } yield pp.pure[Option]
    } yield pieces

  def store(ym: YearMonth, pieces: List[TimePiece]): IO[BFile] =
    for {
      _    <- IO(workDir.createDirectoryIfNotExists())
      file <- IO((workDir / s"${format.format(ym)}.wnd").write(pieces.asJson.spaces2))
    } yield file

}
