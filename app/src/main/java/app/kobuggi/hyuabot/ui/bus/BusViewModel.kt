package app.kobuggi.hyuabot.ui.bus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.ShuttleDateQuery
import app.kobuggi.hyuabot.ui.shuttle.ShuttleStopInfo
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
class BusViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val disposable = CompositeDisposable()
    val busData = mutableListOf<BusRouteItem>()
    val busDataLiveData = MutableLiveData<List<BusRouteItem>>()
    val isLoading = MutableLiveData(false)
    val moveToTimetableFragmentEvent = MutableLiveData<Event<Boolean>>()
    val timetableRouteName = MutableLiveData<String>()
    val timetableRouteColor = MutableLiveData<String>()
    val showErrorToast = MutableLiveData(Event(false))
    private var busWeekDay : String? = null
    private var nativeAd: NativeAd? = null
    private suspend fun fetchBusDate() {
        try {
            val query = client.query(ShuttleDateQuery()).execute().data
            busWeekDay = query?.shuttle?.weekday
        } catch (e: ApolloNetworkException) {
//            showErrorToast.postValue(Event(R.string.error_fetch_shuttle_date))
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            isLoading.value = true
            val startTime = LocalTime.now().minusMinutes(30).toString()
            try {
                val result = client.query(
                    BusQuery(
                        routePair = listOf(
                            app.kobuggi.hyuabot.type.BusQuery("한양대게스트하우스", "10-1"),
                            app.kobuggi.hyuabot.type.BusQuery("한양대정문", "707-1"),
                            app.kobuggi.hyuabot.type.BusQuery("한양대게스트하우스", "3102"),
                        ),
                        weekday = Optional.presentIfNotNull(busWeekDay!!),
                        startTime = Optional.presentIfNotNull(startTime),
                        endTime = Optional.Absent,
                        count = Optional.presentIfNotNull(5)
                    )
                ).execute()
                if (result.data != null) {
                    busData.clear()
                    busData.addAll(result.data!!.bus.filter {
                        (it.routeName == "707-1" && it.stopName == "한양대정문") || (it.routeName != "707-1" && it.stopName == "한양대게스트하우스")
                    }.map { BusRouteItem(it, null) })
                    busData.forEach{
                        Log.d("BusViewModel", it.toString())
                    }
                    if (nativeAd != null) {
                        insertAD(nativeAd!!)
                    }
                    busDataLiveData.value = busData
                } else {
                    Log.e("BusViewModel", result.errors.toString())
                }
            } catch (e: ApolloNetworkException){
                showErrorToast.postValue(Event(true))
                busData.clear()
                busData.addAll(listOf(
                    BusRouteItem(arrivalList= BusQuery.Bus(stopName = "한양대게스트하우스", routeName = "10-1", stopId = 216000379, routeId = 216000068, startStop = "푸르지오6차후문", terminalStop = "상록수역", timeFromStartStop = 11, realtime = listOf(), timetable = listOf())),
                    BusRouteItem(arrivalList= BusQuery.Bus(stopName = "한양대게스트하우스", routeName = "3102", stopId = 216000379, routeId = 216000061, startStop = "새솔고", terminalStop = "강남역", timeFromStartStop = 28, realtime = listOf(), timetable = listOf())),
                    BusRouteItem(arrivalList= BusQuery.Bus(stopName = "한양대정문", routeName = "707-1", stopId = 216000719, routeId = 216000070, startStop = "신안산대", terminalStop = "수원역", timeFromStartStop = 23, realtime = listOf(), timetable = listOf())),
                ))
                busDataLiveData.value = busData
            } finally {
                isLoading.value = false
            }
        }
    }

    fun startFetchData() {
        viewModelScope.launch {
            if (busWeekDay == null) {
                val busPeriodJob = CoroutineScope(Dispatchers.IO).async {
                    fetchBusDate()
                }
                busPeriodJob.await()
                disposable.add(
                    Observable.interval(0, 1, TimeUnit.MINUTES)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            fetchData()
                        }
                )
            }
        }
    }

    fun moveToTimetableFragment(routeName: String, routeColor: String) {
        timetableRouteColor.value = routeColor
        timetableRouteName.value = routeName
        moveToTimetableFragmentEvent.value = Event(true)
    }

    fun stopFetchData() {
        disposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun insertAD(nativeAd: NativeAd){
        this.nativeAd = nativeAd
        busData.add(1, BusRouteItem(null, nativeAd))
        busDataLiveData.value = busData
    }
}

