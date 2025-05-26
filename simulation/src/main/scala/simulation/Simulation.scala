package simulation

import io.circe.Json
import model.*

object Simulation {
  def run(grid: Grid, duration: Int, blitzX: Int, blitzY: Int): Json =
    @annotation.tailrec
    def loop(current: Grid, remaining: Int, acc: List[Json]): List[Json] =
      if (remaining <= 0) acc.reverse
      else if (remaining == duration) { 
        val next = grid.triggerFire(blitzX, blitzY)
        loop(next, remaining - 1, Grid.gridToJson(next) :: acc)
      }
      else
        val next = current.update()
        loop(next, remaining - 1, Grid.gridToJson(next) :: acc)

    Json.arr(loop(grid, duration, Nil)*)
}