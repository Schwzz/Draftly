package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Hero
import com.example.model.Recommendation
import com.example.model.Role

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DraftScreen(
  viewModel: DraftViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val draft = uiState.draftState

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Shield,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "MLBB DRAFT AI",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.resetDraft() },
            modifier = Modifier.testTag("reset_draft_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset Draft")
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. ROLE SELECTION
      item {
        SectionHeader(title = "YOUR ROLE")
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Role.entries.forEach { role ->
            val isSelected = draft.selectedRole == role
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectRole(role) },
              label = { Text(role.displayName) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("role_chip_${role.name.lowercase()}")
            )
          }
        }
      }

      // 2. YOUR TEAM (ALLIED PICKS)
      item {
        SectionHeader(
          title = "YOUR TEAM (ALLIED PICKS)",
          subtitle = "${draft.alliedPicks.filter { it.isNotBlank() }.size} / 5 picked"
        )
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(5) { index ->
            val heroId = draft.alliedPicks.getOrNull(index)
            val hero = heroId?.let { viewModel.getHeroById(it) }
            TeamSlotCard(
              title = "Ally #${index + 1}",
              hero = hero,
              badgeColor = MaterialTheme.colorScheme.primary,
              onSlotClick = { viewModel.openPicker(PickerSlot.AlliedPick(index)) },
              onClearClick = { viewModel.clearSlot(PickerSlot.AlliedPick(index)) },
              testTag = "ally_slot_$index"
            )
          }
        }
      }

      // 3. ENEMY TEAM (ENEMY PICKS)
      item {
        SectionHeader(
          title = "ENEMY TEAM (ENEMY PICKS)",
          subtitle = "${draft.enemyPicks.filter { it.isNotBlank() }.size} / 5 picked"
        )
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(5) { index ->
            val heroId = draft.enemyPicks.getOrNull(index)
            val hero = heroId?.let { viewModel.getHeroById(it) }
            TeamSlotCard(
              title = "Enemy #${index + 1}",
              hero = hero,
              badgeColor = MaterialTheme.colorScheme.error,
              onSlotClick = { viewModel.openPicker(PickerSlot.EnemyPick(index)) },
              onClearClick = { viewModel.clearSlot(PickerSlot.EnemyPick(index)) },
              testTag = "enemy_slot_$index"
            )
          }
        }
      }

      // 4. BANS SECTION
      item {
        BansSection(
          alliedBans = draft.alliedBans,
          enemyBans = draft.enemyBans,
          onOpenPicker = { viewModel.openPicker(it) },
          onClearSlot = { viewModel.clearSlot(it) },
          getHeroById = { viewModel.getHeroById(it) }
        )
      }

      // 5. RECOMMENDATIONS SECTION
      item {
        SectionHeader(
          title = "RECOMMENDED PICKS",
          subtitle = if (draft.selectedRole != null) "Role: ${draft.selectedRole.displayName}" else "All Roles"
        )
      }

      if (uiState.recommendations.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Text(
              text = "No available heroes match current role and bans.",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      } else {
        itemsIndexed(uiState.recommendations, key = { _, rec -> rec.hero.id }) { index, rec ->
          RecommendationCard(
            rank = index + 1,
            recommendation = rec,
            testTag = "recommendation_item_$index"
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // Active Picker Dialog
    uiState.activePickerSlot?.let { slot ->
      val currentId = when (slot) {
        is PickerSlot.AlliedPick -> draft.alliedPicks.getOrNull(slot.index)
        is PickerSlot.EnemyPick -> draft.enemyPicks.getOrNull(slot.index)
        is PickerSlot.AlliedBan -> draft.alliedBans.getOrNull(slot.index)
        is PickerSlot.EnemyBan -> draft.enemyBans.getOrNull(slot.index)
      }

      HeroPickerDialog(
        slot = slot,
        currentHeroId = currentId,
        allHeroes = uiState.allHeroes,
        unavailableHeroIds = draft.unavailableHeroIds,
        onSelectHero = { heroId ->
          viewModel.assignHeroToActiveSlot(heroId)
        },
        onClearSlot = {
          viewModel.clearSlot(slot)
        },
        onDismiss = {
          viewModel.closePicker()
        }
      )
    }
  }
}

@Composable
private fun SectionHeader(title: String, subtitle: String? = null) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 4.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )
    if (subtitle != null) {
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun TeamSlotCard(
  title: String,
  hero: Hero?,
  badgeColor: Color,
  onSlotClick: () -> Unit,
  onClearClick: () -> Unit,
  testTag: String
) {
  OutlinedCard(
    modifier = Modifier
      .width(115.dp)
      .height(105.dp)
      .clickable(onClick = onSlotClick)
      .testTag(testTag),
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(
      width = 1.dp,
      color = if (hero != null) badgeColor else MaterialTheme.colorScheme.outlineVariant
    ),
    colors = CardDefaults.outlinedCardColors(
      containerColor = if (hero != null) badgeColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (hero != null) {
          Icon(
            Icons.Default.Clear,
            contentDescription = "Remove",
            modifier = Modifier
              .size(16.dp)
              .clickable(onClick = onClearClick),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      if (hero != null) {
        Column {
          Text(
            text = hero.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
          Text(
            text = hero.roles.firstOrNull()?.name ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            Icons.Default.Add,
            contentDescription = "Pick Hero",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Pick",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}

@Composable
private fun BansSection(
  alliedBans: List<String>,
  enemyBans: List<String>,
  onOpenPicker: (PickerSlot) -> Unit,
  onClearSlot: (PickerSlot) -> Unit,
  getHeroById: (String) -> Hero?
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "BANS",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Ally: ${alliedBans.filter { it.isNotBlank() }.size} • Enemy: ${enemyBans.filter { it.isNotBlank() }.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Text(
          text = if (isExpanded) "Hide Bans" else "Edit Bans",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Ally Bans",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(5) { index ->
            val heroId = alliedBans.getOrNull(index)
            val hero = heroId?.let { getHeroById(it) }
            BanSlotChip(
              label = "Ally Ban #${index + 1}",
              hero = hero,
              onClick = { onOpenPicker(PickerSlot.AlliedBan(index)) },
              onClear = { onClearSlot(PickerSlot.AlliedBan(index)) },
              testTag = "ally_ban_$index"
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Enemy Bans",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(5) { index ->
            val heroId = enemyBans.getOrNull(index)
            val hero = heroId?.let { getHeroById(it) }
            BanSlotChip(
              label = "Enemy Ban #${index + 1}",
              hero = hero,
              onClick = { onOpenPicker(PickerSlot.EnemyBan(index)) },
              onClear = { onClearSlot(PickerSlot.EnemyBan(index)) },
              testTag = "enemy_ban_$index"
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BanSlotChip(
  label: String,
  hero: Hero?,
  onClick: () -> Unit,
  onClear: () -> Unit,
  testTag: String
) {
  Surface(
    modifier = Modifier
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(6.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    color = if (hero != null) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
    else MaterialTheme.colorScheme.surface
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = hero?.name ?: "+ Ban",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (hero != null) FontWeight.Bold else FontWeight.Normal,
        color = if (hero != null) MaterialTheme.colorScheme.onErrorContainer
        else MaterialTheme.colorScheme.onSurfaceVariant
      )
      if (hero != null) {
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          Icons.Default.Clear,
          contentDescription = "Remove ban",
          modifier = Modifier
            .size(14.dp)
            .clickable(onClick = onClear)
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecommendationCard(
  rank: Int,
  recommendation: Recommendation,
  testTag: String
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(10.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top row: Rank badge, hero name, total score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (rank == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = recommendation.hero.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = recommendation.hero.roles.joinToString(" • ") { it.displayName },
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Score Badge
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${recommendation.totalScore}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "Score",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Score sub-breakdown chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        ScoreChip(label = "Counter", score = recommendation.counterScore)
        ScoreChip(label = "Role", score = recommendation.roleScore)
        ScoreChip(label = "Synergy", score = recommendation.synergyScore)
        ScoreChip(label = "Meta", score = recommendation.metaScore)
      }

      Spacer(modifier = Modifier.height(8.dp))
      Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(8.dp))

      // Reasons
      Text(
        text = "Key Reasons:",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      recommendation.reasons.forEach { reason ->
        val isNegative = reason.contains("weakness", ignoreCase = true) ||
          reason.contains("weak matchup", ignoreCase = true)
        val bulletColor = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        Row(
          modifier = Modifier.padding(vertical = 2.dp),
          verticalAlignment = Alignment.Top
        ) {
          Text(
            text = "• ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = bulletColor
          )
          Text(
            text = reason,
            style = MaterialTheme.typography.bodySmall,
            color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
private fun ScoreChip(label: String, score: Double) {
  AssistChip(
    onClick = {},
    label = {
      Text(
        text = "$label: $score",
        style = MaterialTheme.typography.labelSmall
      )
    },
    colors = AssistChipDefaults.assistChipColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    shape = RoundedCornerShape(4.dp)
  )
}
