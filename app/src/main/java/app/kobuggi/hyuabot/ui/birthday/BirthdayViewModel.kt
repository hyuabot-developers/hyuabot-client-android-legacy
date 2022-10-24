package app.kobuggi.hyuabot.ui.birthday

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BirthdayViewModel : ViewModel() {
    val doNotOpenThisYear = MutableLiveData(false)
}