package com.example.liftassistant.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.liftassistant.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object History : BottomNavItem(
        route = "history_graph",
        titleRes = R.string.tab_history,
        icon = Icons.Default.History
    )
    object Exercises : BottomNavItem(
        route = "exercise_graph",
        titleRes = R.string.tab_exercises,
        icon = Icons.Default.FitnessCenter
    )
    object Routines : BottomNavItem(
        route = "routine_graph",
        titleRes = R.string.tab_routines,
        icon = Icons.Default.ListAlt
    )
}

val bottomNavItems = listOf(
    BottomNavItem.History,
    BottomNavItem.Exercises,
    BottomNavItem.Routines
)