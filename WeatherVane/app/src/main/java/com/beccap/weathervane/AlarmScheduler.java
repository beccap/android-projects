/**
 * Schedules an alarm notification for some time in the future
 *
 * All methods implemented as static
 */

package com.beccap.weathervane;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class AlarmScheduler {
    private static final String TAG = AlarmScheduler.class.toString();

    private static final int REQUEST_NOTIFY_USER = 1;

    private static Context       _context;
    private static AlarmManager  _alarmManager = null;
    private static PendingIntent _pendingIntent = null;

    // initialize - call at app startup
    public static void initialize(Context context) {
        _context = context;

        _alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmReceiverIntent = new Intent(_context, AlarmReceiver.class);
        _pendingIntent = PendingIntent.getBroadcast(_context, REQUEST_NOTIFY_USER, alarmReceiverIntent, 0);
    }

    // schedule an alarm
    public static void scheduleAlarm(long timeIntervalFromNow) {
        if (_alarmManager == null) {
            Log.w(TAG, "Alarm not scheduled because AlarmScheduler has not been initialized.");
            return;
        }
        _alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeIntervalFromNow,
                _pendingIntent);
    }

    // cancel alarms
    public static void cancelScheduledAlarms() {
        if (_alarmManager == null) {
            Log.w(TAG, "No alarms canceled because AlarmScheduler has not been initialized.");
            return;
        }
        _alarmManager.cancel(_pendingIntent);
    }
}
