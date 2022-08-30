package app.kobuggi.hyuabot.ui.subway.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.SubwayQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubwayTimetableViewModel @Inject constructor(private val client: ApolloClient): ViewModel() {
    val subwayTimetable : MutableLiveData<List<SubwayQuery.Timetable>> = MutableLiveData(arrayListOf())
    fun fetchSubwayTimetable(routeName: String, heading: String) {
        viewModelScope.launch {
            try {
                val result = client.query(
                    SubwayQuery(
                        routePair = listOf(
                            app.kobuggi.hyuabot.type.SubwayQuery("한대앞", routeName),
                        ),
                        weekday = Optional.Absent,
                        heading = Optional.presentIfNotNull(heading),
                        startTime = Optional.Absent,
                        endTime = Optional.Absent
                    )
                ).execute()
                if (result.data != null && result.data!!.subway.isNotEmpty()) {
                    subwayTimetable.value = result.data!!.subway[0].timetable
                }
            } catch (e: ApolloNetworkException) {
                subwayTimetable.value = arrayListOf()
            }
        }
    }
}