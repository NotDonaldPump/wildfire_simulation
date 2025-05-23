package model

class ConditionTest extends munit.FunSuite {

  test("Condition.default has expected default values") {
    val default = Condition.default

    assertEquals(default.wind.direction, N)
    assertEqualsDouble(default.wind.strength, 0.0, 0.0001)
    assertEqualsDouble(default.humidity, 0.5, 0.0001)
    assertEqualsDouble(default.temperature, 20.0, 0.0001)
  }

  test("Wind can be constructed with any direction and strength") {
    val wind = Wind(SW, 2.7)
    assertEquals(wind.direction, SW)
    assertEqualsDouble(wind.strength, 2.7, 0.0001)
  }
}
