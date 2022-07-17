package app.kobuggi.hyuabot.ui.shuttle

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttlePeriodQuery
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ShuttleViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    val shuttleTimetable : MutableLiveData<List<ShuttleTimetableQuery.Timetable>> = MutableLiveData(arrayListOf())
    val showShuttleStopLocationDialog = MutableLiveData<Event<Boolean>>()
    val openShuttleTimetableEvent = MutableLiveData<Event<Boolean>>()
    val showShuttleStopLocation = MutableLiveData<LatLng>()
    val shuttleStopName = MutableLiveData<Int>()
    val shuttleTimetableType = MutableLiveData<String>()
    val locationChecked = MutableLiveData(false)
    val sortedStopList = MutableLiveData(listOf(
        ShuttleStopInfo(R.string.dormitory, LatLng(37.29339607529377, 126.83630604103446)),
        ShuttleStopInfo(R.string.shuttlecock_o, LatLng(37.29875417910844, 126.83784054072336)),
        ShuttleStopInfo(R.string.station, LatLng(37.308494476826155, 126.85310236423418)),
        ShuttleStopInfo(R.string.terminal, LatLng(37.31945164682341, 126.8455453372041)),
        ShuttleStopInfo(R.string.shuttlecock_i, LatLng(37.29869328231496, 126.8377767466817))
    ))
    val isLoading = MutableLiveData(false)
    private val disposable = CompositeDisposable()
    private var shuttlePeriod : String? = null
    private suspend fun fetchShuttlePeriod() {
        shuttlePeriod = client.query(ShuttlePeriodQuery()).execute().data?.shuttle?.period!!
    }

    fun fetchShuttleTimetable() {
        isLoading.value = true
        val startTime = LocalTime.now().minusMinutes(30).toString()
        viewModelScope.launch {
            if (shuttlePeriod == null){
                val fetchShuttlePeriodJob = CoroutineScope(Dispatchers.IO).async {
                    fetchShuttlePeriod()
                }
                fetchShuttlePeriodJob.await()
            }
            Log.d("ShuttleViewModel", "period: $shuttlePeriod")
            val result = client.query(
                ShuttleTimetableQuery(shuttlePeriod!!, "weekdays","", "Dormitory", startTime, "23:59")
            ).execute()
            if (result.data != null) {
                shuttleTimetable.value = result.data!!.shuttle.timetable
            }
        }
    }

    fun startFetchData() {
        disposable.add(
            Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fetchShuttleTimetable()
                }
        )
    }

    fun stopFetchData() {
        disposable.clear()
    }

    fun clickShuttleStopLocation(location: LatLng, titleID: Int) {
        showShuttleStopLocation.value = location
        shuttleStopName.value = titleID
        showShuttleStopLocationDialog.value = Event(true)
    }

    fun openShuttleTimetableFragment(stopName: Int, type: String) {
        shuttleStopName.value = stopName
        shuttleTimetableType.value = type
        openShuttleTimetableEvent.value = Event(true)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

