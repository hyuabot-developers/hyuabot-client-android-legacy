package app.kobuggi.hyuabot.data.database

import kotlinx.coroutines.flow.Flow

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
}