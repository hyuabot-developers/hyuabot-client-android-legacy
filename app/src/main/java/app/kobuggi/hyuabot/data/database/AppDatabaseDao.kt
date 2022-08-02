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

    @Query("SELECT * FROM app WHERE phone is not null and category = :category and name LIKE :name")
    fun getPhoneItemsFilterByName(name: String, category: String): Flow<List<AppDatabaseItem>>
}