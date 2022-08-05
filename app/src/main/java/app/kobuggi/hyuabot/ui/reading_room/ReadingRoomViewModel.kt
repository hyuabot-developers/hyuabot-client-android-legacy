package app.kobuggi.hyuabot.ui.reading_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.ReadingRoomQuery
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingRoomViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    val rooms = MutableLiveData<List<ReadingRoomQuery.ReadingRoom>>()

    fun fetchReadingRoomData() {
        viewModelScope.launch {
            val result = client.query(
                ReadingRoomQuery(0, true)
            ).execute()
            if (result.data != null) {
                rooms.value = result.data!!.readingRoom
            }
        }
    }
}