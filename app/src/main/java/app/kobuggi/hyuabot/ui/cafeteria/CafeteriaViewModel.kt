package app.kobuggi.hyuabot.ui.cafeteria

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.CafeteriaMenuQuery
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeteriaViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val _cafeteriaMenu = MutableLiveData<List<CafeteriaMenuQuery.Cafeterium>>()
    val cafeteriaMenu = _cafeteriaMenu
    val showCafeteriaLocationDialog = MutableLiveData<Event<Boolean>>()
    val cafeteriaLocation = MutableLiveData<LatLng>()
    val cafeteriaName = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)
    fun fetchData() {
        isLoading.value = true
        viewModelScope.launch {
            val result = client.query(
                CafeteriaMenuQuery(
                    campusId = 1,
                    cafeteriaIdList = listOf(),
                    timeType = "",
                )
            ).execute()
            if (result.data != null) {
                _cafeteriaMenu.value = result.data!!.cafeteria
            } else {
                Log.d("BusViewModel", result.errors.toString())
            }
        }
    }

    fun clickCafeteriaLocation(location: LatLng, title: String) {
        cafeteriaLocation.value = location
        cafeteriaName.value = title
        showCafeteriaLocationDialog.value = Event(true)
    }
}

