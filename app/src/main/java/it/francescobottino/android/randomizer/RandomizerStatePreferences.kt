package it.francescobottino.android.randomizer

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@SuppressLint("ApplySharedPref")
class RandomizerStatePreferences(ctx: Context) {
    companion object {
        private const val STATE_SHARED_PREFERENCES = "it.francescobottino.android.randomizer.main.saved_state"
        private const val SELECTED_NUMBER = "SELECTED_NUMBER"
        private const val SELECTED_NUMBER_DEFAULT = 6
        private const val RESULT_NUMBER = "RESULT_NUMBER"
        private const val RESULT_NUMBER_DEFAULT = 0
        private const val EXCLUDED_NUMBERS = "EXCLUDED_NUMBERS"
        private const val EXCLUDED_NUMBERS_DEFAULT = "[]"
        private val EXCLUDED_NUMBERS_TYPE = object : TypeToken<Set<Int>>() {}.type
        private const val EXCLUSION_IS_ENABLED = "EXCLUSION_IS_ENABLED"
        private const val EXCLUSION_IS_ENABLED_DEFAULT = false
    }

    private val mainActivityStatePreferences: SharedPreferences = ctx.getSharedPreferences(
        STATE_SHARED_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )

    var selectedNumber: Int
        set(value) {
            mainActivityStatePreferences.edit().putInt(SELECTED_NUMBER, value).commit()
        }
        get() {
            return mainActivityStatePreferences.getInt(SELECTED_NUMBER, SELECTED_NUMBER_DEFAULT)
        }

    var excludedNumbers: Set<Int>
        set(value) {
            val asString = Gson().toJson(value, EXCLUDED_NUMBERS_TYPE)
            mainActivityStatePreferences.edit().putString(EXCLUDED_NUMBERS, asString).commit()
        }
        get() {
            val excludedNumbersString = mainActivityStatePreferences.getString(EXCLUDED_NUMBERS, EXCLUDED_NUMBERS_DEFAULT)
            return Gson().fromJson(excludedNumbersString, EXCLUDED_NUMBERS_TYPE)
        }

    var resultNumber: Int
        set(value) {
            mainActivityStatePreferences.edit().putInt(RESULT_NUMBER, value).commit()
        }
        get() {
            return mainActivityStatePreferences.getInt(RESULT_NUMBER, RESULT_NUMBER_DEFAULT)
        }

    var exclusionIsEnabled: Boolean
        set(value) {
            mainActivityStatePreferences.edit().putBoolean(EXCLUSION_IS_ENABLED, value).commit()
        }
        get() {
            return mainActivityStatePreferences.getBoolean(EXCLUSION_IS_ENABLED, EXCLUSION_IS_ENABLED_DEFAULT)
        }
}