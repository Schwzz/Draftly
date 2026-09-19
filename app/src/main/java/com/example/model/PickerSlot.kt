package com.example.model

sealed interface PickerSlot {
  val title: String
  val index: Int

  data class AlliedPick(override val index: Int) : PickerSlot {
    override val title: String = "Allied Hero #${index + 1}"
  }
  data class EnemyPick(override val index: Int) : PickerSlot {
    override val title: String = "Enemy Hero #${index + 1}"
  }
  data class AlliedBan(override val index: Int) : PickerSlot {
    override val title: String = "Allied Ban #${index + 1}"
  }
  data class EnemyBan(override val index: Int) : PickerSlot {
    override val title: String = "Enemy Ban #${index + 1}"
  }
}
