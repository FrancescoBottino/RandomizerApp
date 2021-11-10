package it.francescobottino.android.randomizer

import android.content.Context
import androidx.lifecycle.ViewModel

class RandomizerViewModel: ViewModel() {
    private var state: RandomizerStatePreferences? = null

    fun getState(context: Context): RandomizerStatePreferences {
        return state ?: RandomizerStatePreferences(context).also {
            state = it
        }
    }
}