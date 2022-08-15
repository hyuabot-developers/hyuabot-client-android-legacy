package app.kobuggi.hyuabot.ui.shuttle

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.*
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
    val shuttleEntireTimetable : MutableLiveData<List<ShuttleTimetableQuery.Timetable>> = MutableLiveData(arrayListOf())
    val showShuttleStopLocationDialog = MutableLiveData<Event<Boolean>>()
    val openShuttleTimetableEvent = MutableLiveData<Event<Boolean>>()
    val showShuttleStopLocation = MutableLiveData<LatLng>()
    val shuttleStopName = MutableLiveData<Int>()
    val shuttleTimetableType = MutableLiveData<String>()
    val locationChecked = MutableLiveData(false)
    val closestStop = MutableLiveData<ShuttleStopInfo>()
    val isLoading = MutableLiveData(false)
    private val disposable = CompositeDisposable()
    private var shuttlePeriod : String? = null
    private var shuttleWeekday : String? = null
    private suspend fun fetchShuttleDate() {
        val query = client.query(ShuttleDateQuery()).execute().data
        shuttlePeriod = query?.shuttle?.period!!
        shuttleWeekday = query.shuttle.weekday
    }

    fun fetchShuttleTimetable() {
        isLoading.value = true
        val startTime = LocalTime.now().minusMinutes(30).toString()
        viewModelScope.launch {
            if (shuttlePeriod == null || shuttleWeekday == null) {
                val fetchShuttlePeriodJob = CoroutineScope(Dispatchers.IO).async {
                    fetchShuttleDate()
                }
                fetchShuttlePeriodJob.await()
            }
            Log.d("ShuttleViewModel", "period: $shuttlePeriod")
            val result = client.query(
                ShuttleTimetableQuery(shuttlePeriod!!, shuttleWeekday!!, startTime, "23:59")
            ).execute()
            if (result.data != null) {
                shuttleTimetable.value = result.data!!.shuttle.timetable
            }
        }
    }

    fun fetchShuttleEntireTimetable() {
        viewModelScope.launch {
            if (shuttlePeriod == null || shuttleWeekday == null) {
                val fetchShuttlePeriodJob = CoroutineScope(Dispatchers.IO).async {
                    fetchShuttleDate()
                }
                fetchShuttlePeriodJob.await()
            }
            Log.d("ShuttleViewModel", "period: $shuttlePeriod")
            val result = client.query(
                ShuttleTimetableQuery(shuttlePeriod!!, shuttleWeekday!!, "00:00", "23:59")
            ).execute()
            if (result.data != null) {
                shuttleEntireTimetable.value = result.data!!.shuttle.timetable
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

