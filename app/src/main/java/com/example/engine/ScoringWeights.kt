package com.example.engine

data class ScoringWeights(
  val counterWeight: Double = 0.60,
  val roleWeight: Double = 0.20,
  val synergyWeight: Double = 0.10,
  val metaWeight: Double = 0.10
)
