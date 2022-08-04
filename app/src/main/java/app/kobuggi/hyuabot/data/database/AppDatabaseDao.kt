package app.kobuggi.hyuabot.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDatabaseDao {
    @Query("SELECT * FROM app")
    fun getAll(): Flow<List<AppDatabaseItem>>

    @Query("DELETE FROM app")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatabaseItem(databaseItem: AppDatabaseItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatabaseItems(vararg databaseItem: AppDatabaseItem)

    @Query("SELECT * FROM app WHERE category = :category and latitude is not null and longitude is not null")
    fun getMapItemsFilterByCategory(category: String): Flow<List<AppDatabaseItem>>

    @Query("SELECT * FROM app WHERE name LIKE :name and latitude is not null and longitude is not null")
    fun getMapItemsFilterByName(name: String): Flow<List<AppDatabaseItem>>

    @Query("SELECT * FROM app WHERE phone is not null and phone != '' and category = :category and name LIKE :name")
    fun getPhoneItemsFilterByName(name: String, category: String): Flow<List<AppDatabaseItem>>

    @Query("SELECT * FROM app WHERE phone is not null and phone != '' and category != :category and name LIKE :name")
    fun getPhoneItemsExceptByName(name: String, category: String): Flow<List<AppDatabaseItem>>

    @Query("SELECT * FROM app WHERE phone is not null and phone != '' and category = :category")
    fun getPhoneItemsFilterByCategory(category: String): Flow<List<AppDatabaseItem>>

    @Query("SELECT * FROM app WHERE phone is not null and phone != '' and category != :category")
    fun getPhoneItemsExceptByCategory(category: String): Flow<List<AppDatabaseItem>>

    @Query("DELETE FROM calendar")
    suspend fun deleteEvents()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarItems(vararg calendarDatabaseItem: CalendarDatabaseItem)

    @Query("SELECT * FROM calendar WHERE start_date < :endDate and end_date > :startDate")
    fun getCalendarItemsFilterByMonth(startDate: String, endDate: String): Flow<List<CalendarDatabaseItem>>

    @Query("SELECT * FROM calendar WHERE start_date >= :startDate and start_date <= :endDate and target_grade = :grade and notification_boolean = 0")
    fun getCalendarItemsFilterByMonthAndGrade(startDate: String, endDate: String, grade: Int): Flow<List<CalendarDatabaseItem>>

    @Query("SELECT * FROM calendar")
    fun getAllEvents() : Flow<List<CalendarDatabaseItem>>
}