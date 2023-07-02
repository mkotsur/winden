package io.github.mkotsur
package winden.config

import winden.persistence.DocumentStore
import winden.persistence.DocumentStore.DocumentRoot

import better.files.{File => BFile}
import pureconfig._
import pureconfig.error._
import pureconfig.generic.semiauto._

import java.io.File
import scala.annotation.unused
import scala.util.Try

case class WindenConf(
    documentRoot: DocumentRoot
)

object WindenConf {

  @unused
  implicit private val configReaderBPath = ConfigReader.fromCursor(cursor =>
    cursor.asString
      .flatMap {
        case str if str.startsWith(s"~${File.separator}") =>
          Try(BFile.home / str.substring(2)).toEither.left
            .map(e =>
              ConfigReaderFailures(
                ThrowableFailure(e, cursor.origin)
              )
            )
        case str =>
          Try(BFile(str)).toEither.left
            .map(e =>
              ConfigReaderFailures(
                ThrowableFailure(e, cursor.origin)
              )
            )
      }
  )

  @unused
  implicit private val iCloudRootReader = deriveReader[DocumentStore.iCloud]
  @unused
  implicit private val localFolderReader = deriveReader[DocumentStore.LocalFolder]
  @unused
  implicit private val documenRootReader = deriveReader[DocumentRoot]

  implicit private val configReader = deriveReader[WindenConf]

  def load =
    ConfigSource.default
      .load[WindenConf]
      .left
      .map(f => new RuntimeException(s"Config loading failed with: \n${f.prettyPrint(2)}"))
}
