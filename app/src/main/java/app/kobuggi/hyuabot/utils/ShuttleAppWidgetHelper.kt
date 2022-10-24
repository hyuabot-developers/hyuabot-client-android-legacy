package app.kobuggi.hyuabot.utils

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R

class ShuttleAppWidgetHelper(private val context: Context) {
    private var title: String? = null
    private var description: String? = null
    private var pendingIntent: (() -> PendingIntent)? = null
    private var types = listOf<Int>()
    private var widgetId: Int = 0

    private lateinit var appWidgetManager: AppWidgetManager

    fun onInitialize(title: String? = null, description: String? = null): ShuttleAppWidgetHelper = apply {
        appWidgetManager = AppWidgetManager.getInstance(context)
        this.title = title
        this.description = description
    }

    fun setWidgetId(id: Int?): ShuttleAppWidgetHelper = apply {
        this.widgetId = id ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    fun setTitleText(title: String?): ShuttleAppWidgetHelper = apply {
        this.title = title
    }

    fun setShuttleType(types: List<Int>): ShuttleAppWidgetHelper = apply {
        this.types = types
    }

    fun setOnClickListener(pendingIntent: () -> PendingIntent): ShuttleAppWidgetHelper = apply {
        this.pendingIntent = pendingIntent
    }

    fun build() {
        // layout_covid_appwidget.xml 이 홈 위젯에 추가
        RemoteViews(context.packageName, R.layout.widget_shuttle).apply {
            setTextViewText(R.id.shuttle_stop_name, title)
            setViewVisibility(R.id.shuttle_station, View.VISIBLE)
            setTextViewText(R.id.shuttle_station, context.getString(types[0]))
            if (types.size > 1) {
                setViewVisibility(R.id.shuttle_terminal, View.VISIBLE)
                setTextViewText(R.id.shuttle_terminal, context.getString(types[1]))
            }
        }.also { views ->
            appWidgetManager.updateAppWidget(widgetId, views)
        }

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }

        (context as GlobalActivity).apply {
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }
}