package app.kobuggi.hyuabot.ui.shuttle.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.ShuttleDateQuery
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShuttleTimetableViewModel @Inject constructor(private val client: ApolloClient): ViewModel() {
    val shuttleTimetable : MutableLiveData<List<ShuttleTimetableQuery.Timetable>> = MutableLiveData(arrayListOf())
    private var shuttlePeriod : String? = null
    private var shuttleWeekday : String? = null

    private suspend fun fetchShuttlePeriod() {
        val query = client.query(ShuttleDateQuery()).execute().data
        shuttlePeriod = query?.shuttle?.period
        shuttleWeekday = query?.shuttle?.weekday
    }

    fun fetchShuttleTimetable() {
        viewModelScope.launch {
            if (shuttlePeriod == null || shuttleWeekday == null) {
                val fetchShuttlePeriodJob = CoroutineScope(Dispatchers.IO).async {
                    fetchShuttlePeriod()
                }
                fetchShuttlePeriodJob.await()
            }
            val result = client.query(
                ShuttleTimetableQuery(shuttlePeriod!!, "", "00:00", "23:59")
            ).execute()
            if (result.data != null) {
                shuttleTimetable.value = result.data!!.shuttle.timetable
            }
        }
    }
}