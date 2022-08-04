package app.kobuggi.hyuabot.ui.calendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import app.kobuggi.hyuabot.data.database.CalendarDatabaseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val repository: AppDatabaseRepository) : ViewModel() {
    val eventsOfMonth = MutableLiveData<List<CalendarDatabaseItem>>()

    fun onCalendarMonthChanged(currentMonth: YearMonth){
        Log.d("CalendarViewModel", "onCalendarMonthChanged: ${currentMonth.year} ${currentMonth.monthValue}")
        viewModelScope.launch {
            repository.getCalendarItemsFilterByMonth(currentMonth).collect{
                eventsOfMonth.value = it
            }
        }
    }

    fun getAllEvents() {
        viewModelScope.launch {
            repository.getAllEvents().collect {
                Log.d("CalendarViewModel", "getAllEvents: ${it.size}")
            }
        }
    }
}