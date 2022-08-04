package app.kobuggi.hyuabot.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity(tableName = "calendar", primaryKeys = ["name"])
@Parcelize
data class CalendarDatabaseItem(
    val name: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String,
    @ColumnInfo(name = "target_grade") val targetGrade: Int,
    @ColumnInfo(name = "notification_boolean") val notificationBoolean: Boolean
): Parcelable
