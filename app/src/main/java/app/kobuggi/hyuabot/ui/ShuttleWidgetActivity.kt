package app.kobuggi.hyuabot.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityAddShuttleWidgetBinding
import app.kobuggi.hyuabot.ui.shuttle.ShuttleStopListAdapter
import app.kobuggi.hyuabot.ui.shuttle.ShuttleWidgetProvider
import app.kobuggi.hyuabot.ui.shuttle.ShuttleWidgetUtility

class ShuttleWidgetActivity: GlobalActivity() {
    private val binding by lazy { ActivityAddShuttleWidgetBinding.inflate(layoutInflater) }
    private var appWidgetID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.add_shuttle_widget)
        val adapter = ShuttleStopListAdapter(this){
            onClickButton(it)
        }
        binding.shuttleStopList.adapter = adapter
        binding.shuttleStopList.layoutManager = LinearLayoutManager(this)
    }

    private fun onClickButton(resID: Int){
        appWidgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetID == AppWidgetManager.INVALID_APPWIDGET_ID) finish()
        createWidget(resID)
    }

    private fun createWidget(resID: Int){
        ShuttleWidgetUtility.saveWidgetStopID(this, appWidgetID!!, resID)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        ShuttleWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetID!!, null)
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}