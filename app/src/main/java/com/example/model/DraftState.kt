package com.example.model

data class DraftState(
  val selectedRole: Role? = null,
  val alliedPicks: List<String> = emptyList(),
  val enemyPicks: List<String> = emptyList(),
  val alliedBans: List<String> = emptyList(),
  val enemyBans: List<String> = emptyList()
) {
  val unavailableHeroIds: Set<String>
    get() = (alliedPicks + enemyPicks + alliedBans + enemyBans).filter { it.isNotBlank() }.toSet()
}
