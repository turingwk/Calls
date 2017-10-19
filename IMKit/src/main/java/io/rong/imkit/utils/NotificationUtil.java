package io.rong.imkit.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by apple on 2017/10/19.
 */

public class NotificationUtil {
    public NotificationUtil() {
    }

    public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId, int defaults) {
        Notification notification = createNotification(context, title, content, intent, defaults);
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notification != null) {
            nm.notify(notificationId, notification);
        }

    }

    public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId) {
        showNotification(context, title, content, intent, notificationId, -1);
    }

    public static void clearNotification(Context context, int notificationId) {
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }

    private static Notification createNotification(Context context, String title, String content, PendingIntent pendingIntent, int defaults) {
        String tickerText = context.getResources().getString(context.getResources().getIdentifier("rc_notification_ticker_text", "string", context.getPackageName()));
        Notification notification;
        if(Build.VERSION.SDK_INT < 11) {
            try {
                notification = new Notification(context.getApplicationInfo().icon, tickerText, System.currentTimeMillis());
                Class smallIcon = Notification.class;
                Method isLollipop = smallIcon.getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class});
                isLollipop.invoke(notification, new Object[]{context, title, content, pendingIntent});
                notification.flags = 48;
                notification.defaults = -1;
            } catch (Exception var12) {
                var12.printStackTrace();
                return null;
            }
        } else {
            boolean isLollipop1 = Build.VERSION.SDK_INT >= 21;
            int smallIcon1 = context.getResources().getIdentifier("notification_small_icon", "drawable", context.getPackageName());
            if(smallIcon1 <= 0 || !isLollipop1) {
                smallIcon1 = context.getApplicationInfo().icon;
            }

            BitmapDrawable bitmapDrawable = (BitmapDrawable)context.getApplicationInfo().loadIcon(context.getPackageManager());
            Bitmap appIcon = bitmapDrawable.getBitmap();
            Notification.Builder builder = new Notification.Builder(context);
            builder.setLargeIcon(appIcon);
            builder.setSmallIcon(smallIcon1);
            builder.setTicker(tickerText);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setOngoing(true);
            builder.setDefaults(defaults);
            notification = builder.getNotification();
        }

        return notification;
    }

    public static int getRingerMode(Context context) {
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode();
    }
}