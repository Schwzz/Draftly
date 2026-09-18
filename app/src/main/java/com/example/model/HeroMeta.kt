package com.example.model

data class HeroMeta(
  val winRate: Double,
  val pickRate: Double,
  val banRate: Double,
  val patch: String = "1.9.20",
  val timestamp: String = "2026-09"
)
