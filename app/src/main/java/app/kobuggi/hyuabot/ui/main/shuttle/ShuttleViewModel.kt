package app.kobuggi.hyuabot.ui.main.shuttle

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.ShuttlePeriodQuery
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ShuttleViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    val shuttleTimetable : MutableLiveData<List<ShuttleTimetableQuery.Timetable>> = MutableLiveData(arrayListOf())
    private val disposable = CompositeDisposable()
    private var shuttlePeriod : String? = null
    private suspend fun fetchShuttlePeriod() {
        shuttlePeriod = client.query(ShuttlePeriodQuery()).execute().data?.shuttle?.period!!
    }

    private fun fetchShuttleTimetable() {
        val startTime = LocalTime.now().minusMinutes(30).toString()
        viewModelScope.launch {
            if (shuttlePeriod == null){
                val fetchShuttlePeriodJob = CoroutineScope(Dispatchers.IO).async {
                    fetchShuttlePeriod()
                }
                fetchShuttlePeriodJob.await()
            }
            Log.d("ShuttleViewModel", "period: $shuttlePeriod")
            val result = client.query(
                ShuttleTimetableQuery(shuttlePeriod!!, "weekdays","", "Dormitory", startTime, "23:59")
            ).execute()
            if (result.data != null) {
                shuttleTimetable.value = result.data!!.shuttle.timetable
            }
        }
    }

    fun startFetchData() {
        disposable.add(
            Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fetchShuttleTimetable()
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

