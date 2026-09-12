package com.jtonomous.droidscroller.ui

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import com.jtonomous.droidscroller.viewmodel.CardScrollerViewModel

@Composable
fun CardScrollerScreen(
    sequence: CardSequence,
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
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        dragOffset.value = Offset(0f, dragOffset.value.y + dragAmount)
                    },
                    onDragEnd = {
                        val threshold = 100f
                        if (dragOffset.value.y > threshold) {
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
            // Previous card (partial)
            sequence.neighborBefore?.let { card ->
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
            sequence.focusedCard?.let { card ->
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
            sequence.neighborAfter?.let { card ->
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
