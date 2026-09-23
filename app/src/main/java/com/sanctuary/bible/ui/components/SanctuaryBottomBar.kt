package com.sanctuary.bible.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sanctuary.bible.ui.navigation.Screen

@Composable
fun SanctuaryBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Screen.bottomNavScreens.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val activeColor = MaterialTheme.colorScheme.primary
            val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigate(screen) }
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            else Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = if (isSelected) activeColor else inactiveColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) activeColor else inactiveColor,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
