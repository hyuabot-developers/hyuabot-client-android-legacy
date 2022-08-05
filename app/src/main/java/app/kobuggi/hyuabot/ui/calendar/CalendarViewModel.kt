package app.kobuggi.hyuabot.ui.calendar

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
    val currentMonthData = MutableLiveData<YearMonth>()
    private val gradeData = MutableLiveData(0)

    fun onCalendarMonthChanged(currentMonth: YearMonth){
        currentMonthData.value = currentMonth
        viewModelScope.launch {
            if (gradeData.value!! > 0){
                repository.getCalendarItemsFilterByGrade(gradeData.value!!, currentMonthData.value!!).collect{
                    eventsOfMonth.value = it
                }
            } else {
                repository.getCalendarItemsFilterByMonth(currentMonth).collect{
                    eventsOfMonth.value = it
                }
            }
        }
    }

    fun filterByGrade(grade: Int){
        gradeData.value = grade
        viewModelScope.launch {
            if (grade > 0){
                repository.getCalendarItemsFilterByGrade(grade, currentMonthData.value!!).collect{
                    eventsOfMonth.value = it
                }
            } else {
                repository.getCalendarItemsFilterByMonth(currentMonthData.value!!).collect{
                    eventsOfMonth.value = it
                }
            }
        }
    }
}