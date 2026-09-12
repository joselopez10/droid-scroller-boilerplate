package com.jtonomous.droidscroller.persistence

import android.content.SharedPreferences

class SharedPreferencesCardScrollerStateStore(
    private val preferences: SharedPreferences
) : CardScrollerStateStore {
    override fun load(): PersistedCardScrollerState? {
        val serialized = preferences.getString(STATE_KEY, null) ?: return null
        return CardScrollerStateCodec.decode(serialized)
    }

    override fun save(state: PersistedCardScrollerState) {
        val saved = preferences.edit()
            .putString(STATE_KEY, CardScrollerStateCodec.encode(state))
            .commit()
        if (!saved) {
            throw PersistenceException("Android did not commit persisted state")
        }
    }

    override fun clear() {
        if (!preferences.edit().remove(STATE_KEY).commit()) {
            throw PersistenceException("Android did not clear persisted state")
        }
    }

    private companion object {
        const val STATE_KEY = "card_scroller_state"
    }
}
