package app.kobuggi.hyuabot.ui.reading_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.ReadingRoomQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingRoomViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    val rooms = MutableLiveData<List<ReadingRoomQuery.ReadingRoom>>()
    val isCampus = MutableLiveData<Boolean>()

    fun fetchReadingRoomData() {
        viewModelScope.launch {
            val result = client.query(
                ReadingRoomQuery(if(isCampus.value == true) 0 else 1, true)
            ).execute()
            if (result.data != null) {
                rooms.value = result.data!!.readingRoom.filter { it.activeSeat > 0 }.sortedBy { it.roomName }
            }
        }
    }
}