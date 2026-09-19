package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DraftSessionManager
import com.example.model.DraftState
import com.example.model.Hero
import com.example.model.PickerSlot
import com.example.model.Recommendation
import com.example.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

typealias PickerSlot = com.example.model.PickerSlot

data class DraftUiState(
  val draftState: DraftState = DraftState(),
  val recommendations: List<Recommendation> = emptyList(),
  val allHeroes: List<Hero> = emptyList(),
  val activePickerSlot: PickerSlot? = null,
  val isOverlayActive: Boolean = false
)

class DraftViewModel(
  private val sessionManager: DraftSessionManager = DraftSessionManager
) : ViewModel() {

  private val _activePickerSlot = MutableStateFlow<PickerSlot?>(null)
  val activePickerSlot: StateFlow<PickerSlot?> = _activePickerSlot.asStateFlow()

  val isOverlayActive: StateFlow<Boolean> = sessionManager.isOverlayActive

  private val _uiState = MutableStateFlow(
    DraftUiState(
      draftState = sessionManager.draftState.value,
      recommendations = sessionManager.recommendations.value,
      allHeroes = sessionManager.allHeroes,
      isOverlayActive = sessionManager.isOverlayActive.value
    )
  )
  val uiState: StateFlow<DraftUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      combine(
        sessionManager.draftState,
        sessionManager.recommendations,
        _activePickerSlot,
        sessionManager.isOverlayActive
      ) { draft, recs, picker, overlayActive ->
        DraftUiState(
          draftState = draft,
          recommendations = recs,
          allHeroes = sessionManager.allHeroes,
          activePickerSlot = picker,
          isOverlayActive = overlayActive
        )
      }.collect { state ->
        _uiState.value = state
      }
    }
  }

  fun selectRole(role: Role?) {
    sessionManager.selectRole(role)
  }

  fun openPicker(slot: PickerSlot) {
    _activePickerSlot.value = slot
  }

  fun closePicker() {
    _activePickerSlot.value = null
  }

  fun assignHeroToActiveSlot(heroId: String) {
    val slot = _activePickerSlot.value ?: return
    sessionManager.assignHeroToSlot(slot, heroId)
    _activePickerSlot.value = null
  }

  fun clearSlot(slot: PickerSlot) {
    sessionManager.clearSlot(slot)
    _activePickerSlot.value = null
  }

  fun resetDraft() {
    sessionManager.resetDraft()
    _activePickerSlot.value = null
  }

  fun getHeroById(heroId: String): Hero? {
    return sessionManager.getHeroById(heroId)
  }

  fun setOverlayActive(active: Boolean) {
    sessionManager.setOverlayActive(active)
  }
}
