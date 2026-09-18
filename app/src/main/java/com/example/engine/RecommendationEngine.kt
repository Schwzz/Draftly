package com.example.engine

import com.example.model.DraftState
import com.example.model.Hero
import com.example.model.Recommendation
import java.util.Locale
import kotlin.math.roundToInt

class RecommendationEngine(
  private val weights: ScoringWeights = ScoringWeights()
) {

  fun recommend(
    draftState: DraftState,
    allHeroes: List<Hero>,
    topN: Int = 5
  ): List<Recommendation> {
    if (allHeroes.isEmpty()) return emptyList()

    val heroMap = allHeroes.associateBy { it.id.lowercase(Locale.ROOT) }
    val unavailable = draftState.unavailableHeroIds.map { it.lowercase(Locale.ROOT) }.toSet()

    // 1. Hard constraint: Remove unavailable heroes (already picked/banned)
    val availableCandidates = allHeroes.filter { hero ->
      !unavailable.contains(hero.id.lowercase(Locale.ROOT))
    }

    // 2. Hard constraint: Role fit (exclude candidates unable to fill the selected role)
    val roleFilteredCandidates = if (draftState.selectedRole != null) {
      availableCandidates.filter { hero ->
        hero.roles.contains(draftState.selectedRole)
      }
    } else {
      availableCandidates
    }

    val enemyHeroes = draftState.enemyPicks
      .mapNotNull { heroMap[it.lowercase(Locale.ROOT)] }

    val alliedHeroes = draftState.alliedPicks
      .mapNotNull { heroMap[it.lowercase(Locale.ROOT)] }

    // 3. Evaluate each candidate
    val recommendations = roleFilteredCandidates.map { candidate ->
      calculateRecommendation(
        candidate = candidate,
        draftState = draftState,
        enemyHeroes = enemyHeroes,
        alliedHeroes = alliedHeroes
      )
    }

    // 4. Sort descending by total score, then by counter score, then name
    return recommendations
      .sortedWith(
        compareByDescending<Recommendation> { it.totalScore }
          .thenByDescending { it.counterScore }
          .thenBy { it.hero.name }
      )
      .take(topN)
  }

  private fun calculateRecommendation(
    candidate: Hero,
    draftState: DraftState,
    enemyHeroes: List<Hero>,
    alliedHeroes: List<Hero>
  ): Recommendation {
    val reasons = mutableListOf<String>()

    // --- A. COUNTER SCORING (60% weight) ---
    var totalPositiveCounter = 0.0
    var totalNegativeCounter = 0.0

    val counterReasons = mutableListOf<String>()
    val weaknessReasons = mutableListOf<String>()

    for (enemy in enemyHeroes) {
      val enemyId = enemy.id.lowercase(Locale.ROOT)
      val candidateId = candidate.id.lowercase(Locale.ROOT)

      // Check positive counter value (candidate counters enemy)
      val posAdvantage = candidate.counters[enemyId]
        ?: enemy.counteredBy[candidateId]
        ?: 0.0

      if (posAdvantage > 0.0) {
        totalPositiveCounter += posAdvantage
        if (posAdvantage >= 2.0) {
          counterReasons.add("Strong counter to ${enemy.name}")
        } else {
          counterReasons.add("Counters ${enemy.name}")
        }
      }

      // Check negative counter value (enemy counters candidate)
      val negDisadvantage = candidate.counteredBy[enemyId]
        ?: enemy.counters[candidateId]
        ?: 0.0

      if (negDisadvantage > 0.0) {
        totalNegativeCounter += negDisadvantage
        if (negDisadvantage >= 2.0) {
          weaknessReasons.add("Severe weakness against ${enemy.name}")
        } else {
          weaknessReasons.add("Weak matchup against ${enemy.name}")
        }
      }
    }

    val counterScore = if (enemyHeroes.isEmpty()) {
      50.0 // neutral when enemy draft is empty
    } else {
      // Punish vulnerabilities appropriately and reward net counter pressure across enemy team
      val netCounter = totalPositiveCounter - (totalNegativeCounter * 1.25)
      (50.0 + (netCounter * 16.0)).coerceIn(0.0, 100.0)
    }

    // --- B. ROLE FIT SCORING (20% weight) ---
    val roleScore: Double
    if (draftState.selectedRole != null) {
      if (candidate.roles.contains(draftState.selectedRole)) {
        roleScore = 100.0
        reasons.add("Fits ${draftState.selectedRole.displayName}")
      } else {
        roleScore = 0.0
      }
    } else {
      roleScore = 100.0
    }

    // --- C. ALLIED SYNERGY SCORING (10% weight) ---
    var totalSynergy = 0.0
    val synergyReasons = mutableListOf<String>()

    for (ally in alliedHeroes) {
      val allyId = ally.id.lowercase(Locale.ROOT)
      val candidateId = candidate.id.lowercase(Locale.ROOT)

      val syn = candidate.synergies[allyId]
        ?: ally.synergies[candidateId]
        ?: 0.0

      if (syn > 0.0) {
        totalSynergy += syn
        synergyReasons.add("Synergizes with ${ally.name}")
      }
    }

    val synergyScore = if (alliedHeroes.isEmpty()) {
      50.0 // neutral
    } else {
      (50.0 + (totalSynergy * 16.0)).coerceIn(0.0, 100.0)
    }

    // --- D. META SCORING (10% weight) ---
    // Standard baseline at 50% winrate = 50.0 score
    val metaScore = ((candidate.meta.winRate - 50.0) * 5.0 + 50.0).coerceIn(0.0, 100.0)
    if (candidate.meta.winRate >= 52.0) {
      val formattedWr = String.format(Locale.US, "%.1f", candidate.meta.winRate)
      reasons.add("Strong ranked performance ($formattedWr% WR)")
    }

    // Add prioritized reasons: counters first, then weaknesses, synergies, role, meta
    val combinedReasons = mutableListOf<String>()
    combinedReasons.addAll(counterReasons)
    combinedReasons.addAll(weaknessReasons)
    combinedReasons.addAll(synergyReasons)
    combinedReasons.addAll(reasons)

    if (combinedReasons.isEmpty()) {
      combinedReasons.add("Standard draft candidate")
    }

    // --- E. WEIGHTED TOTAL SCORE ---
    val rawTotal = (counterScore * weights.counterWeight) +
      (roleScore * weights.roleWeight) +
      (synergyScore * weights.synergyWeight) +
      (metaScore * weights.metaWeight)

    val roundedTotal = (rawTotal * 10.0).roundToInt() / 10.0
    val roundedCounter = (counterScore * 10.0).roundToInt() / 10.0
    val roundedRole = (roleScore * 10.0).roundToInt() / 10.0
    val roundedSynergy = (synergyScore * 10.0).roundToInt() / 10.0
    val roundedMeta = (metaScore * 10.0).roundToInt() / 10.0

    return Recommendation(
      hero = candidate,
      totalScore = roundedTotal,
      counterScore = roundedCounter,
      roleScore = roundedRole,
      synergyScore = roundedSynergy,
      metaScore = roundedMeta,
      reasons = combinedReasons
    )
  }
}
