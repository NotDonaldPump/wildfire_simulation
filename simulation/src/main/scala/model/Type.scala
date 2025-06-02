package model

sealed trait Type {
  def burnDuration: Int
  def ignitionThreshold: Double
  def spreadModifier: Double
}

// todo : adjust values

case object Grass extends Type {
  val burnDuration: Int = 5
  val ignitionThreshold: Double = 0.5
  val spreadModifier: Double = 0.3
}

case object Oak extends Type {
  val burnDuration: Int = 10
  val ignitionThreshold: Double = 0.4
  val spreadModifier: Double = 0.7
}

case object Pine extends Type {
  val burnDuration: Int = 8
  val ignitionThreshold: Double = 0.2
  val spreadModifier: Double = 0.9
}

case object Water extends Type {
  val burnDuration: Int = 0
  val ignitionThreshold: Double = 1.0
  val spreadModifier: Double = 0.0
}

case object Rock extends Type {
  val burnDuration: Int = 0
  val ignitionThreshold: Double = 1.0
  val spreadModifier: Double = 0.0
}
