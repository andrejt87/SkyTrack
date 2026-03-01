package com.skytrack.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.skytrack.app.ui.theme.*

enum class NavTab { Live, Map, History, Settings }

@Composable
fun SkyTrackBottomBar(
    selected: NavTab,
    onTabSelected: (NavTab) -> Unit
) {
    NavigationBar(containerColor = DarkSurface) {
        NavigationBarItem(
            selected = selected == NavTab.Live,
            onClick = { onTabSelected(NavTab.Live) },
            icon = { Icon(Icons.Default.Dashboard, null) },
            label = { Text("Live") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Amber, selectedTextColor = Amber,
                unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary,
                indicatorColor = DarkSurface2
            )
        )
        NavigationBarItem(
            selected = selected == NavTab.Map,
            onClick = { onTabSelected(NavTab.Map) },
            icon = { Icon(Icons.Default.Map, null) },
            label = { Text("Map") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Amber, selectedTextColor = Amber,
                unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary,
                indicatorColor = DarkSurface2
            )
        )
        NavigationBarItem(
            selected = selected == NavTab.History,
            onClick = { onTabSelected(NavTab.History) },
            icon = { Icon(Icons.Default.History, null) },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Amber, selectedTextColor = Amber,
                unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary,
                indicatorColor = DarkSurface2
            )
        )
        NavigationBarItem(
            selected = selected == NavTab.Settings,
            onClick = { onTabSelected(NavTab.Settings) },
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Amber, selectedTextColor = Amber,
                unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary,
                indicatorColor = DarkSurface2
            )
        )
    }
}
