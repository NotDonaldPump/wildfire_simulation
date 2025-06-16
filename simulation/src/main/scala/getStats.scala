import model.*
import simulation.*
import io.circe.Json
import java.io.PrintWriter

@main def getStats(): Unit = {
  runSimulationStats(150, "wind", 0 to 50 by 1, 150)
}
def runSimulationStats(gridSize: Int, param: String, range: Range, simulationDuration: Int): Unit = {
  // Paramètres fixes
  val fixedWind = Wind(N, 15.0)
  val fixedHumidity = 0.5
  val fixedTemperature = 15.0
  val ignitionX = gridSize / 3
  val ignitionY = gridSize / 3

  // Créer le fichier de sortie
  val filename = s"results_varying_$param.txt"
  val file = new PrintWriter(filename)

  // Écrire les conditions initiales
  file.println(s"Paramètre étudié : $param")
  file.println(f"Conditions fixes : température = $fixedTemperature%.2f, humidité = $fixedHumidity%.2f, vent = ${fixedWind.direction} à ${fixedWind.strength} km/h")
  file.println(f"Grille : $gridSize x $gridSize, Durée : $simulationDuration itérations\n")

  // Lancer les simulations pour chaque valeur du paramètre étudié
  range.foreach { value =>
    val condition = param match {
      case "temperature" => Condition(fixedWind, fixedHumidity, value.toDouble)
      case "humidity"    => Condition(fixedWind, value.toDouble / 100.0, fixedTemperature)
      case "wind"        => Condition(fixedWind.copy(strength = value.toDouble), fixedHumidity, fixedTemperature)
      case _             => throw new IllegalArgumentException(s"Paramètre non reconnu : $param")
    }

    val grid = Grid.generateRandom(gridSize, condition).triggerFire(ignitionX, ignitionY)
    val burnableCells = grid.cells.flatten.count(_.cellType match {
      case Water | Rock => false
      case _            => true
    })

    val res: Json = Simulation.run(grid, simulationDuration, ignitionX, ignitionY)
    val lastGridJson = res.asArray.get.last
    val lastGrid = Grid.jsonToGrid(lastGridJson, condition)
    val burnedCells = lastGrid.cells.flatten.count(_.state == Burned)
    val percentBurned = burnedCells.toDouble / burnableCells * 100

    val line = f"$param = $value -> $percentBurned%.2f%% brûlé"
    println(line)
    file.println(line)
  }

  file.close()
  println(s"\nSimulation terminée. Résultats écrits dans $filename")
}
