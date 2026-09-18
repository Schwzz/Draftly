package com.example.model

data class Recommendation(
  val hero: Hero,
  val totalScore: Double,
  val counterScore: Double,
  val roleScore: Double,
  val synergyScore: Double,
  val metaScore: Double,
  val reasons: List<String>
)
