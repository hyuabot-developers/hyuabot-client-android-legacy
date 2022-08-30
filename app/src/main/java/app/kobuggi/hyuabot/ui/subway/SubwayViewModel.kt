package app.kobuggi.hyuabot.ui.subway

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.SubwayQuery
import app.kobuggi.hyuabot.ui.bus.BusRouteItem
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SubwayViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val _subwayData = arrayListOf<SubwayRouteItem>()
    val subwayData = MutableLiveData<List<SubwayRouteItem>>()
    val isLoading = MutableLiveData(false)
    val moveToTimetableFragmentEvent = MutableLiveData<Event<Boolean>>()
    val timetableRouteName = MutableLiveData<String>()
    val timetableRouteColor = MutableLiveData<String>()
    val timetableHeading = MutableLiveData<String>()
    private var nativeAd: NativeAd? = null
    val showErrorToast = MutableLiveData<Event<Boolean>>()

    private fun fetchData() {
        isLoading.value = true
        val now = LocalTime.now()
        viewModelScope.launch {
            try {
                val result = client.query(
                    SubwayQuery(
                        routePair = listOf(
                            app.kobuggi.hyuabot.type.SubwayQuery("한대앞", "4호선"),
                            app.kobuggi.hyuabot.type.SubwayQuery("한대앞", "수인분당선"),
                        ),
                        weekday = Optional.Absent,
                        heading = Optional.Absent,
                        startTime = Optional.presentIfNotNull("${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"),
                        endTime = Optional.Absent,
                        count = Optional.presentIfNotNull(10)
                    )
                ).execute()
                if (result.data != null) {
                    _subwayData.clear()
                    _subwayData.addAll(result.data!!.subway.map { SubwayRouteItem(it) })
                    if (nativeAd != null) {
                        _subwayData.add(1, SubwayRouteItem(null, nativeAd!!))
                    }
                    subwayData.value = _subwayData
                }
            } catch (e: ApolloNetworkException) {
                showErrorToast.value = Event(true)
                _subwayData.clear()
                _subwayData.addAll(listOf(
                    SubwayRouteItem(SubwayQuery.Subway("한대앞", "4호선", listOf(), listOf()), null),
                    SubwayRouteItem(SubwayQuery.Subway("한대앞", "수인분당선", listOf(), listOf()), null)
                ))
                subwayData.value = _subwayData
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

    fun stopFetchData() {
        disposable.clear()
    }

    fun moveToTimetableFragment(routeName: String, heading: String, routeColor: String) {
        timetableRouteColor.value = routeColor
        timetableRouteName.value = routeName
        timetableHeading.value = heading
        moveToTimetableFragmentEvent.value = Event(true)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun insertAD(nativeAd: NativeAd){
        this.nativeAd = nativeAd
        _subwayData.add(1, SubwayRouteItem(null, nativeAd))
        subwayData.value = _subwayData
    }
}

