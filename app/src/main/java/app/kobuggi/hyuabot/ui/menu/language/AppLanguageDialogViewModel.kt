package app.kobuggi.hyuabot.ui.menu.language

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AppLanguageDialogViewModel @Inject constructor() : ViewModel() {
    private val _localeCode = MutableLiveData<String>()
    val localeCode get() = _localeCode

    fun setKorean() {
        _localeCode.value = "ko"
    }

    fun setEnglish() {
        _localeCode.value = "en"
    }

    fun setChinese() {
        _localeCode.value = "zh"
    }
}