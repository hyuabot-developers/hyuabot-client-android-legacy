package app.kobuggi.hyuabot.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityAddShuttleWidgetBinding
import app.kobuggi.hyuabot.ui.shuttle.ShuttleStopListAdapter
import app.kobuggi.hyuabot.utils.ShuttleAppWidgetHelper

class ShuttleWidgetActivity: GlobalActivity() {
    private val binding by lazy { ActivityAddShuttleWidgetBinding.inflate(layoutInflater) }
    private val shuttleAppWidgetHelper by lazy { ShuttleAppWidgetHelper(this) }
    private var appWidgetID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.add_shuttle_widget)
        val adapter = ShuttleStopListAdapter(this, ::onClickButton)
        binding.shuttleStopList.adapter = adapter
        binding.shuttleStopList.layoutManager = LinearLayoutManager(this)
    }

    private fun onClickButton(resID: Int){
        appWidgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        createWidget(resID)
    }

    private fun createWidget(resID: Int){
        val types = when(resID){
            R.string.dormitory -> listOf(R.string.shuttle_type_station, R.string.shuttle_type_terminal)
            R.string.shuttlecock_o -> listOf(R.string.shuttle_type_station, R.string.shuttle_type_terminal)
            R.string.station -> listOf(R.string.shuttle_type_campus, R.string.shuttle_type_terminal)
            R.string.terminal -> listOf(R.string.shuttle_type_campus)
            R.string.shuttlecock_i -> listOf(R.string.shuttle_type_dormitory)
            else -> listOf()
        }
        shuttleAppWidgetHelper.onInitialize()
            .setWidgetId(appWidgetID)
            .setTitleText(getString(resID).replace("(", "\n(").replace(" ", "\n"))
            .setShuttleType(types)
            .setOnClickListener {
                Intent(this, MainActivity::class.java)
                    .let { intent ->
                        PendingIntent.getActivity(this, 0, intent, 0)
                    }
            }.build()
    }
}