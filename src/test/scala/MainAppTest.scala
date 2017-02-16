import java.io.StringWriter

import com.maqdev.mprop.{MainApp, PropertiesParser, PropertiesPrinter}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers}

class MainAppTest extends FreeSpec with Matchers with ScalaFutures {
  "MainApp" - {
    "should merge" in {

      val base = """
a=1
b=
c=2
#ABC
d=3"""

      val overwrite = """
b=2
c=
d=4"""

      val ast = MainApp.merge(
        PropertiesParser(base).get,
        PropertiesParser.toMap(PropertiesParser(overwrite).get)
      )

      val sw = new StringWriter()
      PropertiesPrinter.write(sw, ast)
      sw.toString should equal(
        """
a=1
b=2
c=
#ABC
d=4""")
    }
  }
}
