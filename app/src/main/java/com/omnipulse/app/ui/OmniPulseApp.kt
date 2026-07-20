package com.omnipulse.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.omnipulse.app.ui.clips.ClipsScreen
import com.omnipulse.app.ui.feed.FeedScreen

private enum class Destination(
    val label: String,
    val icon: ImageVector,
    val supportingText: String,
) {
    Pulse(
        label = "Pulse",
        icon = Icons.Rounded.Home,
        supportingText = "Your social feed",
    ),
    Clips(
        label = "Clips",
        icon = Icons.Rounded.PlayCircle,
        supportingText = "Short videos from across OmniPulse.",
    ),
    Chats(
        label = "Chats",
        icon = Icons.Rounded.ChatBubble,
        supportingText = "Private conversations are coming soon.",
    ),
    Assistant(
        label = "AI",
        icon = Icons.Rounded.AutoAwesome,
        supportingText = "Your Omni assistant will live here.",
    ),
}

@Composable
fun OmniPulseApp() {
    var destinationName by rememberSaveable { mutableStateOf(Destination.Pulse.name) }
    val currentDestination = Destination.valueOf(destinationName)
    val stateHolder = rememberSaveableStateHolder()

    Scaffold(
        bottomBar = {
            NavigationBar {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = destination == currentDestination,
                        onClick = { destinationName = destination.name },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            stateHolder.SaveableStateProvider(currentDestination.name) {
                when (currentDestination) {
                    Destination.Pulse -> FeedScreen()
                    Destination.Clips -> ClipsScreen()
                    else -> ComingSoonScreen(currentDestination)
                }
            }
        }
    }
}

@Composable
private fun ComingSoonScreen(destination: Destination) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = destination.label,
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = destination.supportingText,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
