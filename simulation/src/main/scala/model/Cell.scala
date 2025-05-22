package model

final case class Cell private(x: Int, y: Int, cellType: Type, state: State, burnSince: Int = 0) {
    // main method called by the grid to update the cell
    def updateCell(grid: Grid): Cell = this.cellType.match {
        case Water => this
        case Rock => this
        case _ => updateChangingCell(grid)
    }

    // method to get the humidity of the cell according to the humidity of the neighbors
    // i need to think if I want to call this method every iteration or once at the beginning (depends if I make the
    // humidity in the grid conditions static or dynamic)
    // do i make the surrounding burning cells affect the humidity of the cell?
    def getCellHumidity(grid: Grid): Double = this.cellType match {
        case Water => 1.0
        case Rock => 0.0
        case _ => {
            val firstDegreeNeig = (for {
                x <- this.x - 1 to this.x + 1
                y <- this.y - 1 to this.y + 1
                if grid.cells(x)(y).cellType == Water
            } yield true).length
            val secondDegreeNeig = (for {
                x <- this.x - 2 to this.x + 2
                y <- this.y - 2 to this.y + 2
                if grid.cells(x)(y) == Water
            } yield true).length - firstDegreeNeig
            val thirdDegreeNeig = (for {
                x <- this.x - 3 to this.x + 3
                y <- this.y - 3 to this.y + 3
                if grid.cells(x)(y) == Water
            } yield true).length - firstDegreeNeig - secondDegreeNeig
            grid.conditions.humidity + 1.0 * firstDegreeNeig + 0.5 * secondDegreeNeig + 0.25 * thirdDegreeNeig // ajuster les coefficients
        }
    }

    def burnableStuffCheck(grid: Grid): Boolean = {
        true // todo : implement this (Most important part of the simulation)
    }

    def shouldIStartBurning(grid: Grid): Boolean = this.cellType match {
        case Water => false
        case Rock => false
        case _ => burnableStuffCheck(grid)
    }

    def updateChangingCell(grid: Grid): Cell = {
        val startBurning: Boolean = shouldIStartBurning(grid)
        this.state match {
            case Unburned => if (startBurning) Cell(this.x, this.y, this.cellType, Burning, burnSince = 1) else this
            case Burning => {
                val newBurnSince = this.burnSince + 1
                if (newBurnSince >= this.cellType.burnDuration) {
                    Cell(this.x, this.y, this.cellType, Burned)
                } else {
                    Cell(this.x, this.y, this.cellType, Burning, newBurnSince)
                }
            }
            case Burned => this
        }
    }
}

// public constructor because i don't want to make burnSince public
object Cell {
  def apply(x: Int, y: Int, cellType: Type, state: State): Cell =
    Cell(x, y, cellType, state, burnSince = 0)
}
