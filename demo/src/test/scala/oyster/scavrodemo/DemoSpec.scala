package oyster.scavrodemo

import org.scalatest.FlatSpec


class DemoSpec extends FlatSpec {
  "ReadWriteDemo" should "run" in {
    ReadWriteDemo.main(Array.empty[String])
  }
}
