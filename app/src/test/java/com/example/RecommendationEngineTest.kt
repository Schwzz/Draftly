package com.example

import com.example.data.LocalHeroRepository
import com.example.engine.RecommendationEngine
import com.example.engine.ScoringWeights
import com.example.model.DraftState
import com.example.model.Hero
import com.example.model.HeroMeta
import com.example.model.Lane
import com.example.model.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecommendationEngineTest {

  private lateinit var engine: RecommendationEngine
  private lateinit var repository: LocalHeroRepository
  private lateinit var heroes: List<Hero>

  @Before
  fun setUp() {
    engine = RecommendationEngine()
    repository = LocalHeroRepository()
    heroes = repository.getAllHeroes()
  }

  @Test
  fun testCandidateCounteringMultipleEnemiesReceivesCounterValue() {
    // Enemy has Fanny and Claude (mobile diver and mm)
    // Khufra strongly counters both Fanny (2.5) and Claude (2.0)
    val draftState = DraftState(
      enemyPicks = listOf("fanny", "claude")
    )
    val recommendations = engine.recommend(draftState, heroes)
    val khufraRec = recommendations.find { it.hero.id == "khufra" }

    assertTrue("Khufra should be recommended", khufraRec != null)
    assertTrue("Khufra counter score should be above baseline 50", khufraRec!!.counterScore > 70.0)
    assertTrue("Reasons should state counter to Fanny", khufraRec.reasons.any { it.contains("Fanny") })
    assertTrue("Reasons should state counter to Claude", khufraRec.reasons.any { it.contains("Claude") })
  }

  @Test
  fun testCandidateCounteredByEnemyReceivesPenalty() {
    // Enemy has Diggie
    // Atlas is hard countered by Diggie (-3.0)
    val draftWithDiggie = DraftState(
      selectedRole = Role.ROAM,
      enemyPicks = listOf("diggie")
    )
    val recs = engine.recommend(draftWithDiggie, heroes)
    val atlasRec = recs.find { it.hero.id == "atlas" }

    // Counter score for Atlas should drop below neutral 50.0
    if (atlasRec != null) {
      assertTrue("Atlas counter score should be penalized (< 50.0)", atlasRec.counterScore < 50.0)
      assertTrue("Reasons should contain weakness against Diggie", atlasRec.reasons.any { it.contains("Diggie") })
    }
  }

  @Test
  fun testHeroUnableToFillSelectedRoleNotRecommended() {
    // Player role is Roam
    val draftState = DraftState(
      selectedRole = Role.ROAM
    )
    val recommendations = engine.recommend(draftState, heroes)

    // Gold laners like Claude, Beatrix or Mid mages like Xavier should not appear in Roam
    assertFalse("Claude cannot fill Roam", recommendations.any { it.hero.id == "claude" })
    assertFalse("Xavier cannot fill Roam", recommendations.any { it.hero.id == "xavier" })

    // All recommended heroes must support Role.ROAM
    assertTrue("All recommended heroes must support Roam", recommendations.all { it.hero.roles.contains(Role.ROAM) })
  }

  @Test
  fun testPickedHeroesCannotBeRecommended() {
    val draftState = DraftState(
      alliedPicks = listOf("khufra", "saber"),
      enemyPicks = listOf("franco")
    )
    val recommendations = engine.recommend(draftState, heroes)

    assertFalse("Allied pick Khufra cannot be recommended", recommendations.any { it.hero.id == "khufra" })
    assertFalse("Allied pick Saber cannot be recommended", recommendations.any { it.hero.id == "saber" })
    assertFalse("Enemy pick Franco cannot be recommended", recommendations.any { it.hero.id == "franco" })
  }

  @Test
  fun testBannedHeroesCannotBeRecommended() {
    val draftState = DraftState(
      alliedBans = listOf("khufra"),
      enemyBans = listOf("saber")
    )
    val recommendations = engine.recommend(draftState, heroes)

    assertFalse("Allied ban Khufra cannot be recommended", recommendations.any { it.hero.id == "khufra" })
    assertFalse("Enemy ban Saber cannot be recommended", recommendations.any { it.hero.id == "saber" })
  }

  @Test
  fun testAlliedSynergyContributesSmallerBonusThanMajorCounterAdvantages() {
    // In our scoring weights: Counter = 60%, Synergy = 10%
    val customWeights = ScoringWeights(
      counterWeight = 0.60,
      roleWeight = 0.20,
      synergyWeight = 0.10,
      metaWeight = 0.10
    )
    val testEngine = RecommendationEngine(customWeights)

    val heroCounter = Hero(
      id = "hero_counter",
      name = "Hero Counter",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf("enemy_threat" to 2.5),
      meta = HeroMeta(50.0, 1.0, 1.0)
    )

    val heroSynergyOnly = Hero(
      id = "hero_synergy",
      name = "Hero Synergy",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      synergies = mapOf("ally_friend" to 2.5),
      meta = HeroMeta(50.0, 1.0, 1.0)
    )

    val enemyThreat = Hero("enemy_threat", "Enemy Threat", listOf(Role.JUNGLE), listOf(Lane.JUNGLE))
    val allyFriend = Hero("ally_friend", "Ally Friend", listOf(Role.MID), listOf(Lane.MID_LANE))

    val draft = DraftState(
      selectedRole = Role.ROAM,
      alliedPicks = listOf("ally_friend"),
      enemyPicks = listOf("enemy_threat")
    )

    val recs = testEngine.recommend(draft, listOf(heroCounter, heroSynergyOnly, enemyThreat, allyFriend))

    val counterRec = recs.find { it.hero.id == "hero_counter" }!!
    val synergyRec = recs.find { it.hero.id == "hero_synergy" }!!

    assertTrue(
      "Major counter candidate score (${counterRec.totalScore}) should exceed synergy-only score (${synergyRec.totalScore})",
      counterRec.totalScore > synergyRec.totalScore
    )
  }

  @Test
  fun testMetaStrengthCannotOverpowerPrimaryCounterScore() {
    // High winrate hero without counter matchups vs moderate winrate hero with strong counter matchups
    val heroStrongCounter = Hero(
      id = "strong_counter",
      name = "Strong Counter",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counters = mapOf("enemy_boss" to 2.5),
      meta = HeroMeta(winRate = 50.0, pickRate = 1.0, banRate = 1.0)
    )

    val heroHighMetaWeakCounter = Hero(
      id = "high_meta",
      name = "High Meta",
      roles = listOf(Role.ROAM),
      lanes = listOf(Lane.ROAM),
      counteredBy = mapOf("enemy_boss" to 2.5),
      meta = HeroMeta(winRate = 56.0, pickRate = 5.0, banRate = 5.0)
    )

    val enemyBoss = Hero("enemy_boss", "Enemy Boss", listOf(Role.JUNGLE), listOf(Lane.JUNGLE))

    val draft = DraftState(
      selectedRole = Role.ROAM,
      enemyPicks = listOf("enemy_boss")
    )

    val recs = engine.recommend(draft, listOf(heroStrongCounter, heroHighMetaWeakCounter, enemyBoss))

    val strongCounterRec = recs.find { it.hero.id == "strong_counter" }!!
    val highMetaRec = recs.find { it.hero.id == "high_meta" }!!

    assertTrue(
      "Strong counter hero (${strongCounterRec.totalScore}) should beat high meta hero that is severely countered (${highMetaRec.totalScore})",
      strongCounterRec.totalScore > highMetaRec.totalScore
    )
  }

  @Test
  fun testRecommendationsSortedByTotalScore() {
    val draftState = DraftState(
      enemyPicks = listOf("fanny", "claude")
    )
    val recs = engine.recommend(draftState, heroes)

    for (i in 0 until recs.size - 1) {
      assertTrue(
        "Recommendations must be sorted descending: ${recs[i].totalScore} >= ${recs[i+1].totalScore}",
        recs[i].totalScore >= recs[i + 1].totalScore
      )
    }
  }

  @Test
  fun testEngineReturnsNoUnavailableHeroes() {
    val draftState = DraftState(
      alliedPicks = listOf("khufra", "saber", "franco"),
      enemyPicks = listOf("tigreal", "atlas"),
      alliedBans = listOf("diggie", "lolita"),
      enemyBans = listOf("minotaur", "kaja")
    )

    val recs = engine.recommend(draftState, heroes)
    val recIds = recs.map { it.hero.id }

    for (unavailableId in draftState.unavailableHeroIds) {
      assertFalse("Unavailable hero $unavailableId must not be in recommendations", recIds.contains(unavailableId))
    }
  }

  @Test
  fun testEngineHandlesEmptyOrIncompleteDraftsSafely() {
    // Completely empty draft
    val emptyDraft = DraftState()
    val recsEmpty = engine.recommend(emptyDraft, heroes)
    assertEquals("Should return top 5 for empty draft", 5, recsEmpty.size)

    // Incomplete draft with 1 pick, 0 bans
    val partialDraft = DraftState(
      enemyPicks = listOf("fanny")
    )
    val recsPartial = engine.recommend(partialDraft, heroes)
    assertEquals(5, recsPartial.size)
  }

  @Test
  fun testScenarioXavierJulianTerizlaVsFannyYveClaudeForRoam() {
    // ALLIED: Xavier, Julian, Terizla
    // ENEMY: Fanny, Yve, Claude
    // PLAYER ROLE: Roam
    val draftState = DraftState(
      selectedRole = Role.ROAM,
      alliedPicks = listOf("xavier", "julian", "terizla"),
      enemyPicks = listOf("fanny", "yve", "claude")
    )

    val recs = engine.recommend(draftState, heroes)

    // Must return up to 5 recommendations
    assertTrue("Should return recommendations", recs.isNotEmpty())
    assertTrue("Should return at most 5 recommendations", recs.size <= 5)

    // All candidates must be valid Roam heroes
    assertTrue("All recommended candidates must be capable of filling Roam", recs.all { it.hero.roles.contains(Role.ROAM) })

    // None of the picked heroes can be in recommendations
    val allPicked = listOf("xavier", "julian", "terizla", "fanny", "yve", "claude")
    for (picked in allPicked) {
      assertFalse("Picked hero $picked must not be in recommendations", recs.any { it.hero.id == picked })
    }

    // Roam heroes that strongly counter Fanny, Yve, Claude (such as Saber, Khufra, Lolita) should evaluate favorably
    val saberRec = recs.find { it.hero.id == "saber" }
    val khufraRec = recs.find { it.hero.id == "khufra" }

    assertTrue("Saber or Khufra should be evaluated among top recommendations for this matchup", saberRec != null || khufraRec != null)

    // Tigreal is heavily countered by Yve's extreme slows and Claude's mobility, so its counter score should be lower than Saber/Khufra
    val tigrealRec = recs.find { it.hero.id == "tigreal" }
    if (tigrealRec != null && saberRec != null) {
      assertTrue("Saber counter score (${saberRec.counterScore}) should be higher than Tigreal (${tigrealRec.counterScore}) vs Fanny/Yve/Claude",
        saberRec.counterScore > tigrealRec.counterScore)
    }
  }
}
