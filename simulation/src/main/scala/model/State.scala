package model

sealed trait State

case object Unburned extends State
case object Burning extends State
case object Burned extends State
