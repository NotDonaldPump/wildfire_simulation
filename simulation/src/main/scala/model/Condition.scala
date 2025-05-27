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

case class Condition(wind: Wind, humidity: Double, temperature: Double) {
  def getWindOffset: Tuple2[Int, Int] = {
    this.wind.direction match {
      case N  => (0, 1)
      case S  => (0, -1)
      case E  => (0, -1)
      case W  => (0, 1)
      case NE => (-1, 1)
      case NW => (1, 1)
      case SE => (-1, -1)
      case SW => (1, -1)
    }
  }
}

case object Condition {
  def default: Condition = Condition(Wind(N, 0.0), 0.5, 20.0)
}