package com.jtonomous.droidscroller.ui

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.jtonomous.droidscroller.model.Card as CardModel
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.cardPresentationFor
import com.jtonomous.droidscroller.model.fixedPagerSlots
import com.jtonomous.droidscroller.model.InterestSequence
import com.jtonomous.droidscroller.model.NavigationMode
import com.jtonomous.droidscroller.model.NavigationSettings
import com.jtonomous.droidscroller.viewmodel.CardScrollerViewModel

@Composable
fun CardScrollerScreen(
    interestSequence: InterestSequence,
    navigationSettings: NavigationSettings,
    persistenceError: String? = null,
    modifier: Modifier = Modifier,
    viewModel: CardScrollerViewModel? = null
) {
    val dragOffset = remember { mutableStateOf(Offset.Zero) }
    val animatedOffset = animateOffsetAsState(
        targetValue = dragOffset.value,
        animationSpec = tween(durationMillis = 300),
        label = "CardScrollOffset"
    )
    val showAddCardDialog = remember { mutableStateOf(false) }
    val newCardTitle = remember { mutableStateOf("") }

    if (navigationSettings.isSettingsOpen) {
        NavigationSettingsScreen(
            navigationSettings = navigationSettings,
            onToggleNavigationButtons = { viewModel?.toggleNavigationButtons() },
            onClose = { viewModel?.closeSettings() },
            persistenceError = persistenceError,
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset.value = Offset(
                            dragOffset.value.x + dragAmount.x,
                            dragOffset.value.y + dragAmount.y
                        )
                    },
                    onDragEnd = {
                        val threshold = 100f
                        if (kotlin.math.abs(dragOffset.value.x) > kotlin.math.abs(dragOffset.value.y)) {
                            if (dragOffset.value.x > threshold) {
                                viewModel?.moveToPreviousInterest()
                            } else if (dragOffset.value.x < -threshold) {
                                viewModel?.moveToNextInterest()
                            }
                        } else if (dragOffset.value.y > threshold) {
                            viewModel?.moveBackward()
                        } else if (dragOffset.value.y < -threshold) {
                            viewModel?.moveForward()
                        }
                        dragOffset.value = Offset.Zero
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            persistenceError?.let { error ->
                Text(
                    text = "Persistence error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        newCardTitle.value = ""
                        showAddCardDialog.value = true
                    }
                ) {
                    Text("+")
                }
                Button(
                    onClick = { viewModel?.openSettings() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text("⚙")
                }
            }

            if (navigationSettings.showNavigationButtons) {
                Button(
                    onClick = { viewModel?.moveBackward() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Next card")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = interestSequence.activeInterest?.title ?: "No interests",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = interestSequence.activeInterest?.cards?.let { sequence ->
                    val position = if (sequence.cards.isEmpty()) {
                        "No cards"
                    } else {
                        "Card ${sequence.focusedIndex + 1} of ${sequence.cards.size}"
                    }
                    "$position · ${navigationModeLabel(sequence.navigationMode)}"
                } ?: "No cards · ${navigationModeLabel(NavigationMode.FINITE)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            interestSequence.activeInterest?.cards?.fixedPagerSlots()?.forEach { slot ->
                    val relativePosition = slot.relativePosition
                    val presentation = cardPresentationFor(relativePosition)
                    val height = when (kotlin.math.abs(relativePosition)) {
                        0 -> 150.dp
                        1 -> 80.dp
                        else -> 50.dp
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (relativePosition == 0) 0.95f else 0.85f)
                            .height(height)
                            .scale(presentation.scale)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        slot.card?.let { card ->
                            CardItem(
                                card = card,
                                isFocused = relativePosition == 0,
                                alpha = presentation.alpha,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .offset(y = (animatedOffset.value.y / 50).dp)
            )

            if (navigationSettings.showNavigationButtons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { viewModel?.moveForward() }) {
                        Text("Previous card")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (navigationSettings.showNavigationButtons) {
                    Row {
                        Button(onClick = { viewModel?.moveToPreviousInterest() }) {
                            Text("← Interest")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = { viewModel?.moveToNextInterest() }) {
                            Text("Interest →")
                        }
                    }
                }
            }
        }

        if (showAddCardDialog.value) {
            AlertDialog(
                onDismissRequest = { showAddCardDialog.value = false },
                title = { Text("Add card") },
                text = {
                    TextField(
                        value = newCardTitle.value,
                        onValueChange = { newCardTitle.value = it },
                        label = { Text("Card name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel?.addCard(newCardTitle.value)
                            showAddCardDialog.value = false
                        },
                        enabled = newCardTitle.value.isNotBlank()
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCardDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun NavigationSettingsScreen(
    navigationSettings: NavigationSettings,
    onToggleNavigationButtons: () -> Unit,
    onClose: () -> Unit,
    persistenceError: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        persistenceError?.let { error ->
            Text(
                text = "Persistence error: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Navigation buttons")
            Button(onClick = onToggleNavigationButtons) {
                Text(if (navigationSettings.showNavigationButtons) "On" else "Off")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClose) {
            Text("Back")
        }
    }
}

@Composable
private fun CardItem(
    card: CardModel,
    isFocused: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFocused) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = card.title,
                    style = if (isFocused) {
                        MaterialTheme.typography.headlineSmall
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
                Text(
                    text = "ID: ${card.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.7f)
                )
            }
        }

    }
}

private fun navigationModeLabel(mode: NavigationMode): String {
    return mode.name.lowercase().replaceFirstChar { it.uppercase() }
}
