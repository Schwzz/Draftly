package com.example.model

data class DraftState(
  val selectedRole: Role? = null,
  val alliedPicks: List<String> = List(SLOT_COUNT) { "" },
  val enemyPicks: List<String> = List(SLOT_COUNT) { "" },
  val alliedBans: List<String> = List(SLOT_COUNT) { "" },
  val enemyBans: List<String> = List(SLOT_COUNT) { "" }
) {
  companion object {
    const val SLOT_COUNT = 5
  }

  val unavailableHeroIds: Set<String>
    get() = (alliedPicks + enemyPicks + alliedBans + enemyBans).filter { it.isNotBlank() }.toSet()
}
