package app.kobuggi.hyuabot.data.database

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity(tableName = "app", primaryKeys = ["name"])
@Parcelize
data class AppDatabaseItem(
    val name: String,
    val category: String,
    val phone: String?,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?,
): Parcelable
