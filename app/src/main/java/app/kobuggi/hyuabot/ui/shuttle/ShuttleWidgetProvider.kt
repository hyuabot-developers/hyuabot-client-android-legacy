package app.kobuggi.hyuabot.ui.shuttle

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.RemoteViews
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ui.MainActivity

class ShuttleWidgetProvider: AppWidgetProvider() {
    // 앱 데이터 갱신을 요구할 때
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId -> updateAppWidget(context, appWidgetManager, appWidgetId) }
    }

    // 해당 종류로 마지막 위젯이 삭제될 때
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId -> ShuttleWidgetUtility.deleteWidgetStopID(context, appWidgetId) }
    }

    companion object {
        private const val REQUEST_CODE_OPEN_ACTIVITY = 1
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            fun constructRemoteView(resID: Int) = RemoteViews(context.packageName, R.layout.widget_shuttle).apply {
                val types = when(resID){
                    R.string.dormitory -> listOf(R.string.shuttle_type_station, R.string.shuttle_type_terminal)
                    R.string.shuttlecock_o -> listOf(R.string.shuttle_type_station, R.string.shuttle_type_terminal)
                    R.string.station -> listOf(R.string.shuttle_type_campus, R.string.shuttle_type_terminal)
                    R.string.terminal -> listOf(R.string.shuttle_type_campus)
                    R.string.shuttlecock_i -> listOf(R.string.shuttle_type_dormitory)
                    else -> listOf()
                }
                setTextViewText(R.id.shuttle_stop_name, context.getString(resID).replace(" ", "\n").replace("(", "\n("))
                if (types.size > 1){
                    setTextViewText(R.id.shuttle_station, context.getString(types[0]))
                    setTextViewText(R.id.shuttle_terminal, context.getString(types[1]))
                }
                else{
                    setTextViewText(R.id.shuttle_station, context.getString(types[0]))
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
            val remoteViews = constructRemoteView(stopID)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }
}