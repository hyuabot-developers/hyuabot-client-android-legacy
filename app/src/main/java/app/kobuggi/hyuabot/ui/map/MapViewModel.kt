package app.kobuggi.hyuabot.ui.map

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: AppDatabaseRepository) : ViewModel() {
    private val _searchInputFocus = MutableLiveData(false)
    val searchInputFocus get() = _searchInputFocus
    private val _items = MutableLiveData<List<AppDatabaseItem>>()
    val items get() = _items
    private val _markers = MutableLiveData<List<Marker>>()
    val markers get() = _markers
    private val _markerOptions = MutableLiveData<List<MarkerOptions>>()
    val markerOptions get() = _markerOptions
    val selectedCategory = MutableLiveData<String>()
    val showCategoryButton = MutableLiveData(false)

    fun setSearchInputFocus(focus: Boolean) {
        _searchInputFocus.value = focus
    }

    fun getSearchResult(query: String) {
        viewModelScope.launch {
            repository.getMapItemsFilterByName("%${query}%").collect {
                _items.value = it
            }
        }
    }

    fun setMarkerOptions(options: List<MarkerOptions>) {
        _markerOptions.value = options
    }

    fun onCategoryButtonClick(category: String, categoryKey: String, bitmap: Bitmap) {
        selectedCategory.value = category
        viewModelScope.launch {
            repository.getMapItemsFilterByCategory(categoryKey).collect {
                it.map { item -> run {

                    MarkerOptions().apply {
                        position(LatLng(item.latitude!!, item.longitude!!))
                        title(item.name)
                        icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                }}}.let {
                    _markerOptions.value = it
                }
            }
        }
    }

    fun onSelectedCategoryButtonClick(view: View) {
        showCategoryButton.value = !showCategoryButton.value!!
    }
}