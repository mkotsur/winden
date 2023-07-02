package io.github.mkotsur
package winden.service

import winden.model.TimePiece
import winden.persistence.DocumentStore
import winden.persistence.DocumentStore.DocumentRoot

import better.files.{File => BFile}
import cats.data.Kleisli
import cats.effect.IO
import cats.implicits._
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.time.YearMonth
import java.time.format.DateTimeFormatter

object Persistence {

  private implicit def logger = Slf4jLogger.getLogger[IO]

  private val format = DateTimeFormatter.ofPattern("yyyy.MM")

  def load(ym: YearMonth): Kleisli[IO, DocumentRoot, Option[List[TimePiece]]] =
    Kleisli { documentRoot =>
      for {
        workDir <- DocumentStore.resolve(documentRoot)
        _       <- logger.info(s"Reading ${workDir.pathAsString}")
        file    <- IO(workDir / s"${format.format(ym)}.wnd")
        pieces <-
          if (file.notExists) None.pure[IO]
          else
            for {
              txt <- IO(file.contentAsString)
              pp  <- IO.fromEither(decode[List[TimePiece]](txt))
            } yield pp.pure[Option]
      } yield pieces
    }

  def store(ym: YearMonth, pieces: List[TimePiece]): Kleisli[IO, DocumentRoot, BFile] =
    Kleisli { documentRoot =>
      for {
        workDir <- DocumentStore.resolve(documentRoot)
        _       <- IO(workDir.createDirectoryIfNotExists())
        file    <- IO((workDir / s"${format.format(ym)}.wnd").write(pieces.asJson.spaces2))
      } yield file
    }

}
