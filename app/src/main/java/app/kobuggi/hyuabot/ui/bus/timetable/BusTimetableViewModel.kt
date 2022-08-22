package app.kobuggi.hyuabot.ui.bus.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BusTimetableViewModel @Inject constructor(private val client: ApolloClient): ViewModel() {
    val busTimetable : MutableLiveData<List<BusQuery.Timetable>> = MutableLiveData(arrayListOf())
    val showErrorToast : MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    fun fetchBusTimetable(routeName: String) {
        viewModelScope.launch {
            try {
                val result = client.query(
                    BusQuery(listOf("한양대게스트하우스", "한양대정문"), listOf(routeName), "")
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