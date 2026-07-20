package com.omnipulse.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.omnipulse.app.ui.navigation.Routes
import com.omnipulse.app.ui.screens.AiChatScreen
import com.omnipulse.app.ui.screens.AuthScreen
import com.omnipulse.app.ui.screens.ClipsScreen
import com.omnipulse.app.ui.screens.ConversationScreen
import com.omnipulse.app.ui.screens.FeedScreen
import com.omnipulse.app.ui.screens.MessagesScreen

private data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun OmniPulseApp() {
    var signedIn by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()

    if (!signedIn) {
        AuthScreen(onSignedIn = { signedIn = true })
        return
    }

    val tabs = remember {
        listOf(
            TabItem(Routes.FEED, "Feed", Icons.Filled.Home),
            TabItem(Routes.CLIPS, "Clips", Icons.Filled.PlayCircle),
            TabItem(Routes.MESSAGES, "Chats", Icons.AutoMirrored.Filled.Chat),
            TabItem(Routes.AI, "PulseBot", Icons.Filled.AutoAwesome)
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in tabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.FEED,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.FEED) { FeedScreen() }
            composable(Routes.CLIPS) { ClipsScreen() }
            composable(Routes.MESSAGES) {
                MessagesScreen(
                    onOpenChat = { chatId ->
                        navController.navigate(Routes.conversation(chatId))
                    }
                )
            }
            composable(Routes.AI) { AiChatScreen() }
            composable(
                route = Routes.CONVERSATION,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { entry ->
                val chatId = entry.arguments?.getString("chatId").orEmpty()
                ConversationScreen(
                    chatId = chatId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
