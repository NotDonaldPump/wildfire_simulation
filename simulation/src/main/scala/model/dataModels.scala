package model

// data models for the json parser
case class GridParams(
  gridSize: Int,
  humidity: Double,
  temperature: Double,
  windSpeed: Double,
  windDirection: String
)
case class SimuParams(
  duration: Int,
  blitzX: Int,
  blitzY: Int
)
case class SimulationResult(
    result: List[ujson.Value]
)
