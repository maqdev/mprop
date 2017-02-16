package com.maqdev.mprop

import org.parboiled2._

import scala.util.Try

sealed trait AstElement
case object NewLine extends AstElement
case class Comment(value: String) extends AstElement
case class KeyValue(key: String, value: String) extends AstElement

class PropertiesParser (val input: ParserInput) extends Parser with StringBuilding {
  import CharPredicate._
  import PropertiesParser._

  def Value = rule { capture( zeroOrMore(!'\n' ~ ANY) ) ~> trim _ }

  def KeyChars = rule (
    """\\""" ~ appendSB("""\""")
    | """\=""" ~ appendSB("=")
    | (!'=' ~ !'\\' ~ !'=' ~ Visible ~ appendSB())
  )

  def Key = rule { clearSB() ~ zeroOrMore( KeyChars ) ~ push(sb.toString) }

  def KeyValueExpression: Rule1[AstElement] = rule { WhiteSpace ~ Key ~ WhiteSpace ~ '=' ~ Value ~> KeyValue }

  def WhiteSpace = rule { zeroOrMore(WhiteSpaceChar) }

  def EmptyLine = rule { '\n' ~ WhiteSpace ~ push(NewLine) }

  def CommentExpression: Rule1[AstElement] = rule { zeroOrMore(WhiteSpaceChar) ~ '#' ~ capture( zeroOrMore(!'\n' ~ ANY) ) ~> (Comment) }

  def Expression = rule { KeyValueExpression | CommentExpression | EmptyLine }

  def Lines = rule { Expression ~ zeroOrMore(Expression) ~> (Seq(_) ++ _) }

  def InputLine = rule { Lines ~ EOI }

  private def trim(s: String):String = s.trim
}

object PropertiesParser {
  val WhiteSpaceChar = CharPredicate(" \t\f")

  def apply(input: String): Try[Seq[AstElement]] = {
    val parser = new PropertiesParser(input)
    parser.InputLine.run()
  }

  def toMap(input: Seq[AstElement]): Map[String,String] = {
    val mb = Map.newBuilder[String,String]
    input.foreach {
      case KeyValue(k,v) ⇒ mb.+=(k → v)
      case other ⇒ // ignore
    }
    mb.result()
  }
}