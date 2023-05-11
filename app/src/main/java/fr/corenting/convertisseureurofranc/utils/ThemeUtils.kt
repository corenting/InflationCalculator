package fr.corenting.convertisseureurofranc.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import fr.corenting.convertisseureurofranc.R

object ThemeUtils {

    private const val THEME_PREFERENCE_KEY = "theme"

    enum class ThemePreference(val preferenceValue: Int, val menuId: Int) {
        THEME_SYSTEM(1, R.id.action_theme_system),
        THEME_LIGHT(2, R.id.action_theme_light),
        THEME_DARK(3, R.id.action_theme_dark);

        companion object {
            fun fromPreferenceValue(value: Int) =
                values().first { it.preferenceValue == value }

            fun fromMenuId(value: Int) = values().first { it.menuId == value }
        }
    }

    fun saveThemePreference(context: Context, itemId: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = prefs.edit()
        prefsEditor.putInt(THEME_PREFERENCE_KEY, ThemePreference.fromMenuId(itemId).preferenceValue)
        prefsEditor.apply()
    }

    fun getMenuIdForCurrentTheme(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return ThemePreference.fromPreferenceValue(
            prefs.getInt(
                THEME_PREFERENCE_KEY,
                ThemePreference.THEME_SYSTEM.preferenceValue
            )
        ).menuId
    }

    fun getThemeToUse(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        return when (ThemePreference.fromPreferenceValue(
            prefs.getInt(
                THEME_PREFERENCE_KEY,
                ThemePreference.THEME_SYSTEM.preferenceValue
            )
        )) {
            ThemePreference.THEME_SYSTEM -> {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            ThemePreference.THEME_DARK -> {
                AppCompatDelegate.MODE_NIGHT_YES
            }

            ThemePreference.THEME_LIGHT -> {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}