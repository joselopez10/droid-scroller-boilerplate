package com.jtonomous.droidscroller.ui

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.jtonomous.droidscroller.model.Card as CardModel
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.InterestSequence
import com.jtonomous.droidscroller.model.NavigationMode
import com.jtonomous.droidscroller.viewmodel.CardScrollerViewModel

@Composable
fun CardScrollerScreen(
    interestSequence: InterestSequence,
    modifier: Modifier = Modifier,
    viewModel: CardScrollerViewModel? = null
) {
    val dragOffset = remember { mutableStateOf(Offset.Zero) }
    val animatedOffset = animateOffsetAsState(
        targetValue = dragOffset.value,
        animationSpec = tween(durationMillis = 300),
        label = "CardScrollOffset"
    )

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
                .fillMaxSize()
                .offset(y = (animatedOffset.value.y / 50).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

            // Previous card (partial)
            interestSequence.activeInterest?.cards?.neighborBefore?.let { card ->
                CardItem(
                    card = card,
                    isFocused = false,
                    alpha = 0.5f,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(120.dp)
                        .padding(bottom = 8.dp)
                )
            }

            // Focused card
            interestSequence.activeInterest?.cards?.focusedCard?.let { card ->
                CardItem(
                    card = card,
                    isFocused = true,
                    alpha = 1f,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(200.dp)
                        .padding(vertical = 8.dp)
                )
            }

            // Next card (partial)
            interestSequence.activeInterest?.cards?.neighborAfter?.let { card ->
                CardItem(
                    card = card,
                    isFocused = false,
                    alpha = 0.5f,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(120.dp)
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Control buttons for testing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { viewModel?.moveBackward() }) {
                    Text("← Previous")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { viewModel?.moveForward() }) {
                    Text("Next →")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
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
