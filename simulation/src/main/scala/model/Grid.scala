package model

import io.circe.Json
import io.circe.syntax._
import simulation.GridStats

final case class Grid(cells: Vector[Vector[Cell]], conditions: Condition) {
  // main method called by the simulation to update the grid every tick
  def update(): Grid = {
    val newCells = cells.map { (row: Vector[Cell]) =>
      row.map { (cell: Cell) =>
        cell.updateCell(this)
      }
    }
    Grid(newCells, conditions)
  }

  def triggerFire(x: Int, y: Int): Grid = {
    val newCells = cells.map { (row: Vector[Cell]) =>
      row.map { (cell: Cell) =>
        if (cell.x == x && cell.y == y || cell.x == x + 1 && cell.y == y + 1
        || cell.x == x - 1 && cell.y == y - 1) {
          cell.triggerCell
        } else {
          cell
        }
      }
    }
    Grid(newCells, conditions)
  }

  def getStats(timeStamp: Int): GridStats = {
    val (burning, burned, unburned) = cells.flatten.foldLeft((0, 0, 0)) {
      case ((b, d, u), cell) => cell.state match
        case Burning  => (b + 1, d, u)
        case Burned   => (b, d + 1, u)
        case Unburned => (b, d, u + 1)
    }
    GridStats(burning, burned, unburned, this.conditions.humidity, this.conditions.temperature, timeStamp)
  }
}

object Grid {
  // Generates a random grid of the given size with a default condition (need to implement another generator with given conditions by user)
  def generateRandom(size: Int, conditions: Condition = Condition.default): Grid = {
    val rand = scala.util.Random
    val cells: Vector[Vector[Cell]] = Vector.tabulate(size, size) { (y, x) =>
      if (x < 3 || y < 3 || x >= size - 3 || y >= size - 3) {
        Cell(x, y, Rock, Unburned)
      } else {
        val cellType: Type = rand.nextDouble() match {
          case p if p < 0.2 => Water
          case p if p < 0.3 => Rock
          case p if p < 0.5 => Grass
          case p if p < 0.7 => Oak
          case _            => Pine
        }
        Cell(x, y, cellType, Unburned)
      }
    }
    Grid(cells, conditions)
  }

  // tx mon frère chatgpt, i hope it works
  // Converts the grid to a JSON object for the frontend
  def gridToJson(grid: Grid): Json = {
    val cellsJson = grid.cells.flatten.map { cell =>
      Json.obj(
        "x"     -> Json.fromInt(cell.x),
        "y"     -> Json.fromInt(cell.y),
        "type"  -> Json.fromString(cell.cellType.toString),
        "state" -> Json.fromString(cell.state.toString)
      )
    }
    Json.obj("cells" -> Json.fromValues(cellsJson))
  }

  def jsonToGrid(json: Json, defaultCondition: Condition = Condition.default): Grid = {
    val cellsArray = json.hcursor.downField("cells").as[List[Json]].getOrElse(Nil)

    val parsedCells = cellsArray.flatMap { cellJson =>
      val cursor = cellJson.hcursor
      for {
        x     <- cursor.get[Int]("x").toOption
        y     <- cursor.get[Int]("y").toOption
        tStr  <- cursor.get[String]("type").toOption
        sStr  <- cursor.get[String]("state").toOption
        cellType <- stringToType(tStr)
        state    <- stringToState(sStr)
      } yield Cell(x, y, cellType, state)
    }

    // Reconstituer la matrice 2D depuis la liste plate
    val byY = parsedCells.groupBy(_.y).toVector.sortBy(_._1).map(_._2)
    val rows: Vector[Vector[Cell]] = byY.map { rowCells =>
      rowCells.sortBy(_.x).toVector
    }

    Grid(rows, defaultCondition)
  }

  // Helpers pour convertir string en Type et State
  def stringToType(s: String): Option[Type] = s match {
    case "Grass" => Some(Grass)
    case "Oak"   => Some(Oak)
    case "Pine"  => Some(Pine)
    case "Water" => Some(Water)
    case "Rock"  => Some(Rock)
    case _       => None
  }

  def stringToState(s: String): Option[State] = s match {
    case "Unburned" => Some(Unburned)
    case "Burning"  => Some(Burning)
    case "Burned"   => Some(Burned)
    case _          => None
  }
}
