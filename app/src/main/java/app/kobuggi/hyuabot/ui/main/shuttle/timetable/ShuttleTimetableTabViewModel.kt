package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.kobuggi.hyuabot.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ShuttleTimetableTabViewModel @Inject constructor(): ViewModel() {
    val showShuttleRouteDialog = MutableLiveData<Event<Boolean>>()
    private val _arrivalTime = MutableLiveData<LocalTime>()
    val arrivalTime get() = _arrivalTime
    private val _startStop = MutableLiveData<String>()
    val startStop get() = _startStop

    fun openShuttleRouteDialog(arrivalTime: LocalTime, startStop: String) {
        _arrivalTime.value = arrivalTime
        _startStop.value = startStop
        showShuttleRouteDialog.value = Event(true)
    }
}