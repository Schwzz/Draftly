package com.example.overlay

import com.example.data.DraftSessionManager
import com.example.model.Recommendation
import com.example.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages the overlay UI state and transitions (active/inactive, collapsed/expanded).
 * Pure Kotlin class easily unit tested.
 */
class OverlayController(
  private val sessionManager: DraftSessionManager = DraftSessionManager
) {
  private val _state = MutableStateFlow(OverlayState())
  val state: StateFlow<OverlayState> = _state.asStateFlow()

  fun startOverlay() {
    sessionManager.setOverlayActive(true)
    _state.update {
      it.copy(
        isActive = true,
        isExpanded = false,
        selectedRole = sessionManager.draftState.value.selectedRole,
        recommendations = sessionManager.recommendations.value
      )
    }
  }

  fun stopOverlay() {
    sessionManager.setOverlayActive(false)
    _state.update {
      it.copy(isActive = false, isExpanded = false)
    }
  }

  fun expand() {
    _state.update { it.copy(isExpanded = true) }
  }

  fun minimize() {
    _state.update { it.copy(isExpanded = false) }
  }

  fun toggleExpand() {
    _state.update { it.copy(isExpanded = !it.isExpanded) }
  }

  fun updateDraftData(role: Role?, recommendations: List<Recommendation>) {
    _state.update {
      it.copy(selectedRole = role, recommendations = recommendations)
    }
  }
}
