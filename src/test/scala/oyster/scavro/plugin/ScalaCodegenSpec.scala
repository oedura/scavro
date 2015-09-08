package oyster.scavro.plugin

import org.scalatest._
import com.twitter.util.Eval
import ScalaCodegen._
import treehugger.forest._, definitions._, treehuggerDSL._

class ScalaCodegenSpec extends FlatSpec {
  "getterName" should "generate the getter" in {
    assert(ScalaCodegen.getterName("foo") == "getFoo")
  }

  "setterName" should "generate the setter" in {
    assert(ScalaCodegen.setterName("foo") == "setFoo")
  }

  "ScavroString" should "extract char sequence" in {
    val eval = new Eval()
    val tree = ScavroString.javaGetter("foo")
    val code = treeToString(tree)

    val setup = """case class Foo(foo: CharSequence) {
                  |  def getFoo(): CharSequence = foo
                  |}
                  |val j = Foo("asdf")
                  |""".stripMargin

    val output = eval[String](setup + code)
    assert(output == "asdf")
  }

  "Scavro Primatives" should "build like ScavroInt(\"intField\")" in {
    val string = ScavroString("foo")
    assert(treeToString(CASECLASSDEF("Foo") withParams(string.scalaParam) : Tree) == "case class Foo(foo: String)")
    val boolean = ScavroBoolean("foo")
    assert(treeToString(CASECLASSDEF("Foo") withParams(boolean.scalaParam) : Tree) == "case class Foo(foo: Boolean)")
    val int = ScavroInt("foo")
    assert(treeToString(CASECLASSDEF("Foo") withParams(int.scalaParam) : Tree) == "case class Foo(foo: Int)")
    val long = ScavroLong("foo")
    assert(treeToString(CASECLASSDEF("Foo") withParams(long.scalaParam) : Tree) == "case class Foo(foo: Long)")
    val float = ScavroFloat("foo")
    assert(treeToString(CASECLASSDEF("Foo") withParams(float.scalaParam) : Tree) == "case class Foo(foo: Float)")
    val double = ScavroDouble("foo")
    assert(treeToString(CASECLASSDEF("Foo") withParams(double.scalaParam) : Tree) == "case class Foo(foo: Double)")
  }

  it should "retrieve value from case class" in {
    val eval = new Eval()
    // ------------------------
    val stringParam = ScavroString("foo")
    val code_s = s"""case class Foo(${treeToString(stringParam.scalaParam)}) {
                    |  def test: String = ${treeToString(stringParam.scalaGetter)}
                    |}
                    |val test = Foo("asdf")
                    |test.test
                    |""".stripMargin
    assert(eval[String](code_s) == "asdf")

    val booleanParam = ScavroBoolean("foo")
    val code_b = s"""case class Foo(${treeToString(booleanParam.scalaParam)}) {
                    |  def test: Boolean = ${treeToString(booleanParam.scalaGetter)}
                    |}
                    |val test = Foo(false)
                    |test.test
                    |""".stripMargin
    assert(eval[Boolean](code_b) == false)

    val intParam = ScavroInt("foo")
    val code_i = s"""case class Foo(${treeToString(intParam.scalaParam)}) {
                    |  def test: Int = ${treeToString(intParam.scalaGetter)}
                    |}
                    |val test = Foo(42)
                    |test.test
                    |""".stripMargin
    assert(eval[Int](code_i) == 42)

    val longParam = ScavroLong("foo")
    val code_l = s"""case class Foo(${treeToString(longParam.scalaParam)}) {
                    |  def test: Long = ${treeToString(longParam.scalaGetter)}
                    |}
                    |val test = Foo(42L)
                    |test.test
                    |""".stripMargin
    assert(eval[Long](code_l) == 42L)

    val floatParam = ScavroFloat("foo")
    val code_f = s"""case class Foo(${treeToString(floatParam.scalaParam)}) {
                    |  def test: Float = ${treeToString(floatParam.scalaGetter)}
                    |}
                    |val test = Foo(42.0F)
                    |test.test
                    |""".stripMargin
    assert(eval[Float](code_f) == 42.0F)

    val doubleParam = ScavroDouble("foo")
    val code_d = s"""case class Foo(${treeToString(doubleParam.scalaParam)}) {
                    |  def test: Double = ${treeToString(doubleParam.scalaGetter)}
                    |}
                    |val test = Foo(42.0)
                    |test.test
                    |""".stripMargin
    assert(eval[Double](code_d) == 42.0)
  }

  it should "provide java conversions through _.toAvro" in {
    import PrimitiveTypeConverters._
    val eval = new Eval()

    val code_s = """import oyster.scavro.plugin.PrimitiveTypeConverters._
                   |val x: String = "asdf"
                   |x.toAvro""".stripMargin
    assert(eval[java.lang.String](code_s) == "asdf")

    val code_b = """import oyster.scavro.plugin.PrimitiveTypeConverters._
                   |val x: Boolean = false
                   |x.toAvro""".stripMargin
    assert(eval[java.lang.Boolean](code_b) == false)

    val code_i = """import oyster.scavro.plugin.PrimitiveTypeConverters._
                   |val x: Int = 42
                   |x.toAvro""".stripMargin
    assert(eval[java.lang.Integer](code_i) == 42)

    val code_l = """import oyster.scavro.plugin.PrimitiveTypeConverters._
                   |val x: Long = 42
                   |x.toAvro""".stripMargin
    assert(eval[java.lang.Long](code_l) == 42)

    val code_f = """import oyster.scavro.plugin.PrimitiveTypeConverters._
                   |val x: Float = 42.0f
                   |x.toAvro""".stripMargin
    assert(eval[java.lang.Float](code_f) == 42.0f)

    val code_d = """import oyster.scavro.plugin.PrimitiveTypeConverters._
                   |val x: Double = 42.0
                   |x.toAvro""".stripMargin
    assert(eval[java.lang.Double](code_d) == 42.0)
  }

  "ScavroOption" should "extract None" in {
    val eval = new Eval()
    val tree = ScavroOption("foo", ScavroInt).javaGetter
    val code = treeToString(tree)

    val setup = 
      """case class Foo(foo: Int) {
        |  def getFoo(): Integer = null
        |}
        |val j = Foo(3)
        |""".stripMargin

    val output = eval[Option[Int]](setup + code)
    assert(output.isEmpty)
  }

  it should "extract some value" in {
    val eval = new Eval()
    val tree = ScavroOption("foo", ScavroInt).javaGetter
    val code = treeToString(tree)

    val setup = 
      """case class Foo(foo: Int) {
        |  def getFoo(): Integer = new Integer(foo)
        |}
        |val j = Foo(3)
        |""".stripMargin

    val output = eval[Option[Int]](setup + code)
    assert(output.isDefined)
    assert(output.exists(_ == 3))
  }
}
