package app.kobuggi.hyuabot.data.database

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

class AppDatabaseRepository(private val dao: AppDatabaseDao) {
    suspend fun deleteAll() = dao.deleteAll()
    suspend fun insertAll(items: List<AppDatabaseItem>) = dao.insertDatabaseItems(*items.toTypedArray())

    fun getAll(): Flow<List<AppDatabaseItem>> = dao.getAll()
    fun getMapItemsFilterByCategory(category: String) : Flow<List<AppDatabaseItem>> = dao.getMapItemsFilterByCategory(category)
    fun getMapItemsFilterByName(name: String) : Flow<List<AppDatabaseItem>> = dao.getMapItemsFilterByName(name)
    fun getPhoneItemsFilterByName(category: String, name: String) : Flow<List<AppDatabaseItem>> = dao.getPhoneItemsFilterByName(name, category)
    fun getPhoneItemsExceptByName(category: String, name: String) : Flow<List<AppDatabaseItem>> = dao.getPhoneItemsExceptByName(name, category)
    fun getPhoneItemsFilterByCategory(category: String) : Flow<List<AppDatabaseItem>> = dao.getPhoneItemsFilterByCategory(category)
    fun getPhoneItemsExceptByCategory(category: String) : Flow<List<AppDatabaseItem>> = dao.getPhoneItemsExceptByCategory(category)

    suspend fun deleteAllEvents() = dao.deleteEvents()
    suspend fun insertAllEvents(items: List<CalendarDatabaseItem>) = dao.insertCalendarItems(*items.toTypedArray())

    fun getCalendarItemsFilterByMonth(currentMonth: YearMonth) : Flow<List<CalendarDatabaseItem>> = dao.getCalendarItemsFilterByMonth("${currentMonth.year}-${currentMonth.monthValue.toString().padStart(2, '0')}-01T00:00+09:00", "${currentMonth.year}-${(currentMonth.monthValue + 1).toString().padStart(2, '0')}-01T00:00+09:00")
    fun getCalendarNotificationItemsFilterByDay(targetGrade: Int, targetDate: LocalDate) : Flow<List<CalendarDatabaseItem>> = dao.getCalendarItemsFilterByMonthAndGrade("${targetDate.year}-${targetDate.monthValue.toString().padStart(2, '0')}-${targetDate.dayOfMonth}T00:00+09:00", "${targetDate.year}-${targetDate.monthValue.toString().padStart(2, '0')}-${targetDate.dayOfMonth}T23:59+09:00", targetGrade)
    fun getAllEvents(): Flow<List<CalendarDatabaseItem>> = dao.getAllEvents()
}