import model.*
import simulation.*
import io.circe.Json

@main def getStats(): Unit = {
  val temps = -9 to 50

  val results = for {
    i <- temps
  } yield {
    val cond = Condition(Wind(N, 15.0), 0.5, i.toDouble)
    val grid = Grid.generateRandom(100, cond).triggerFire(50, 50)

    // Nombre de cellules pouvant brûler
    val burnableCells = grid.cells.flatten.count(_.cellType match {
      case Water | Rock => false
      case _ => true
    })

    // Run simulation
    val res: Json = Simulation.run(grid, duration = 100, blitzX = 50, blitzY = 50)
    val lastGridJson = res.asArray.get.last

    // Reconstruct last grid
    val lastGrid = Grid.jsonToGrid(lastGridJson, cond)

    val burnedCells = lastGrid.cells.flatten.count(_.state == Burned)
    val percentBurned = burnedCells.toDouble / burnableCells * 100

    f"Temp: $i°C → $percentBurned%.2f%% brûlé"
  }

  results.foreach(println)
}
