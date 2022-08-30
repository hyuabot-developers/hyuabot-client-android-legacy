package app.kobuggi.hyuabot.ui.bus.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.ShuttleDateQuery
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BusTimetableViewModel @Inject constructor(private val client: ApolloClient): ViewModel() {
    val busTimetable : MutableLiveData<List<BusQuery.Timetable>> = MutableLiveData(arrayListOf())
    val showErrorToast : MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))

    fun fetchBusTimetable(routeName: String) {
        val queryData = hashMapOf(
            "10-1" to app.kobuggi.hyuabot.type.BusQuery("한양대게스트하우스", "10-1"),
            "707-1" to app.kobuggi.hyuabot.type.BusQuery("한양대정문", "707-1"),
            "3102" to app.kobuggi.hyuabot.type.BusQuery("한양대게스트하우스", "3102"),
        )
        viewModelScope.launch {
            try {
                val result = client.query(
                    BusQuery(
                        routePair = listOf(
                            queryData[routeName]!!
                        ),
                        weekday = Optional.Absent
                    )
                ).execute()
                if (result.data != null && result.data!!.bus.isNotEmpty()) {
                    busTimetable.value = result.data!!.bus[0].timetable
                } else {
                    busTimetable.value = listOf()
                }
            } catch (e: ApolloNetworkException){
                busTimetable.value = listOf()
                showErrorToast.value = Event(true)
            }
        }
    }
}