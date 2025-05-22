package model

trait Direction
case object N extends Direction
case object S extends Direction
case object E extends Direction
case object W extends Direction
case object NE extends Direction
case object NW extends Direction
case object SE extends Direction
case object SW extends Direction

case class Wind(direction: Direction, strength: Double)

case class Condition(wind: Wind, humidity: Double, temperature: Double)

case object Condition {
  def default: Condition = Condition(Wind(N, 0.0), 0.5, 20.0)
}