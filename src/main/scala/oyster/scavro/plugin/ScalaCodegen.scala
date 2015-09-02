package oyster.scavro.plugin

import treehugger.forest._, definitions._, treehuggerDSL._
import treehugger.forest._
import definitions._
import treehuggerDSL._

/*

import treehugger.forest._, definitions._, treehuggerDSL._
import treehugger.forest._
import definitions._
import treehuggerDSL._

import oyster.scavro.plugin.ScalaCodegen
import oyster.scavro.plugin.ScalaCodegen._

val paramList = ScavroString("name") :: ScavroDouble("price") :: ScavroInt("quantity") :: ScavroBoolean("isActive") :: Nil
println(treeToString(ScalaCodegen.buildTree("LineItem", "oyster.scavrodemo.idl.LineItem", paramList)))

*/

object ScalaCodegen {
  sealed trait ScavroParam {
    val name: String
  }

  sealed trait PrimativeScavroParam extends ScavroParam {
    val tHClass: Symbol
  }

  case class ScavroString(name: String) extends PrimativeScavroParam { val tHClass = StringClass }
  case class ScavroDouble(name: String) extends PrimativeScavroParam { val tHClass = DoubleClass }
  case class ScavroInt(name: String) extends PrimativeScavroParam { val tHClass = IntClass }
  case class ScavroBoolean(name: String) extends PrimativeScavroParam { val tHClass = BooleanClass }
  case class ScavroFloat(name: String) extends PrimativeScavroParam { val tHClass = FloatClass }

  def toScalaParam(param: ScavroParam): ValDef = param match {
    case p: PrimativeScavroParam => PARAM(p.name, p.tHClass)
  }

  def toScalaGetter(param: ScavroParam): Tree = REF(param.name)

  def toJavaGetter(param: ScavroParam): Tree = param match {
    case ScavroString(p) => REF("j") DOT getterName(p) DOT "toString"
    case ScavroDouble(p) => REF("j") DOT getterName(p) DOT "doubleValue"
    case ScavroInt(p) => REF("j") DOT getterName(p)
    case ScavroBoolean(p) => REF("j") DOT getterName(p)
    case ScavroFloat(p) => REF("j") DOT getterName(p) DOT "floatValue"
  }

  def getterName(scalaName: String) = "get" + scalaName.head.toUpper + scalaName.tail

  def buildTree(scalaClassName: String, javaClassName: String, classParams: List[ScavroParam]) = {
    object sym {
      val ScalaClass = RootClass.newClass(scalaClassName)
      val JavaClass = TYPE_REF(REF(javaClassName))
      
      val AvroSerializable = TYPE_REF(REF("AvroSerializeable"))
      val AvroMetdata = TYPE_REF(REF("AvroMetdata")) APPLYTYPE(ScalaClass, JavaClass)
      val Class = TYPE_REF(REF("Class")) APPLYTYPE(JavaClass)
      val AvroReader = TYPE_REF(REF("AvroReader")) APPLYTYPE(ScalaClass)
      val toAvroType = TYPE_REF(LAMBDA(PARAM("j", JavaClass)) ==> TYPE_REF(ScalaClass))
    }

    val scalaClassParams = classParams.map(toScalaParam)
    val scalaClassAccessors = classParams.map(toScalaGetter)
    val javaClassAccessors = classParams.map(toJavaGetter)

    // Defines case class
    val lineItemClassTree = (
      CASECLASSDEF(sym.ScalaClass) withParams(scalaClassParams) withParents(sym.JavaClass)
    ) := BLOCK (
      TYPEVAR("J") := sym.JavaClass,
      DEF("toAvro", sym.JavaClass) withFlags(Flags.OVERRIDE) := BLOCK(
        NEW(sym.JavaClass, scalaClassAccessors: _*)
      )
    ) : Tree

    // Defines companion object
    val lineItemObjectTree = (
      OBJECTDEF(sym.ScalaClass) := BLOCK (
        DEF("reader") withFlags(Flags.IMPLICIT) := NEW(ANONDEF(sym.AvroReader) := BLOCK(
          TYPEVAR("J") := sym.JavaClass
        )),
        VAL("metadata") withFlags(Flags.IMPLICIT) := NEW(ANONDEF(sym.AvroMetdata) := BLOCK(
            VAL("avroClass", TYPE_REF(sym.Class)) withFlags(Flags.OVERRIDE) := REF("classOf") APPLYTYPE(sym.JavaClass),
            VAL("schema") withFlags(Flags.OVERRIDE) withType("Schema") := 
              TYPE_REF(sym.JavaClass) DOT "getClassSchema" APPLY(),
            VAL("fromAvro") withFlags(Flags.OVERRIDE) withType(sym.toAvroType) := BLOCK(
              LAMBDA(PARAM("j", sym.JavaClass)) ==> NEW(sym.ScalaClass, javaClassAccessors: _*)
            )
          )
        )
      )
    ) : Tree

    val schemaImport = IMPORT("org.apache.avro.Schema")
    val scavroImport = IMPORT("oyster.scavro", "AvroMetadata", "AvroReader", "AvroSerializeable")

    BLOCK(schemaImport, scavroImport, lineItemClassTree, lineItemObjectTree) inPackage("oyster.scavrodemo.model")
  }
}
