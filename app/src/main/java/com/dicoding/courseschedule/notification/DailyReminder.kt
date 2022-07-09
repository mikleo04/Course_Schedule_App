package com.dicoding.courseschedule.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.home.HomeActivity
import com.dicoding.courseschedule.util.executeThread
import java.util.*

class DailyReminder : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val repository = DataRepository.getInstance(context)
            val courses = repository?.getTodaySchedule()

            courses?.let {
                if (it.isNotEmpty()) showNotification(context, it)
            }
        }
    }

    //TODO 12 : Implement daily reminder for every 06.00 a.m using AlarmManager
    fun dailyReminder(context: Context) {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 6)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_REPEATING,
            Intent(context, DailyReminder::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val receiver = ComponentName(context, DailyReminder::class.java)

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .setInexactRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm(context: Context) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_REPEATING,
            Intent(context, DailyReminder::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, DailyReminder::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }

    private fun getPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, HomeActivity::class.java).apply {
        }
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private fun showNotification(context: Context, content: List<Course>) {
        //TODO 13 : Show today schedules in inbox style notification & open HomeActivity when notification tapped
        val notificationStyle = NotificationCompat.InboxStyle()
        val timeString = context.resources.getString(R.string.notification_message_format)
        content.forEach {
            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
            notificationStyle.addLine(courseData)
        }
        
        try {
            var mBuilder = NotificationCompat.Builder(context, "cn_id")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(context.getString(R.string.today_schedule))
                .setStyle(notificationStyle)
                .setContentIntent(getPendingIntent(context))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("cn_id", "cn_name", NotificationManager.IMPORTANCE_DEFAULT)
                mBuilder.setChannelId("cn_id")
                channel.description = "cn_name"
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(123, mBuilder.build())
        }catch (e: Exception){
            Log.d("notif", "error")
        }
    }

    companion object{
        const val ID_REPEATING = 1
    }
}