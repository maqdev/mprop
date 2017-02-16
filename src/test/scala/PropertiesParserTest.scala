import com.maqdev.mprop._
import monix.reactive.Observable
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Success

class PropertiesParserTest extends FreeSpec with Matchers with ScalaFutures {
  "PropertiesParser" - {
    "should parse single property" in {
      PropertiesParser("a=1") should equal(Success(Seq(KeyValue("a","1"))))
    }

    "should parse single property with value with special chars" in {
      PropertiesParser("a=b=1\\2") should equal(Success(Seq(KeyValue("a","b=1\\2"))))
    }

    "should parse single property and trim key and value" in {
      PropertiesParser(" a = b ") should equal(Success(Seq(KeyValue("a","b"))))
    }

    "should parse single property with key with special chars" in {
      PropertiesParser("a\\=b\\\\=1\\2") should equal(Success(Seq(KeyValue("a=b\\","1\\2"))))
    }

    "should parse single property with value with #" in {
      PropertiesParser("a=b # 2") should equal(Success(Seq(KeyValue("a","b # 2"))))
    }

    "should parse single property with empty value" in {
      PropertiesParser("a=") should equal(Success(Seq(KeyValue("a",""))))
    }

    "should parse single empty line" in {
      PropertiesParser("\n") should equal(Success(Seq(NewLine)))
    }

    "should parse multiple properties" in {
      PropertiesParser("a=1\nb=2") should equal(Success(Seq(
        KeyValue("a","1"),
        NewLine,
        KeyValue("b","2")
      )))
    }

    "should parse comment line" in {
      PropertiesParser("#hello") should equal(Success(Seq(Comment("hello"))))
    }

    "should parse multiple comment lines" in {
      PropertiesParser("#hello\n#goodbye") should equal(Success(Seq(Comment("hello"),NewLine,Comment("goodbye"))))
    }

    "integrated complex test" in {
      PropertiesParser(
        """
a=1
b\\x= 2 #
# comment 1
c.y=3

d=4\a
# comment 2
p=
        """
      ) should equal(Success(List(
        NewLine,
        KeyValue("a", "1"), NewLine,
        KeyValue("b\\x", "2 #"), NewLine,
        Comment(" comment 1"), NewLine,
        KeyValue("c.y", "3"), NewLine,
        NewLine,
        KeyValue("d", "4\\a"), NewLine,
        Comment(" comment 2"), NewLine,
        KeyValue("p", ""), NewLine
      )))
    }
  }
}
