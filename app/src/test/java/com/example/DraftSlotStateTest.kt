package com.example

import com.example.data.DraftSessionManager
import com.example.data.LocalHeroRepository
import com.example.engine.RecommendationEngine
import com.example.model.DraftState
import com.example.model.PickerSlot
import com.example.model.Role
import com.example.overlay.OverlayController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DraftSlotStateTest {

  private lateinit var sessionManager: DraftSessionManager
  private lateinit var overlayController: OverlayController

  @Before
  fun setUp() {
    sessionManager = DraftSessionManager
    sessionManager.resetDraft()
    sessionManager.selectRole(null)
    overlayController = OverlayController(sessionManager)
  }

  @Test
  fun `clearing first allied slot preserves later slot positions without shifting`() {
    // Setup: allied slots with Xavier, Julian, Terizla
    val initialPicks = listOf("xavier", "julian", "terizla", "", "")
    val updated = sessionManager.updateSlotList(initialPicks, 0, null)

    assertEquals(5, updated.size)
    assertEquals("", updated[0])
    assertEquals("julian", updated[1])
    assertEquals("terizla", updated[2])
    assertEquals("", updated[3])
    assertEquals("", updated[4])
  }

  @Test
  fun `clearing middle slot preserves earlier and later positions`() {
    val initialPicks = listOf("xavier", "julian", "terizla", "", "")
    val updated = sessionManager.updateSlotList(initialPicks, 1, null)

    assertEquals(5, updated.size)
    assertEquals("xavier", updated[0])
    assertEquals("", updated[1])
    assertEquals("terizla", updated[2])
    assertEquals("", updated[3])
    assertEquals("", updated[4])
  }

  @Test
  fun `assigning a hero to an empty first slot preserves later positions`() {
    val currentPicks = listOf("", "julian", "terizla", "", "")
    val updated = sessionManager.updateSlotList(currentPicks, 0, "nathan")

    assertEquals(5, updated.size)
    assertEquals("nathan", updated[0])
    assertEquals("julian", updated[1])
    assertEquals("terizla", updated[2])
    assertEquals("", updated[3])
    assertEquals("", updated[4])
  }

  @Test
  fun `slot clearing applies consistently to all 4 picker slot types`() {
    var state = DraftState(
      alliedPicks = listOf("xavier", "julian", "terizla", "", ""),
      enemyPicks = listOf("fanny", "claude", "tigreal", "", ""),
      alliedBans = listOf("ling", "nolan", "", "", ""),
      enemyBans = listOf("mathilda", "angela", "", "", "")
    )

    // Clear ally pick index 0
    state = sessionManager.applyHeroToSlot(state, PickerSlot.AlliedPick(0), null)
    assertEquals("", state.alliedPicks[0])
    assertEquals("julian", state.alliedPicks[1])
    assertEquals("terizla", state.alliedPicks[2])
    assertEquals(5, state.alliedPicks.size)

    // Clear enemy pick index 1
    state = sessionManager.applyHeroToSlot(state, PickerSlot.EnemyPick(1), null)
    assertEquals("fanny", state.enemyPicks[0])
    assertEquals("", state.enemyPicks[1])
    assertEquals("tigreal", state.enemyPicks[2])
    assertEquals(5, state.enemyPicks.size)

    // Clear allied ban index 0
    state = sessionManager.applyHeroToSlot(state, PickerSlot.AlliedBan(0), null)
    assertEquals("", state.alliedBans[0])
    assertEquals("nolan", state.alliedBans[1])
    assertEquals(5, state.alliedBans.size)

    // Clear enemy ban index 0
    state = sessionManager.applyHeroToSlot(state, PickerSlot.EnemyBan(0), null)
    assertEquals("", state.enemyBans[0])
    assertEquals("angela", state.enemyBans[1])
    assertEquals(5, state.enemyBans.size)
  }

  @Test
  fun `overlay controller handles lifecycle and UI transitions cleanly`() {
    val controller = OverlayController(sessionManager)

    // Initially inactive and collapsed
    assertFalse(controller.state.value.isActive)
    assertFalse(controller.state.value.isExpanded)

    // Start overlay -> active, collapsed
    controller.startOverlay()
    assertTrue(controller.state.value.isActive)
    assertFalse(controller.state.value.isExpanded)
    assertTrue(sessionManager.isOverlayActive.value)

    // Expand
    controller.expand()
    assertTrue(controller.state.value.isExpanded)

    // Minimize
    controller.minimize()
    assertFalse(controller.state.value.isExpanded)

    // Toggle expand
    controller.toggleExpand()
    assertTrue(controller.state.value.isExpanded)
    controller.toggleExpand()
    assertFalse(controller.state.value.isExpanded)

    // Stop overlay
    controller.stopOverlay()
    assertFalse(controller.state.value.isActive)
    assertFalse(controller.state.value.isExpanded)
    assertFalse(sessionManager.isOverlayActive.value)
  }

  @Test
  fun `recommendation engine processes draft state with empty slots correctly`() {
    val engine = RecommendationEngine()
    val repo = LocalHeroRepository()
    val allHeroes = repo.getAllHeroes()

    // Enemy has fanny in index 1, empty index 0
    val draftState = DraftState(
      selectedRole = Role.ROAM,
      enemyPicks = listOf("", "fanny", "", "", ""),
      alliedPicks = listOf("", "claude", "", "", "")
    )

    val recommendations = engine.recommend(draftState, allHeroes, topN = 5)
    assertTrue("Recommendations should not be empty", recommendations.isNotEmpty())

    // Khufra and Franco are strong counters against Fanny
    val heroIds = recommendations.map { it.hero.id }
    assertTrue(
      "Expected counter pick against Fanny like Khufra or Franco in recommendations",
      heroIds.contains("khufra") || heroIds.contains("franco") || heroIds.contains("minotaur")
    )
  }
}
