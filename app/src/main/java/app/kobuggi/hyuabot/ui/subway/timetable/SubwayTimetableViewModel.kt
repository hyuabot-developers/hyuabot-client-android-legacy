package app.kobuggi.hyuabot.ui.subway.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.SubwayQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubwayTimetableViewModel @Inject constructor(private val client: ApolloClient): ViewModel() {
    val subwayTimetable : MutableLiveData<List<SubwayQuery.Timetable>> = MutableLiveData(arrayListOf())
    fun fetchSubwayTimetable(routeName: String, heading: String) {
        viewModelScope.launch {
            val result = client.query(
                SubwayQuery(
                    stations = listOf("한대앞"),
                    routes = listOf(routeName),
                    weekday = "",
                    heading = heading,
                    startTime = "00:00",
                    endTime = "23:59"
                )
            ).execute()
            if (result.data != null && result.data!!.subway.isNotEmpty()) {
                subwayTimetable.value = result.data!!.subway[0].timetable
            }
        }
    }
}