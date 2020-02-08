package com.dti.cornell.events.utils.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.dti.cornell.events.DetailsActivity;
import com.dti.cornell.events.MainActivity;
import com.dti.cornell.events.R;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.SettingsUtil;

import org.joda.time.format.DateTimeFormat;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyWorker extends Worker {

    Event e;
    Location loc;
    Context context;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.e = Event.fromString(this.getInputData().getString("event"));
        this.loc = Location.fromString(this.getInputData().getString("location"));
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerNotification();

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Data.NOTIFICATION_TAG, "even notifications", importance);
            channel.setDescription("eve notifications");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void triggerNotification(){
        try{
            createNotificationChannel();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.OPEN_EVENT, true);
            intent.putExtra(DetailsActivity.EVENT_KEY, e.toString());

            SettingsUtil.createSingleton(context);
            SettingsUtil.SINGLETON.doLoad();

            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
            Notification notification = new NotificationCompat.Builder(context, Data.NOTIFICATION_TAG)
                    .setTicker(e.title)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(Color.parseColor("#E84646"))
                    .setContentTitle(e.title)
                    .setContentText(e.startTime.toString(DateTimeFormat.shortTime()) + " - " + loc.room + ", " + loc.building)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(e.id, notification);
        } catch (Error e){
            Log.e("eve NotifyWorker", e.getMessage());
        }
    }
}