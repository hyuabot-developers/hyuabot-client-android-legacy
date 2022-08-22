package app.kobuggi.hyuabot.ui.bus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.ui.shuttle.ShuttleStopInfo
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch
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
    private var nativeAd: NativeAd? = null

    private fun fetchData() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = client.query(
                    BusQuery(
                        routes = listOf("10-1", "3102", "707-1"),
                        stopList = listOf("한양대게스트하우스", "한양대정문"),
                        weekday = "weekdays"
                    )
                ).execute()
                if (result.data != null) {
                    busData.clear()
                    busData.addAll(result.data!!.bus.filter {
                        (it.routeName == "707-1" && it.stopName == "한양대정문") || (it.routeName != "707-1" && it.stopName == "한양대게스트하우스")
                    }.map { BusRouteItem(it, null) })
                    if (nativeAd != null) {
                        insertAD(nativeAd!!)
                    }
                    busDataLiveData.value = busData
                } else {
                    Log.e("BusViewModel", result.errors.toString())
                }
            } catch (e: ApolloNetworkException){
                showErrorToast.postValue(Event(true))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun startFetchData() {
        disposable.add(
            Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fetchData()
                }
        )
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

