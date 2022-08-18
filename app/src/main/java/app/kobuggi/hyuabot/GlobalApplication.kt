package app.kobuggi.hyuabot

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltAndroidApp
class GlobalApplication : Application() {
    private val themePrefKey = stringPreferencesKey("theme")
    @Inject
    lateinit var dataStore: DataStore<Preferences>
    init {
        instance = this
    }

    companion object {
        lateinit var instance: GlobalApplication
        fun applicationContext() = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        val theme : Flow<String> = dataStore.data.map { it[themePrefKey] ?: "system"}
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            theme.collect{
                when (it) {
                    "system" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    "off" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    "on" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }
        MobileAds.initialize(this)
        val testDeviceIds: List<String> = listOf("98A0F2211F0AD23799E4A6F63A14CF46")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
    }
}