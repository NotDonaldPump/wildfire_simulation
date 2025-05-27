package model

final case class Cell private(x: Int, y: Int, cellType: Type, state: State, burnSince: Int = 0) {
    def triggerCell: Cell = Cell(this.x, this.y, this.cellType, Burning, 1)

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
            grid.conditions.humidity + 1.0 * firstDegreeNeig + 0.5 * secondDegreeNeig + 0.25 * thirdDegreeNeig // ajuster les coefficients (normaliser à 1)
        }
    }

    def burnableStuffCheck(grid: Grid): Boolean = {
        val windOffset = grid.conditions.getWindOffset
        val tempMin = -10.0
        val tempMax = 50.0
        val normalizedTemp = ((grid.conditions.temperature - tempMin) / (tempMax - tempMin)).max(0.0).min(1.0)
        val offsets = List((-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1))
        val totalProb = for {
            (dx, dy) <- offsets
            if (grid.cells(this.x + dx)(this.y + dy).state == Burning)
        } yield {
            if (dx == windOffset._1 && dy == windOffset._2) {
                (normalizedTemp * 0.3 + grid.cells(this.x + dx)(this.y + dy).cellType.spreadModifier * 0.35 + grid.conditions.humidity * 0.35) * 2 * grid.conditions.wind.strength
            }
            else {
                normalizedTemp * 0.3 + grid.cells(this.x + dx)(this.y + dy).cellType.spreadModifier * 0.35 + grid.conditions.humidity * 0.35
            }
        }
        if totalProb.sum / (offsets.length + 1) > this.cellType.ignitionThreshold then {
            true
        } else {
            false
        }
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
