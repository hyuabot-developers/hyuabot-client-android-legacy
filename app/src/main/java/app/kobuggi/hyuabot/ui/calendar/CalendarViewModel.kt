package app.kobuggi.hyuabot.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val repository: AppDatabaseRepository) : ViewModel() {
    fun onCalendarMonthChanged(currentMonth: YearMonth){
        viewModelScope.launch {
            repository.getCalendarItemsFilterByMonth(currentMonth).collect{
                Log.d("CalendarViewModel", "onCalendarMonthChanged: ${it.size}")
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