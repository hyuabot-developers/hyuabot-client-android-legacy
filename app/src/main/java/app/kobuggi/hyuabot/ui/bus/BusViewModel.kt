package app.kobuggi.hyuabot.ui.bus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.BusQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BusViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val disposable = CompositeDisposable()
    val busData = MutableLiveData<List<BusQuery.Bus>>()
    val isLoading = MutableLiveData(false)

    private fun fetchData() {
        viewModelScope.launch {
            isLoading.value = true
            val result = client.query(
                BusQuery(
                    routes = listOf("10-1", "3102", "707-1"),
                    stopList = listOf("한양대게스트하우스", "한양대정문"),
                    weekday = "weekdays"
                )
            ).execute()
            if (result.data != null) {
                busData.value = result.data!!.bus.filter {
                    (it.routeName == "707-1" && it.stopName == "한양대정문") || (it.routeName != "707-1" && it.stopName == "한양대게스트하우스")
                }
            } else {
                Log.e("BusViewModel", result.errors.toString())
            }
        }
    }

    fun startFetchData() {
        disposable.add(
            Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fetchData()
                }
        )
    }

    fun stopFetchData() {
        disposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

