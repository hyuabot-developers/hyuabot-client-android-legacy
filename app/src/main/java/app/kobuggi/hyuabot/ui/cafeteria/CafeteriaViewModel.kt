package app.kobuggi.hyuabot.ui.cafeteria

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.CafeteriaMenuQuery
import app.kobuggi.hyuabot.ui.bus.BusRouteItem
import app.kobuggi.hyuabot.utils.Event
import com.apollographql.apollo3.ApolloClient
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeteriaViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val _cafeteriaMenu = arrayListOf<CafeteriaItem>()
    val cafeteriaMenu = MutableLiveData<List<CafeteriaItem>>()
    val showCafeteriaLocationDialog = MutableLiveData<Event<Boolean>>()
    val cafeteriaLocation = MutableLiveData<LatLng>()
    val cafeteriaName = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)
    private var nativeAd: NativeAd? = null

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
                _cafeteriaMenu.clear()
                _cafeteriaMenu.addAll(result.data!!.cafeteria.map { CafeteriaItem(it) })
                if (nativeAd != null) {
                    _cafeteriaMenu.add(1, CafeteriaItem(null, nativeAd!!))
                }
                cafeteriaMenu.value = _cafeteriaMenu
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

    fun insertAD(nativeAd: NativeAd){
        this.nativeAd = nativeAd
        _cafeteriaMenu.add(1, CafeteriaItem(null, nativeAd))
        cafeteriaMenu.value = _cafeteriaMenu
    }
}

