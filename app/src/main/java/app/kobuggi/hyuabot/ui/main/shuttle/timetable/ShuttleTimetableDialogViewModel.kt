package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ShuttleTimetableDialogViewModel @Inject constructor() : ViewModel() {
    val arrivalTime = MutableLiveData<LocalTime>()
    val startStop = MutableLiveData<String>()
    val stopID = MutableLiveData<Int>()
    val shuttleType = MutableLiveData<Int>()

    fun setData(arrivalTime: LocalTime, startStop: String, stopID: Int, shuttleType: Int) {
        this.arrivalTime.value = arrivalTime
        this.startStop.value = startStop
        this.stopID.value = stopID
        this.shuttleType.value = shuttleType
    }
}