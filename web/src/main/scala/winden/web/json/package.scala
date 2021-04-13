package io.github.mkotsur
package winden.web

import winden.model.Prompt

import io.circe.Encoder

package object json {

  object encoders {
    implicit def encodePrompt[A: Encoder]: Encoder[Prompt[A]] =
      Encoder.forProduct2("question", "defaultAnswer")(u => (u.question, u.defaultAnswer))
  }

}
