package app.kobuggi.hyuabot.ui.shuttle

import android.content.Context
import android.content.SharedPreferences
import app.kobuggi.hyuabot.R

object ShuttleWidgetUtility {
    private const val PREFS_NAME = "app.kobuggi.hyuabot.ui.shuttle.ShuttleWidgetProvider"
    private const val PREF_PREFIX_KEY = "shuttle_widget_"
    internal fun saveWidgetStopID(context: Context, appWidgetId: Int, stopID: Int) {
        context.getSharedPreferences(PREFS_NAME, 0).edit().putInt(PREF_PREFIX_KEY + appWidgetId, stopID).apply()
    }

    internal fun loadWidgetStopID(context: Context, appWidgetId: Int): Int {
        return context.getSharedPreferences(PREFS_NAME, 0).getInt(PREF_PREFIX_KEY + appWidgetId, R.string.shuttlecock_o)
    }

    internal fun deleteWidgetStopID(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, 0).edit().remove(PREF_PREFIX_KEY + appWidgetId).apply()
    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    private fun Context.getSharedPreferences(name: String, mode: Int) : SharedPreferences {
        return getSharedPreferences(name, mode)
    }
}