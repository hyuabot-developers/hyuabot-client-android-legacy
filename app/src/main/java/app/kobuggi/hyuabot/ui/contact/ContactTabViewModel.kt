package app.kobuggi.hyuabot.ui.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactTabViewModel @Inject constructor(private val repository: AppDatabaseRepository) : ViewModel() {
    val contactList = MutableLiveData<List<AppDatabaseItem>>()
    private val category = "on campus"
    private fun getContactFilterByCategory(category: String) = repository.getPhoneItemsFilterByCategory(category)
    private fun getContactExceptByCategory(category: String) = repository.getPhoneItemsExceptByCategory(category)
    fun setPosition(position: Int) {
        viewModelScope.launch {
            if (position == 0) {
                getContactFilterByCategory(category).collect{
                    contactList.value = it
                }
            } else {
                getContactExceptByCategory(category).collect{
                    contactList.value = it
                }
            }
        }
    }

    fun queryContact(position: Int, query: String) {
        viewModelScope.launch {
            if (position == 0) {
                if (query.isNotEmpty()){
                    repository.getPhoneItemsFilterByName(category, "%$query%").collect{
                        contactList.value = it
                    }
                } else {
                    getContactFilterByCategory(category).collect{
                        contactList.value = it
                    }
                }
            } else {
                if (query.isNotEmpty()){
                    repository.getPhoneItemsExceptByName(category, "%$query%").collect{
                        contactList.value = it
                    }
                } else {
                    getContactExceptByCategory(category).collect{
                        contactList.value = it
                    }
                }
            }
        }
    }
}