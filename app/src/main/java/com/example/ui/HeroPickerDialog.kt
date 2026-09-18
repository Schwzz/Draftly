package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Hero
import com.example.model.Role
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroPickerDialog(
  slot: PickerSlot,
  currentHeroId: String?,
  allHeroes: List<Hero>,
  unavailableHeroIds: Set<String>,
  onSelectHero: (String) -> Unit,
  onClearSlot: () -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var roleFilter by remember { mutableStateOf<Role?>(null) }

  val filteredHeroes = remember(searchQuery, roleFilter, allHeroes) {
    allHeroes.filter { hero ->
      val matchesQuery = searchQuery.isBlank() ||
        hero.name.contains(searchQuery.trim(), ignoreCase = true)
      val matchesRole = roleFilter == null || hero.roles.contains(roleFilter)
      matchesQuery && matchesRole
    }.sortedBy { it.name }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.85f)
      .testTag("hero_picker_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Select Hero",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = slot.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close picker")
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search hero name...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear search")
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Role Filter Chips
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          FilterChip(
            selected = roleFilter == null,
            onClick = { roleFilter = null },
            label = { Text("All") }
          )
          Role.entries.forEach { role ->
            FilterChip(
              selected = roleFilter == role,
              onClick = { roleFilter = if (roleFilter == role) null else role },
              label = { Text(role.name) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hero List
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(filteredHeroes, key = { it.id }) { hero ->
            val isUnavailable = unavailableHeroIds.contains(hero.id.lowercase(Locale.ROOT))
            val isSelectedHere = currentHeroId.equals(hero.id, ignoreCase = true)

            HeroPickerItem(
              hero = hero,
              isUnavailable = isUnavailable && !isSelectedHere,
              isSelected = isSelectedHere,
              onClick = {
                if (!isUnavailable || isSelectedHere) {
                  onSelectHero(hero.id)
                }
              }
            )
          }

          if (filteredHeroes.isEmpty()) {
            item {
              Text(
                text = "No heroes found matching \"$searchQuery\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      if (!currentHeroId.isNullOrBlank()) {
        TextButton(
          onClick = {
            onClearSlot()
            onDismiss()
          },
          modifier = Modifier.testTag("clear_slot_button")
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Remove")
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
private fun HeroPickerItem(
  hero: Hero,
  isUnavailable: Boolean,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val cardColors = if (isSelected) {
    CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
  } else if (isUnavailable) {
    CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    )
  } else {
    CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface
    )
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(enabled = !isUnavailable || isSelected, onClick = onClick)
      .testTag("hero_picker_item_${hero.id}"),
    colors = cardColors,
    shape = RoundedCornerShape(8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = hero.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
          if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              Icons.Default.Check,
              contentDescription = "Selected",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
        Text(
          text = hero.roles.joinToString(" • ") { it.displayName },
          style = MaterialTheme.typography.bodySmall,
          color = if (isUnavailable) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
          else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (isUnavailable) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
        ) {
          Text(
            text = "Unavailable",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
