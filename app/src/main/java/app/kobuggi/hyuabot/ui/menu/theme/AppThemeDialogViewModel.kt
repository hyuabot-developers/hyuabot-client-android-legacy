package app.kobuggi.hyuabot.ui.menu.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AppThemeDialogViewModel @Inject constructor() : ViewModel() {
    private val _darkMode = MutableLiveData<String>()
    val darkmode get() = _darkMode

    fun setDarkMode(theme: Boolean) {
        if (theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            _darkMode.value = "on"
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            _darkMode.value = "off"
        }
    }

    fun setDarkModeSystem(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        darkmode.value = "system"
    }

}