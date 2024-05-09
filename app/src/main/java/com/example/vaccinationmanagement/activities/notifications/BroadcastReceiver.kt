import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vaccinationmanagement.R

class ReminderBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val builder = NotificationCompat.Builder(context, "reminder")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Vaccination Reminder")
            .setContentText("You have a vaccination appointment in one hour.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(200, builder.build())
        }
    }
}