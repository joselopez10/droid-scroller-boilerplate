package com.jtonomous.droidscroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jtonomous.droidscroller.ui.CardScrollerScreen
import com.jtonomous.droidscroller.viewmodel.CardScrollerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DroidScrollerApp()
        }
    }
}

@Composable
private fun DroidScrollerApp(viewModel: CardScrollerViewModel = viewModel()) {
    MaterialTheme {
        Surface {
            CardScrollerScreen(
                interestSequence = viewModel.interestSequence.value,
                modifier = Modifier,
                viewModel = viewModel
            )
        }
    }
}
