package app.kobuggi.hyuabot.ui.shuttle

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import app.kobuggi.hyuabot.BuildConfig
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleDateQuery
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloNetworkException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ShuttleWidgetProvider @Inject constructor(private val client: ApolloClient): AppWidgetProvider() {
    constructor() : this(ApolloClient.Builder().serverUrl("${BuildConfig.server_url}/api/v2").build())
    private var shuttlePeriod : String? = null
    private var shuttleWeekday : String? = null
    var shuttleTimetable : List<ShuttleTimetableQuery.Timetable> = arrayListOf()

    // 앱 데이터 갱신을 요구할 때
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            fetchShuttleTimetable(context, appWidgetManager, appWidgetIds)
        }
    }

    // 해당 종류로 마지막 위젯이 삭제될 때
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId -> ShuttleWidgetUtility.deleteWidgetStopID(context, appWidgetId) }
    }

    companion object {
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, timetable: List<ShuttleTimetableQuery.Timetable>?, now: LocalTime = LocalTime.now()) {
            fun constructRemoteView(resID: Int, timetable: List<ShuttleTimetableQuery.Timetable>?) = RemoteViews(context.packageName, R.layout.widget_shuttle).apply {
                val types = when(resID){
                    R.string.dormitory -> listOf(R.string.shuttle_type_station, R.string.shuttle_type_terminal)
                    R.string.shuttlecock_o -> listOf(R.string.shuttle_type_station, R.string.shuttle_type_terminal)
                    R.string.station -> listOf(R.string.shuttle_type_campus, R.string.shuttle_type_terminal)
                    R.string.terminal -> listOf(R.string.shuttle_type_campus)
                    R.string.shuttlecock_i -> listOf(R.string.shuttle_type_dormitory)
                    else -> listOf()
                }

                setTextViewText(R.id.shuttle_stop_name, context.getString(resID).replace(" ", "\n").replace("(", "\n("))
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                if (timetable != null){
                    val nextStation = timetable.filter { (it.shuttleType == "DH" || it.shuttleType == "C") && (it.startStop == "Dormitory" || resID != R.string.dormitory) && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(getTimeDelta(resID, it.shuttleType).toLong()) > now }
                        .map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(getTimeDelta(resID, it.shuttleType).toLong()) }
                    if (nextStation.isNotEmpty()){
                        setTextViewText(R.id.shuttle_station, context.getString(R.string.shuttle_type_station, Duration.between(now, nextStation.minOrNull()).toMinutes()))
                        setViewVisibility(R.id.shuttle_station, VISIBLE)
                    } else {
                        setTextViewText(R.id.shuttle_station, context.getString(R.string.shuttle_type_station_out_of_service))
                        setViewVisibility(R.id.shuttle_station, VISIBLE)
                    }
                    val nextTerminal = timetable.filter { (it.shuttleType == "DY" || it.shuttleType == "C") && (it.startStop == "Dormitory" || resID != R.string.dormitory) && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(getTimeDelta(resID, it.shuttleType).toLong()) > now }
                        .map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(getTimeDelta(resID, it.shuttleType).toLong()) }
                    if (nextTerminal.isNotEmpty()){
                        setTextViewText(R.id.shuttle_terminal, context.getString(R.string.shuttle_type_terminal, Duration.between(now, nextStation.minOrNull()).toMinutes()))
                        setViewVisibility(R.id.shuttle_terminal, VISIBLE)
                    } else {
                        setTextViewText(R.id.shuttle_terminal, context.getString(R.string.shuttle_type_terminal_out_of_service))
                        setViewVisibility(R.id.shuttle_terminal, VISIBLE)
                    }
                    val nextCampus = timetable.filter { (it.startStop == "Dormitory" || resID != R.string.dormitory) && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(getTimeDelta(resID, it.shuttleType).toLong()) > now }
                        .map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(getTimeDelta(resID, it.shuttleType).toLong()) }
                    if (nextCampus.isNotEmpty() && (types[0] == R.string.shuttle_type_campus || types[0] == R.string.shuttle_type_dormitory)){
                        setTextViewText(R.id.shuttle_station, context.getString(R.string.shuttle_type_campus, Duration.between(now, nextStation.minOrNull()).toMinutes()))
                        setViewVisibility(R.id.shuttle_station, VISIBLE)
                    } else if (nextCampus.isEmpty()) {
                        setTextViewText(R.id.shuttle_station, context.getString(R.string.shuttle_type_campus_out_of_service))
                        setViewVisibility(R.id.shuttle_station, VISIBLE)
                    }
                } else {
                    setTextViewText(R.id.shuttle_station, context.getString(R.string.shuttle_need_refresh))
                    setViewVisibility(R.id.shuttle_terminal, GONE)
                }
                if (types.size == 1) {
                    setTextViewText(R.id.shuttle_terminal, "")
                    setViewVisibility(R.id.shuttle_terminal, GONE)
                }
                val updateWidgetIntent = Intent(context, ShuttleWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                }
                val updateWidgetPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, updateWidgetIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                setOnClickPendingIntent(R.id.refresh_button, updateWidgetPendingIntent)
            }
            val stopID = ShuttleWidgetUtility.loadWidgetStopID(context, appWidgetId)
            val remoteViews = constructRemoteView(stopID, timetable)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        private fun getTimeDelta(stopID: Int, shuttleType: String): Int{
            val timeDelta = hashMapOf(
                R.string.dormitory to arrayListOf(-5, -5, -5),
                R.string.shuttlecock_o to arrayListOf(0, 0, 0),
                R.string.station to arrayListOf(10, 0, 10),
                R.string.terminal to arrayListOf(0, 10, 15),
                R.string.shuttlecock_i to arrayListOf(20, 20, 25)
            )
            return timeDelta[stopID]!![when(shuttleType){
                "DH" -> 0
                "DY" -> 1
                "C" -> 2
                else -> 0
            }]
        }
    }

    private suspend fun fetchShuttleDate() {
        try {
            val query = client.query(ShuttleDateQuery()).execute().data
            shuttlePeriod = query?.shuttle?.period!!
            shuttleWeekday = query.shuttle.weekday
        } catch (e: ApolloNetworkException) {
//            showErrorToast.postValue(Event(R.string.error_fetch_shuttle_date))
        }
    }

    private suspend fun fetchShuttleTimetable(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val startTime = LocalTime.now().minusMinutes(30).toString()
        if (shuttlePeriod == null || shuttleWeekday == null) {
            val fetchShuttlePeriodJob = CoroutineScope(Dispatchers.IO).async {
                fetchShuttleDate()
            }
            fetchShuttlePeriodJob.await()
        }
        if (shuttlePeriod != null && shuttleWeekday != null) {
            try {
                val result = client.query(
                    ShuttleTimetableQuery(
                        period =  shuttlePeriod!!,
                        weekday = Optional.presentIfNotNull(shuttleWeekday!!),
                        startTime = Optional.presentIfNotNull(startTime),
                        count = Optional.presentIfNotNull(5)
                    )
                ).execute()
                if (result.data != null) {
                    shuttleTimetable = result.data!!.shuttle.timetable.sortedBy { it.shuttleTime }
                    appWidgetIds.forEach { appWidgetId -> updateAppWidget(context, appWidgetManager, appWidgetId, shuttleTimetable) }
                } else {
                    Log.e("ShuttleWidgetProvider", "fetchShuttleTimetable: ${result.errors}")
                }
            } catch (e: ApolloNetworkException) {
                Log.e("ShuttleWidgetProvider", "fetchShuttleTimetable: ${e.message}")
            }
        }
    }
}