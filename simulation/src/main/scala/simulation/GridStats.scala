package simulation

import model.*

case class GridStats(burningCells: Int,
    burnedCells: Int,
    safeCells: Int,
    humidity: Double,
    temperature: Double,
    timeStamp: Int) {
    def toJson: String = ???
}

