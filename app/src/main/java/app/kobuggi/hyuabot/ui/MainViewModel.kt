package app.kobuggi.hyuabot.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import app.kobuggi.hyuabot.GlobalApplication
import app.kobuggi.hyuabot.data.database.AppDatabase
import app.kobuggi.hyuabot.data.database.AppDatabaseDao
import app.kobuggi.hyuabot.data.database.AppDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val appDatabaseRepository: AppDatabaseRepository) : ViewModel() {
    fun upgradeDatabase (assetsPath: String){
        Log.d("MainViewModel", "upgradeDatabase")
        // 데이터베이스 업데이트
        val database = Room.databaseBuilder(GlobalApplication.applicationContext(), AppDatabase::class.java, assetsPath)
            .fallbackToDestructiveMigration()
            .build()
        val dao = database.databaseDao()
        viewModelScope.launch {
            appDatabaseRepository.getAll().collect{
                if (it.isEmpty()){
                    initializeAppDatabase(dao)
                }
            }
        }
        viewModelScope.launch {
            appDatabaseRepository.getAllEvents().collect{
                if (it.isEmpty()){
                    initializeCalendarDatabase(dao)
                }
            }
        }
    }

    fun initializeDatabase (assetsPath: String) {
        // 데이터베이스 업데이트
        val database = Room.databaseBuilder(GlobalApplication.applicationContext(), AppDatabase::class.java, assetsPath)
            .fallbackToDestructiveMigration()
            .build()
        val dao = database.databaseDao()
        viewModelScope.launch {
            appDatabaseRepository.deleteAll()
            dao.getAll().collect{
                appDatabaseRepository.insertAll(it)
            }
            appDatabaseRepository.deleteAllEvents()
            dao.getAllEvents().collect{
                appDatabaseRepository.insertAllEvents(it)
            }
        }
    }

    private fun initializeAppDatabase(dao: AppDatabaseDao){
        viewModelScope.launch {
            appDatabaseRepository.deleteAll()
            dao.getAll().collect {
                appDatabaseRepository.insertAll(it)
            }
        }
    }

    private fun initializeCalendarDatabase(dao: AppDatabaseDao){
        viewModelScope.launch {
            appDatabaseRepository.deleteAllEvents()
            dao.getAllEvents().collect {
                appDatabaseRepository.insertAllEvents(it)
            }
        }
    }
}