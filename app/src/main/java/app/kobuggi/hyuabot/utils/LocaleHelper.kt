package app.kobuggi.hyuabot.utils

import android.content.Context
import android.util.Log
import java.util.*

class LocaleHelper {
    companion object {
        var locale: Locale? = null

        fun wrap(context: Context): Context {
            if(locale == null){
                return context
            }

            val resource = context.resources
            val configuration = resource.configuration
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        }

        fun setLocale(localeCode: String){
            Log.d("LocaleHelper", "setLocale: $localeCode")
            locale = Locale(localeCode)
        }
    }
}