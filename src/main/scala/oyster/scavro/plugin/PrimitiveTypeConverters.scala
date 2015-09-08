package oyster.scavro.plugin

object PrimitiveTypeConverters {
  implicit class StringConverter(c: String) {
    def toAvro: String = c.toString
  }

  implicit class BooleanConverter(c: Boolean) {
    def toAvro = new java.lang.Boolean(c)
  }

  implicit class IntConverter(c: Int) {
    def toAvro = new java.lang.Integer(c)
  }

  implicit class LongConverter(c: Long) {
    def toAvro = new java.lang.Long(c)
  }

  implicit class FloatConverter(c: Float) {
    def toAvro = new java.lang.Float(c)
  }

  implicit class DoubleConverter(c: Double) {
    def toAvro = new java.lang.Double(c)
  }
}