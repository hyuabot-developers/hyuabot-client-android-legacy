package app.kobuggi.hyuabot.ui.reading_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.ReadingRoomQuery
import app.kobuggi.hyuabot.ui.cafeteria.CafeteriaItem
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingRoomViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val _readingRoomList = arrayListOf<ReadingRoomItem>()
    val rooms = MutableLiveData<List<ReadingRoomItem>>()
    val isCampus = MutableLiveData<Boolean>()
    private var nativeAd: NativeAd? = null

    fun fetchReadingRoomData() {
        viewModelScope.launch {
            try {
                val result = client.query(
                    ReadingRoomQuery(if(isCampus.value == true) 0 else 1, true)
                ).execute()
                if (result.data != null) {
                    _readingRoomList.clear()
                    _readingRoomList.addAll(
                        result.data!!.readingRoom.filter { it.activeSeat > 0 }.sortedBy { it.roomName }.map { ReadingRoomItem(it) }
                    )
                    if (nativeAd != null) {
                        _readingRoomList.add(1, ReadingRoomItem(null, nativeAd!!))
                    }
                    rooms.value = _readingRoomList
                }
            } catch (e: ApolloNetworkException) {
                _readingRoomList.clear()
                rooms.value = _readingRoomList
            }
        }
    }

    fun insertAD(nativeAd: NativeAd){
        this.nativeAd = nativeAd
        _readingRoomList.add(1, ReadingRoomItem(null, nativeAd))
        rooms.value = _readingRoomList
    }
}