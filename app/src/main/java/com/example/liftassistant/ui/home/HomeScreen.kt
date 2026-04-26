@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.util.TimeUtils.formatDuration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.R
import com.example.liftassistant.data.Workout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToWorkoutSummary: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (homeUiState.inProgressWorkout == null) {
                FloatingActionButton(onClick = navigateToStartWorkout) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.start_workout)
                    )
                }
            }
        }
    ) { innerPadding ->
        HomeBody(
            workoutList = homeUiState.workoutList,
            onWorkoutClick = navigateToWorkoutSummary,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun HomeBody(
    workoutList: List<Workout>,
    onWorkoutClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (workoutList.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_workout_list),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            WorkoutList(
                workoutList = workoutList,
                onWorkoutClick = { onWorkoutClick(it.id) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun WorkoutList(
    workoutList: List<Workout>,
    onWorkoutClick: (Workout) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn (
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = workoutList, key = { it.id }) { workout ->
            WorkoutListItem(
                workout = workout,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onWorkoutClick(workout) }
            )
        }
    }
}

@Composable
private fun WorkoutListItem(
    workout: Workout,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                workout.duration?.let { duration ->
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Text(
                text = formatDate(workout.date),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun formatDuration(durationMillis: Long): String {
    val totalMinutes = durationMillis / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("EEE, MMM d YYYY", Locale.getDefault())
    return formatter.format(date)
}