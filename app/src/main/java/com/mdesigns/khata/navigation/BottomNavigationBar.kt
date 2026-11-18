package com.mdesigns.khata.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mdesigns.khata.core.theme.PrimaryTeal

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Khata : BottomNavItem(Screen.Khata.route, "Khata", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    object Reports : BottomNavItem(Screen.Reports.route, "Reports", Icons.Filled.Assessment, Icons.Outlined.Assessment)
    object Money : BottomNavItem(Screen.Money.route, "Money", Icons.Filled.Paid, Icons.Outlined.Paid)
    object More : BottomNavItem(Screen.More.route, "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Khata,
        BottomNavItem.Reports,
        BottomNavItem.Money,
        BottomNavItem.More
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = items.any { it.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = selected,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryTeal,
                        selectedTextColor = PrimaryTeal,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}
