package app.kobuggi.hyuabot.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _searchInputFocus = MutableLiveData(false)
    val searchInputFocus get() = _searchInputFocus

    fun setSearchInputFocus(focus: Boolean) {
        _searchInputFocus.value = focus
    }
}