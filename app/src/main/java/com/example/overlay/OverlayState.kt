package com.example.overlay

import com.example.model.Recommendation
import com.example.model.Role

data class OverlayState(
  val isActive: Boolean = false,
  val isExpanded: Boolean = false,
  val selectedRole: Role? = null,
  val recommendations: List<Recommendation> = emptyList()
)
