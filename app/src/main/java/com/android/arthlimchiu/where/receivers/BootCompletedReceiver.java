package com.android.arthlimchiu.where.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.arthlimchiu.where.WhereTrackActivity;
import com.android.arthlimchiu.where.services.LoadGeofencesIntentService;
import com.android.arthlimchiu.where.services.NewTrackService;

import java.util.Calendar;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        loadGeofences(context);

        setUpNewTrackAlarm(context);
    }

    private void loadGeofences(Context context) {
        Intent loadGeofences = new Intent(context, LoadGeofencesIntentService.class);
        context.startService(loadGeofences);
    }

    private void setUpNewTrackAlarm(Context context) {
        Intent newTrackIntent = new Intent(context, NewTrackService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, newTrackIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

}
