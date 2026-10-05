package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AngleMode
import com.example.model.CalculatorMode

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)

    var defaultCalculatorMode: CalculatorMode
        get() {
            val name = prefs.getString(KEY_DEFAULT_MODE, CalculatorMode.BASIC.name)
            return try {
                CalculatorMode.valueOf(name ?: CalculatorMode.BASIC.name)
            } catch (_: Exception) {
                CalculatorMode.BASIC
            }
        }
        set(value) = prefs.edit().putString(KEY_DEFAULT_MODE, value.name).apply()

    var angleMode: AngleMode
        get() {
            val name = prefs.getString(KEY_ANGLE_MODE, AngleMode.DEG.name)
            return try {
                AngleMode.valueOf(name ?: AngleMode.DEG.name)
            } catch (_: Exception) {
                AngleMode.DEG
            }
        }
        set(value) = prefs.edit().putString(KEY_ANGLE_MODE, value.name).apply()

    var decimalSeparator: String
        get() = prefs.getString(KEY_DECIMAL_SEP, ".") ?: "."
        set(value) = prefs.edit().putString(KEY_DECIMAL_SEP, value).apply()

    var numberGrouping: String
        get() = prefs.getString(KEY_NUMBER_GROUPING, "Comma") ?: "Comma"
        set(value) = prefs.edit().putString(KEY_NUMBER_GROUPING, value).apply()

    var isLargeButtonsEnabled: Boolean
        get() = prefs.getBoolean(KEY_LARGE_BUTTONS, false)
        set(value) = prefs.edit().putBoolean(KEY_LARGE_BUTTONS, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, false)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "AUTO") ?: "AUTO"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var selectedPaletteName: String
        get() = prefs.getString(KEY_PALETTE_NAME, "Orchid") ?: "Orchid"
        set(value) = prefs.edit().putString(KEY_PALETTE_NAME, value).apply()

    companion object {
        private const val KEY_DEFAULT_MODE = "pref_default_mode"
        private const val KEY_ANGLE_MODE = "pref_angle_mode"
        private const val KEY_DECIMAL_SEP = "pref_decimal_sep"
        private const val KEY_NUMBER_GROUPING = "pref_number_grouping"
        private const val KEY_LARGE_BUTTONS = "pref_large_buttons"
        private const val KEY_VIBRATION = "pref_vibration"
        private const val KEY_SOUND = "pref_sound"
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_PALETTE_NAME = "pref_palette_name"
    }
}
