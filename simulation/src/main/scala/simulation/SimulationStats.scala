package simulation
import model.*

case class SimulationStats(
    initialHumidity: Double,
    initialTemperature: Double,
    initialWindSpeed: Double,
    initialWindDirection: String,
    allGridStats: List[GridStats],
)

object SimulationStats {
    def updateStats(currentStats: SimulationStats, currentGrid: Grid, timeStamp: Int): SimulationStats = {
        SimulationStats(
            initialHumidity = currentStats.initialHumidity,
            initialTemperature = currentStats.initialTemperature,
            initialWindSpeed = currentStats.initialWindSpeed,
            initialWindDirection = currentStats.initialWindDirection,
            allGridStats = currentStats.allGridStats :+ currentGrid.getStats(timeStamp)
        )
    }

    def exportToJson(stats: SimulationStats): String = ???
}
