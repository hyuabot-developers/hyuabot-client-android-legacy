package app.kobuggi.hyuabot.ui.shuttle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.*
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.google.android.gms.ads.nativead.NativeAd
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
    val stopList = mutableListOf(
        ShuttleStopInfo(R.string.dormitory, LatLng(37.29339607529377, 126.83630604103446), null),
        ShuttleStopInfo(R.string.shuttlecock_o, LatLng(37.29875417910844, 126.83784054072336), null),
        ShuttleStopInfo(R.string.station, LatLng(37.308494476826155, 126.85310236423418), null),
        ShuttleStopInfo(R.string.terminal, LatLng(37.31945164682341, 126.8455453372041), null),
        ShuttleStopInfo(R.string.shuttlecock_i, LatLng(37.29869328231496, 126.8377767466817), null)
    )
    private val _stopLiveData = MutableLiveData<List<ShuttleStopInfo>>()
    val stopLiveData: MutableLiveData<List<ShuttleStopInfo>> = _stopLiveData
    private val disposable = CompositeDisposable()
    private var shuttlePeriod : String? = null
    private var shuttleWeekday : String? = null
    val showErrorToast = MutableLiveData<Event<Int>>()

    init {
        _stopLiveData.value = stopList
    }

    private suspend fun fetchShuttleDate() {
        try {
            val query = client.query(ShuttleDateQuery()).execute().data
            shuttlePeriod = query?.shuttle?.period!!
            shuttleWeekday = query.shuttle.weekday
        } catch (e: ApolloNetworkException) {
//            showErrorToast.postValue(Event(R.string.error_fetch_shuttle_date))
        }
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
            if (shuttlePeriod != null && shuttleWeekday != null) {
                try {
                    val result = client.query(
                        ShuttleTimetableQuery(
                            period =  shuttlePeriod!!,
                            weekday = Optional.presentIfNotNull(shuttleWeekday!!),
                            startTime = Optional.presentIfNotNull(startTime),
                            count = Optional.presentIfNotNull(5)
                        )
                    ).execute()
                    if (result.data != null) {
                        shuttleTimetable.value = result.data!!.shuttle.timetable
                    } else {
                        showErrorToast.value = Event(R.string.error_fetch_shuttle_date)
                    }
                } catch (e: ApolloNetworkException) {
                    showErrorToast.value = Event(R.string.error_fetch_shuttle_date)
                }
            }
            isLoading.value = false
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
            if (shuttlePeriod != null && shuttleWeekday != null) {
                try {
                    val result = client.query(
                        ShuttleTimetableQuery(
                            period =  shuttlePeriod!!,
                            weekday = Optional.presentIfNotNull(shuttleWeekday!!),
                        )).execute()
                    if (result.data != null) {
                        shuttleEntireTimetable.value = result.data!!.shuttle.timetable
                    }
                } catch (e: ApolloNetworkException) {
                    shuttleEntireTimetable.value = arrayListOf()
                }
            }
        }
    }

    fun startFetchData() {
        disposable.add(
            Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    try {
                        fetchShuttleTimetable()
                    } catch (e: ApolloNetworkException){
                        e.printStackTrace()
                    }
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

    fun insertAD(nativeAd: NativeAd){
        stopList.add(2, ShuttleStopInfo(0, LatLng(0.0, 0.0), nativeAd))
        _stopLiveData.value = stopList
    }
}

