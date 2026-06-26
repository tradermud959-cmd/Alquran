package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Default.Home)
    object Home : Screen("home", "Beranda", Icons.Default.Home)
    object History : Screen("history", "Riwayat", Icons.Default.List)
    object Stats : Screen("stats", "Statistik", Icons.Default.PieChart)
    object Settings : Screen("settings", "Pengaturan", Icons.Default.Settings)
    object Add : Screen("add", "Tambah", Icons.Default.Add)
}

@Composable
fun MainNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController, viewModel) }
        composable(Screen.History.route) { HistoryScreen(viewModel) }
        composable(Screen.Stats.route) { StatsScreen(viewModel) }
        composable(Screen.Settings.route) { SettingsScreen(viewModel) }
        composable(Screen.Add.route) { AddScreen(navController, viewModel) }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.History,
        Screen.Stats,
        Screen.Settings
    )

    NavigationBar(
        containerColor = com.example.ui.theme.SurfaceDark,
        contentColor = com.example.ui.theme.TextSecondary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute == Screen.Splash.route || currentRoute == Screen.Add.route) return@NavigationBar

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title, fontSize = 10.sp, fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = com.example.ui.theme.AccentGreen,
                    selectedTextColor = com.example.ui.theme.AccentGreen,
                    indicatorColor = com.example.ui.theme.BackgroundDark,
                    unselectedIconColor = com.example.ui.theme.TextSecondary,
                    unselectedTextColor = com.example.ui.theme.TextSecondary
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
