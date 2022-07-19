package app.kobuggi.hyuabot.ui.subway

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.SubwayQuery
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
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
    val subwayData = MutableLiveData<List<SubwayQuery.Subway>>()
    val isLoading = MutableLiveData(false)
    val moveToTimetableFragmentEvent = MutableLiveData<Event<Boolean>>()
    val timetableRouteName = MutableLiveData<String>()
    val timetableRouteColor = MutableLiveData<String>()
    val timetableHeading = MutableLiveData<String>()

    private fun fetchData() {
        isLoading.value = true
        val now = LocalTime.now()
        viewModelScope.launch {
            val result = client.query(
                SubwayQuery(
                    stations = listOf("한대앞"),
                    routes = listOf("4호선", "수인분당선"),
                    weekday = "now",
                    heading = "",
                    startTime = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}",
                    endTime = "23:59"
                )
            ).execute()
            if (result.data != null) {
                subwayData.value = result.data!!.subway
            } else {
                Log.d("SubwayViewModel", result.errors.toString())
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
}

