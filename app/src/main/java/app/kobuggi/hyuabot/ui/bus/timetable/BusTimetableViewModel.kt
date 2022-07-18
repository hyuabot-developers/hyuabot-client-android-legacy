package app.kobuggi.hyuabot.ui.bus.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.ShuttlePeriodQuery
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BusTimetableViewModel @Inject constructor(private val client: ApolloClient): ViewModel() {
    val busTimetable : MutableLiveData<List<BusQuery.Timetable>> = MutableLiveData(arrayListOf())
    fun fetchBusTimetable(routeName: String) {
        viewModelScope.launch {
            val result = client.query(
                BusQuery(listOf("한양대게스트하우스", "한양대정문"), listOf(routeName), "")
            ).execute()
            if (result.data != null && result.data!!.bus.isNotEmpty()) {
                busTimetable.value = result.data!!.bus[0].timetable
            }
        }
    }
}