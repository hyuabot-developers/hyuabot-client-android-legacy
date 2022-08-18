package app.kobuggi.hyuabot.ui.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import app.kobuggi.hyuabot.ui.cafeteria.CafeteriaItem
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactTabViewModel @Inject constructor(private val repository: AppDatabaseRepository) : ViewModel() {
    val contactList = MutableLiveData<List<ContactItem>>()
    private val _contactList = arrayListOf<ContactItem>()
    private val category = "on campus"
    private var nativeAd: NativeAd? = null

    private fun getContactFilterByCategory(category: String) = repository.getPhoneItemsFilterByCategory(category)
    private fun getContactExceptByCategory(category: String) = repository.getPhoneItemsExceptByCategory(category)
    fun setPosition(position: Int) {
        viewModelScope.launch {
            _contactList.clear()
            if (position == 0) {
                getContactFilterByCategory(category).collect{
                    _contactList.addAll(it.map { item -> ContactItem(item.name, item.phone.toString(), null) })
                }
            } else {
                getContactExceptByCategory(category).collect{
                    _contactList.addAll(it.map { item -> ContactItem(item.name, item.phone.toString(), null) })
                }
            }
            if (nativeAd != null) {
                _contactList.add(0, ContactItem("", "", nativeAd))
            }
        }
    }

    fun queryContact(position: Int, query: String) {
        _contactList.clear()
        viewModelScope.launch {
            if (position == 0) {
                if (query.isNotEmpty()){
                    repository.getPhoneItemsFilterByName(category, "%$query%").collect{
                        _contactList.addAll(it.map { item -> ContactItem(item.name, item.phone.toString(), null) })
                    }
                } else {
                    getContactFilterByCategory(category).collect{
                        _contactList.addAll(it.map { item -> ContactItem(item.name, item.phone.toString(), null) })
                    }
                }
            } else {
                if (query.isNotEmpty()){
                    repository.getPhoneItemsExceptByName(category, "%$query%").collect{
                        _contactList.addAll(it.map { item -> ContactItem(item.name, item.phone.toString(), null) })
                    }
                } else {
                    getContactExceptByCategory(category).collect{
                        _contactList.addAll(it.map { item -> ContactItem(item.name, item.phone.toString(), null) })
                    }
                }
            }
        }
        if (nativeAd != null) {
            _contactList.add(0, ContactItem("", "", nativeAd))
        }
        contactList.value = _contactList
    }

    fun insertAD(nativeAd: NativeAd){
        this.nativeAd = nativeAd
        _contactList.add(0, ContactItem("", "", nativeAd))
        contactList.value = _contactList
    }
}