package model

import io.circe.Json
import io.circe.syntax._

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
        if (cell.x == x && cell.y == y) {
          cell.triggerCell
        } else {
          cell
        }
      }
    }
    Grid(newCells, conditions)
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
}
