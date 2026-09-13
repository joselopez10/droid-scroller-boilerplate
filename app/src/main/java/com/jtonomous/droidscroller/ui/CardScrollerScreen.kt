package com.jtonomous.droidscroller.ui

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.jtonomous.droidscroller.model.Card as CardModel
import com.jtonomous.droidscroller.model.CardLayoutSize
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
    val showCardActionsDialog = remember { mutableStateOf(false) }
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }

    if (navigationSettings.isSettingsOpen) {
        NavigationSettingsScreen(
            navigationSettings = navigationSettings,
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

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val layoutSize = CardLayoutSize(
                    width = maxWidth.value,
                    height = maxHeight.value
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    interestSequence.activeInterest?.cards?.fixedPagerSlots()?.forEach { slot ->
                        val relativePosition = slot.relativePosition
                        val presentation = cardPresentationFor(relativePosition, layoutSize)
                        val slotModifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                        Box(
                            modifier = slotModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            slot.card?.let { card ->
                                CardItem(
                                    card = card,
                                    isFocused = relativePosition == 0,
                                    alpha = presentation.alpha,
                                    onLongPress = if (relativePosition == 0) {
                                        { showCardActionsDialog.value = true }
                                    } else {
                                        null
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(if (relativePosition == 0) 0.95f else 0.85f)
                                        .fillMaxHeight()
                                        .scale(presentation.scale)
                                        .alpha(presentation.alpha)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height((animatedOffset.value.y / 50).dp))
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

        if (showCardActionsDialog.value) {
            AlertDialog(
                onDismissRequest = { showCardActionsDialog.value = false },
                title = { Text("Card actions") },
                text = { Text("Choose an action for ${interestSequence.activeInterest?.cards?.focusedCard?.title ?: "this card"}.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCardActionsDialog.value = false
                            showDeleteConfirmationDialog.value = true
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCardActionsDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDeleteConfirmationDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirmationDialog.value = false
                    showCardActionsDialog.value = true
                },
                title = { Text("Delete card?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel?.deleteFocusedCard()
                            showDeleteConfirmationDialog.value = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmationDialog.value = false
                            showCardActionsDialog.value = true
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Composable
private fun NavigationSettingsScreen(
    navigationSettings: NavigationSettings,
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
        Text("Version")
        Text("0.1", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.then(
            if (onLongPress == null) {
                Modifier
            } else {
                Modifier.pointerInput(card.id) {
                    detectTapGestures(onLongPress = { onLongPress() })
                }
            }
        ),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: ${card.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

    }
}

private fun navigationModeLabel(mode: NavigationMode): String {
    return mode.name.lowercase().replaceFirstChar { it.uppercase() }
}
