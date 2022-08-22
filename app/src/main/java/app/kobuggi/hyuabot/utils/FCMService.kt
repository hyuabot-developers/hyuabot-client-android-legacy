package app.kobuggi.hyuabot.utils

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            Log.d("FCMService", "onMessageReceived: ${message.notification!!.body.toString()}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun executeLongPeriodJob(){
        val work = OneTimeWorkRequest.Builder(WorkerClass::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun executeShortPeriodJob(){
        Log.d("FCMService", "executeShortPeriodJob")
    }

    private fun sendRegistrationToServer(token: String) {
        TODO()
    }

    inner class WorkerClass(private val context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters) {
        override fun doWork(): Result {
            return Result.success()
        }
    }
}