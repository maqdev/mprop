package com.maqdev.mprop

import java.io._

import monix.reactive.Observable

import scala.util.control.NonFatal


object MainApp {


  def main(args: Array[String]) : Unit = {
    if (args.length < 3) {
      println("Please specify output, base and additional property files")
      System.exit(-1)
    }

    try {
      val outputFileName = args(0)
      val baseFileName = args(1)
      val additionalFileName = args(2)

      println (s"Reading $baseFileName")
      val base = scala.io.Source.fromFile(baseFileName)
      val baseLines = try base.getLines().mkString("\n") finally base.close()
      val astIn = PropertiesParser(baseLines).get

      println (s"Reading $additionalFileName")
      val overwrite = scala.io.Source.fromFile(additionalFileName)
      val overwriteLines = try overwrite.getLines().mkString("\n") finally overwrite.close()
      val astOv = PropertiesParser(overwriteLines).get

      println (s"Writing $outputFileName")
      val writer = new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8")
      val astOut = merge(astIn, PropertiesParser.toMap(astOv))

      PropertiesPrinter.write(writer, astOut)
      writer.close()

      println("DONE")
    }
    catch {
      case NonFatal(e) ⇒
        println(e.toString)
        System.exit(-2)
    }
  }

  def merge(base: Iterable[AstElement],
            overwriteWith: Map[String, String]): Iterable[AstElement] = {
    base.map {
      case KeyValue(k, v) ⇒
        overwriteWith.get(k) match {
          case Some(s) ⇒ KeyValue(k, s)
          case None ⇒ KeyValue(k, v)
        }
      case other ⇒ other
    }
  }
}
