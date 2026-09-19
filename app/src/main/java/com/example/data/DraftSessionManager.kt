package com.example.data

import com.example.engine.RecommendationEngine
import com.example.model.DraftState
import com.example.model.Hero
import com.example.model.PickerSlot
import com.example.model.Recommendation
import com.example.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Shared in-memory session manager for draft state and recommendations.
 * Acts as the single source of truth for both DraftViewModel and OverlayService.
 */
object DraftSessionManager {
  private val heroRepository: HeroRepository = LocalHeroRepository()
  private val recommendationEngine: RecommendationEngine = RecommendationEngine()

  val allHeroes: List<Hero> = heroRepository.getAllHeroes()

  private val _draftState = MutableStateFlow(DraftState())
  val draftState: StateFlow<DraftState> = _draftState.asStateFlow()

  private val _recommendations = MutableStateFlow<List<Recommendation>>(emptyList())
  val recommendations: StateFlow<List<Recommendation>> = _recommendations.asStateFlow()

  private val _isOverlayActive = MutableStateFlow(false)
  val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

  init {
    recomputeRecommendations(_draftState.value)
  }

  fun setOverlayActive(active: Boolean) {
    _isOverlayActive.value = active
  }

  fun selectRole(role: Role?) {
    _draftState.update { current ->
      val newRole = if (current.selectedRole == role) null else role
      current.copy(selectedRole = newRole)
    }
    recomputeRecommendations(_draftState.value)
  }

  fun assignHeroToSlot(slot: PickerSlot, heroId: String) {
    _draftState.update { current ->
      applyHeroToSlot(current, slot, heroId)
    }
    recomputeRecommendations(_draftState.value)
  }

  fun clearSlot(slot: PickerSlot) {
    _draftState.update { current ->
      applyHeroToSlot(current, slot, null)
    }
    recomputeRecommendations(_draftState.value)
  }

  fun resetDraft() {
    _draftState.update { current ->
      DraftState(selectedRole = current.selectedRole)
    }
    recomputeRecommendations(_draftState.value)
  }

  fun getHeroById(heroId: String): Hero? {
    return heroRepository.getHeroById(heroId)
  }

  /**
   * Updates a slot at a given index without shifting any subsequent items.
   * Pads the list to at least slotCount (5) if smaller, sets index to heroId or empty string,
   * preserving all other positions.
   */
  fun updateSlotList(
    currentList: List<String>,
    index: Int,
    heroId: String?,
    slotCount: Int = DraftState.SLOT_COUNT
  ): List<String> {
    val mutable = currentList.toMutableList()
    while (mutable.size < slotCount) {
      mutable.add("")
    }
    if (index in 0 until mutable.size) {
      mutable[index] = heroId ?: ""
    }
    return mutable.toList()
  }

  fun applyHeroToSlot(
    draftState: DraftState,
    slot: PickerSlot,
    heroId: String?
  ): DraftState {
    return when (slot) {
      is PickerSlot.AlliedPick -> {
        val updated = updateSlotList(draftState.alliedPicks, slot.index, heroId)
        draftState.copy(alliedPicks = updated)
      }
      is PickerSlot.EnemyPick -> {
        val updated = updateSlotList(draftState.enemyPicks, slot.index, heroId)
        draftState.copy(enemyPicks = updated)
      }
      is PickerSlot.AlliedBan -> {
        val updated = updateSlotList(draftState.alliedBans, slot.index, heroId)
        draftState.copy(alliedBans = updated)
      }
      is PickerSlot.EnemyBan -> {
        val updated = updateSlotList(draftState.enemyBans, slot.index, heroId)
        draftState.copy(enemyBans = updated)
      }
    }
  }

  private fun recomputeRecommendations(draft: DraftState) {
    _recommendations.value = recommendationEngine.recommend(draft, allHeroes, topN = 5)
  }
}
