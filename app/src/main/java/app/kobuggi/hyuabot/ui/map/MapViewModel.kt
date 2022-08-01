package app.kobuggi.hyuabot.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
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

    fun onCategoryButtonClick(category: String) {
        viewModelScope.launch {
            repository.getMapItemsFilterByCategory(category).collect {
                _items.value = it
            }
        }
    }
}