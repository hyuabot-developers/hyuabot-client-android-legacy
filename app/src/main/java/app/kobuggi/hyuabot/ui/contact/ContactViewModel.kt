package app.kobuggi.hyuabot.ui.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor() : ViewModel() {
    val queryString = MutableLiveData<String>()
}