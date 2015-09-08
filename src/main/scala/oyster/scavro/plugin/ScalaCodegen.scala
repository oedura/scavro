package oyster.scavro.plugin

import treehugger.forest._
import definitions._
import treehuggerDSL._

/*

import treehugger.forest._, definitions._, treehuggerDSL._
import oyster.scavro.plugin.ScalaCodegen
import oyster.scavro.plugin.ScalaCodegen._

val optionType = ScavroParam("price", new ScavroArray(ScavroDouble))
val paramList = ScavroString("name") :: optionType :: ScavroInt("quantity") :: Nil
println(treeToString(ScalaCodegen.buildTree("LineItem", "oyster.scavrodemo.idl.LineItem", paramList)))

*/

object ScalaCodegen {

  /* ScavroParam */  case class ScavroParam(name: String, paramType: ScavroParamType) {
    def scalaParam: ValDef = paramType.scalaParam(name)
    def javaGetter: Tree = paramType.javaGetter(name)
    def scalaGetter: Tree = paramType.scalaGetter(name)
    def builderCommand: Tree = paramType.builderCommand(name)
  }

  sealed trait ScavroParamType {
    // Used for the definition of the param: foo: Double -- PARAM("foo", TYPE_REF(DoubleClass))
    def scalaParam(name: String): ValDef
    // get from java object: j.getFoo()
    def javaGetter(name: String): Tree = REF("j") DOT getterName(name)
    // gets value from scala type instances: name
    def scalaGetter(name: String): Tree = REF(name)
    // Builder command: builder.setFoo(foo)
    def builderCommand(name: String): Tree = REF("builder") DOT setterName(name) APPLY scalaGetter(name)
    // Mapper to convert java type to scala type: Foo(_) or _.toDouble
    def javaConverter: Tree
    // Type ref: TYPE_REF(DoubleClass)
    def scalaType: Type
  }

  /* Primitives */
  sealed trait PrimativeScavroParam extends ScavroParamType {
    val thClass: Symbol
    def scalaType = TYPE_REF(thClass)
    def scalaParam(name: String): ValDef = PARAM(name, this.scalaType)
  }

  object ScavroString extends PrimativeScavroParam { 
    val thClass = StringClass
    override def javaGetter(name: String): Tree = REF("j") DOT getterName(name) DOT "toString"
    def javaConverter = WILDCARD DOT "toString"
    def apply(name: String) = new ScavroParam(name, this)
  }

  object ScavroDouble extends PrimativeScavroParam { 
    val thClass = DoubleClass 
    def javaConverter = WILDCARD DOT "toDouble"
    def apply(name: String) = new ScavroParam(name, this)
  }

  object ScavroInt extends PrimativeScavroParam { 
    val thClass = IntClass 
    def javaConverter = WILDCARD DOT "toInt"
    def apply(name: String) = new ScavroParam(name, this)
  }

  object ScavroBoolean extends PrimativeScavroParam { 
    val thClass = BooleanClass
    def javaConverter = WILDCARD DOT "toBoolean"
    def apply(name: String) = new ScavroParam(name, this)
  }

  object ScavroFloat extends PrimativeScavroParam { 
    val thClass = FloatClass 
    def javaConverter = WILDCARD DOT "toFloat"
    def apply(name: String) = new ScavroParam(name, this)
  }

  object ScavroLong extends PrimativeScavroParam {
    val thClass = LongClass
    def javaConverter = WILDCARD DOT "toLong"
    def apply(name: String) = new ScavroParam(name, this)
  }

  /* Complex types */
  class ScavroOption(underlying: ScavroParamType) extends ScavroParamType {
    def scalaType: Type = TYPE_OPTION(underlying.scalaType)
    def scalaParam(name: String): ValDef = PARAM(name, this.scalaType)
    def javaConverter: Tree = REF("Option") APPLY (WILDCARD) MAP (underlying.javaConverter)
    override def javaGetter(name: String): Tree = REF("Option") APPLY (REF("j") DOT getterName(name))
    override def builderCommand(name: String): Tree = (
      IF (scalaGetter(name) DOT "isDefined") 
      THEN (REF("builder") DOT setterName(name) APPLY (scalaGetter(name) DOT "get")) 
      ELSE (REF("builder") DOT "clearPublicationDate" APPLY())
    )
  }

  object ScavroOption {
    def apply(name: String, underlying: ScavroParamType) = new ScavroParam(name, new ScavroOption(underlying))
  }

  class ScavroArray(underlying: ScavroParamType) extends ScavroParamType {
    def scalaType: Type = TYPE_ARRAY(underlying.scalaType)
    def scalaParam(name: String): ValDef = PARAM(name, this.scalaType)
    override def javaGetter(name: String): Tree = REF("j") DOT getterName(name) MAP (underlying.javaConverter)
    override def scalaGetter(name: String): Tree = REF(name) MAP (WILDCARD DOT "toAvro")
    def javaConverter: Tree = WILDCARD MAP (underlying.javaConverter)
  }

  // Converts a scala accessor method name of the form `name` to a java accessor method name of the form `getName`
  def getterName(scalaName: String) = "get" + scalaName.head.toUpper + scalaName.tail
  def setterName(scalaName: String) = "set" + scalaName.head.toUpper + scalaName.tail

  /********************************************************************************/

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

    val scalaClassParams = classParams.map(_.scalaParam)
    val scalaClassAccessors = classParams.map(_.scalaGetter)
    val javaClassAccessors = classParams.map(_.javaGetter)

    val builderStatements = classParams.map(_.builderCommand)
    val initilizeBuilderTree: Tree = VAL("builder") := sym.JavaClass DOT "newBuilder" APPLY()
    val finalizeBuilderTree: Tree = REF("builder") DOT "build" APPLY()

    // Defines case class
    val lineItemClassTree = (
      CASECLASSDEF(sym.ScalaClass) withParams(scalaClassParams) withParents(sym.JavaClass)
    ) := BLOCK (
      TYPEVAR("J") := sym.JavaClass,
      DEF("toAvro", sym.JavaClass) withFlags(Flags.OVERRIDE) := BLOCK(
        // NEW(sym.JavaClass, scalaClassAccessors: _*)
        initilizeBuilderTree +: builderStatements :+ finalizeBuilderTree
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
            VAL("fromAvro") withFlags(Flags.OVERRIDE) := BLOCK(
              LAMBDA(PARAM("j", sym.JavaClass)) ==> NEW(sym.ScalaClass, javaClassAccessors: _*)
            )
          )
        )
      )
    ) : Tree

    val schemaImport = IMPORT("org.apache.avro.Schema")
    val scavroImport = IMPORT("oyster.scavro", "AvroMetadata", "AvroReader", "AvroSerializeable")
    val primitiveConverters = IMPORT("oyster.scavro.plugin.PrimitiveTypeConverters", "_")

    BLOCK(schemaImport, scavroImport, primitiveConverters,
      lineItemClassTree, lineItemObjectTree) inPackage("oyster.scavrodemo.model")
  }
}
