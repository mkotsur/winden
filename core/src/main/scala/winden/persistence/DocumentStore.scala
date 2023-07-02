package io.github.mkotsur
package winden.persistence

import better.files.{File => BFile}
import cats.effect.IO

import java.nio.file.Path

object DocumentStore {

  sealed trait DocumentRoot

  case class iCloud(directoryName: Path, iCloudRoot: BFile) extends DocumentRoot

  case class LocalFolder(root: BFile) extends DocumentRoot

  def resolve(root: DocumentRoot): IO[BFile] =
    IO {
      val res: BFile = root match {
        case iCloud(directoryName, iCloudRoot) =>
          iCloudRoot / directoryName.toString
        case LocalFolder(root) => root
      }
      res
    }

}
