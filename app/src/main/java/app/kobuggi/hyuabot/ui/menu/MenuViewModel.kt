package app.kobuggi.hyuabot.ui.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.kobuggi.hyuabot.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {
    private val _moveEvent = MutableLiveData<Event<Int>>()
    val moveEvent get() = _moveEvent

    fun moveToSomewhere(stringID: Int) {
        moveEvent.value = Event(stringID)
    }
}

