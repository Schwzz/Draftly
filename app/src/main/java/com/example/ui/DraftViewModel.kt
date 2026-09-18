package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.HeroRepository
import com.example.data.LocalHeroRepository
import com.example.engine.RecommendationEngine
import com.example.model.DraftState
import com.example.model.Hero
import com.example.model.Recommendation
import com.example.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface PickerSlot {
  val title: String
  data class AlliedPick(val index: Int) : PickerSlot {
    override val title: String = "Allied Hero #${index + 1}"
  }
  data class EnemyPick(val index: Int) : PickerSlot {
    override val title: String = "Enemy Hero #${index + 1}"
  }
  data class AlliedBan(val index: Int) : PickerSlot {
    override val title: String = "Allied Ban #${index + 1}"
  }
  data class EnemyBan(val index: Int) : PickerSlot {
    override val title: String = "Enemy Ban #${index + 1}"
  }
}

data class DraftUiState(
  val draftState: DraftState = DraftState(),
  val recommendations: List<Recommendation> = emptyList(),
  val allHeroes: List<Hero> = emptyList(),
  val activePickerSlot: PickerSlot? = null
)

class DraftViewModel(
  private val heroRepository: HeroRepository = LocalHeroRepository(),
  private val recommendationEngine: RecommendationEngine = RecommendationEngine()
) : ViewModel() {

  private val _uiState = MutableStateFlow(DraftUiState())
  val uiState: StateFlow<DraftUiState> = _uiState.asStateFlow()

  init {
    val heroes = heroRepository.getAllHeroes()
    val initialDraft = DraftState()
    val initialRecs = recommendationEngine.recommend(initialDraft, heroes, topN = 5)
    _uiState.value = DraftUiState(
      draftState = initialDraft,
      recommendations = initialRecs,
      allHeroes = heroes
    )
  }

  fun selectRole(role: Role?) {
    _uiState.update { current ->
      val newDraft = current.draftState.copy(
        selectedRole = if (current.draftState.selectedRole == role) null else role
      )
      val newRecs = recommendationEngine.recommend(newDraft, current.allHeroes, topN = 5)
      current.copy(draftState = newDraft, recommendations = newRecs)
    }
  }

  fun openPicker(slot: PickerSlot) {
    _uiState.update { it.copy(activePickerSlot = slot) }
  }

  fun closePicker() {
    _uiState.update { it.copy(activePickerSlot = null) }
  }

  fun assignHeroToActiveSlot(heroId: String) {
    val slot = _uiState.value.activePickerSlot ?: return
    _uiState.update { current ->
      val newDraft = applyHeroToSlot(current.draftState, slot, heroId)
      val newRecs = recommendationEngine.recommend(newDraft, current.allHeroes, topN = 5)
      current.copy(
        draftState = newDraft,
        recommendations = newRecs,
        activePickerSlot = null
      )
    }
  }

  fun clearSlot(slot: PickerSlot) {
    _uiState.update { current ->
      val newDraft = applyHeroToSlot(current.draftState, slot, null)
      val newRecs = recommendationEngine.recommend(newDraft, current.allHeroes, topN = 5)
      current.copy(
        draftState = newDraft,
        recommendations = newRecs,
        activePickerSlot = null
      )
    }
  }

  fun resetDraft() {
    _uiState.update { current ->
      val newDraft = DraftState(selectedRole = current.draftState.selectedRole)
      val newRecs = recommendationEngine.recommend(newDraft, current.allHeroes, topN = 5)
      current.copy(
        draftState = newDraft,
        recommendations = newRecs,
        activePickerSlot = null
      )
    }
  }

  fun getHeroById(heroId: String): Hero? {
    return heroRepository.getHeroById(heroId)
  }

  private fun applyHeroToSlot(
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

  private fun updateSlotList(
    currentList: List<String>,
    index: Int,
    heroId: String?
  ): List<String> {
    // Expand to at least index + 1 if needed
    val mutable = currentList.toMutableList()
    while (mutable.size <= index) {
      mutable.add("")
    }
    if (heroId != null) {
      mutable[index] = heroId
    } else {
      mutable[index] = ""
    }
    // Filter trailing blanks, keeping valid slots
    return mutable.filter { it.isNotBlank() }
  }
}
