package com.example.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Recommendation
import com.example.model.Role

@Composable
fun OverlayContent(
  isExpanded: Boolean,
  selectedRole: Role?,
  recommendations: List<Recommendation>,
  onToggleExpand: () -> Unit,
  onMinimize: () -> Unit,
  onStopOverlay: () -> Unit,
  onOpenApp: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (!isExpanded) {
    // Collapsed state: Compact circular floating button [D]
    Box(
      modifier = modifier
        .size(54.dp)
        .shadow(8.dp, CircleShape)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary)
        .border(2.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f), CircleShape)
        .clickable(onClick = onToggleExpand)
        .testTag("overlay_collapsed_button"),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
          text = "D",
          color = MaterialTheme.colorScheme.onPrimary,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp
        )
      }
    }
  } else {
    // Expanded state: Compact panel
    Card(
      modifier = modifier
        .width(280.dp)
        .shadow(12.dp, RoundedCornerShape(14.dp))
        .testTag("overlay_expanded_panel"),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        // Header: Draftly with action buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onOpenApp)
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Draftly Icon",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Draftly",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.OpenInNew,
              contentDescription = "Open App",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
          }

          IconButton(
            onClick = onMinimize,
            modifier = Modifier.size(28.dp).testTag("overlay_minimize_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Minimize",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Divider(
          modifier = Modifier.padding(vertical = 8.dp),
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Role line
        Text(
          text = "Role: ${selectedRole?.displayName ?: "All Roles"}",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Top recommendations section
        Text(
          text = "TOP RECOMMENDATIONS",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        val topRecs = recommendations.take(3)
        if (topRecs.isEmpty()) {
          Text(
            text = "No heroes match current role and bans.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
          )
        } else {
          topRecs.forEachIndexed { index, rec ->
            val rank = index + 1
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(20.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = rec.hero.name,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "${rec.hero.lanes.firstOrNull()?.displayName ?: rec.hero.roles.firstOrNull()?.displayName ?: ""} • Score: ${rec.totalScore.toInt()}%",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Divider(
          modifier = Modifier.padding(vertical = 8.dp),
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Action Buttons: [Minimize] and [Stop Overlay]
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onMinimize,
            modifier = Modifier.weight(1f).height(36.dp).testTag("overlay_minimize_action"),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text("Minimize", style = MaterialTheme.typography.labelMedium)
          }

          Button(
            onClick = onStopOverlay,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.weight(1f).height(36.dp).testTag("overlay_stop_action"),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text("Stop Overlay", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }
  }
}
