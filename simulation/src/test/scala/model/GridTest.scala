package model

import scala.collection.mutable

class GridTest extends munit.FunSuite {

  test("Grid.generateRandom has expected default values and a specific size") {
    val grid = Grid.generateRandom(10)

    assertEquals(grid.cells.length, 10)
    assertEquals(grid.cells(0).length, 10)
    assertEquals(grid.conditions, Condition.default)
  }

  test("Grid.generateRandom creates a grid with speficic probabilities") {
    val grid2 = Grid.generateRandom(1000)
    val cellsNumber = 1000 * 1000
    val map = mutable.Map[String, Tuple2[Int, Double]]().withDefaultValue((0, 0.0))

    for (row <- grid2.cells; cell <- row) {
        val key = cell.cellType.toString
        val (count, _) = map(key)
        map(key) = (count + 1, 0.0)
    }

    for ((key, (count, _)) <- map) {
        map(key) = (count, count.toDouble / cellsNumber)
    }

    assertEquals(grid2.cells.length, 1000)
    assertEquals(grid2.cells(0).length, 1000)
    assertEqualsDouble(map("Water")._2, 0.2, 0.1)
    assertEqualsDouble(map("Rock")._2, 0.2, 0.1)
    assertEqualsDouble(map("Grass")._2, 0.2, 0.1)
    assertEqualsDouble(map("Oak")._2, 0.2, 0.1)
    assertEqualsDouble(map("Pine")._2, 0.3, 0.1)
  }
}
