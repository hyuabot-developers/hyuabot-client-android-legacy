package app.kobuggi.hyuabot.ui

import androidx.lifecycle.ViewModel
import androidx.room.Room
import app.kobuggi.hyuabot.GlobalApplication
import app.kobuggi.hyuabot.data.database.AppDatabase
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val appDatabaseRepository: AppDatabaseRepository) : ViewModel() {
    fun initializeDatabase (assetsPath: String) {
        // 데이터베이스 업데이트
        val database = Room.databaseBuilder(GlobalApplication.applicationContext(), AppDatabase::class.java, assetsPath)
            .fallbackToDestructiveMigration()
            .build()
        val dao = database.databaseDao()
        runBlocking {
            appDatabaseRepository.deleteAll()
            dao.getAll().collect{
                appDatabaseRepository.insertAll(it)
            }
        }
    }
}