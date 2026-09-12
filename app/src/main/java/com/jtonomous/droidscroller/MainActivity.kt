package com.jtonomous.droidscroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.jtonomous.droidscroller.persistence.SharedPreferencesCardScrollerStateStore
import com.jtonomous.droidscroller.ui.CardScrollerScreen
import com.jtonomous.droidscroller.viewmodel.CardScrollerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var cardScrollerViewModel: CardScrollerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stateStore = SharedPreferencesCardScrollerStateStore(
            getSharedPreferences(STATE_PREFERENCES, MODE_PRIVATE)
        )
        cardScrollerViewModel = ViewModelProvider(
            this,
            CardScrollerViewModel.Factory(stateStore)
        )[CardScrollerViewModel::class.java]
        setContent {
            DroidScrollerApp(cardScrollerViewModel)
        }
    }

    override fun onStop() {
        cardScrollerViewModel.persistNow()
        super.onStop()
    }

    fun resetPersistedStateForTesting() {
        cardScrollerViewModel.resetToDefaultsForTesting()
    }

    private companion object {
        const val STATE_PREFERENCES = "card_scroller_preferences"
    }
}

@Composable
private fun DroidScrollerApp(viewModel: CardScrollerViewModel) {
    MaterialTheme {
        Surface {
            CardScrollerScreen(
                interestSequence = viewModel.interestSequence.value,
                navigationSettings = viewModel.navigationSettings.value,
                persistenceError = viewModel.persistenceError.value,
                modifier = Modifier,
                viewModel = viewModel
            )
        }
    }
}
