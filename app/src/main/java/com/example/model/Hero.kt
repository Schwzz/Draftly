package com.example.model

data class Hero(
  val id: String,
  val name: String,
  val roles: List<Role>,
  val lanes: List<Lane>,
  val counters: Map<String, Double> = emptyMap(),
  val counteredBy: Map<String, Double> = emptyMap(),
  val synergies: Map<String, Double> = emptyMap(),
  val meta: HeroMeta = HeroMeta(winRate = 50.0, pickRate = 1.0, banRate = 1.0)
)
