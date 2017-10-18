//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import io.rong.common.RLog;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.notification.MessageNotificationManager;
import io.rong.imkit.utils.SystemUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;

public class RongIM {
    private static final String TAG = RongIM.class.getSimpleName();
    private static final int ON_SUCCESS_CALLBACK = 100;
    private static final int ON_PROGRESS_CALLBACK = 101;
    private static final int ON_CANCEL_CALLBACK = 102;
    private static final int ON_ERROR_CALLBACK = 103;
    private static Context mContext;
    static OnReceiveMessageListener sMessageListener;
    static ConnectionStatusListener sConnectionStatusListener;
    private RongIMClientWrapper mClientWrapper;
    private String mAppKey;
    private static boolean notificationQuiteHoursConfigured;

    private RongIM() {
        this.mClientWrapper = new RongIMClientWrapper();
    }

    private static void saveToken(String token) {
        SharedPreferences preferences = mContext.getSharedPreferences("RongKitConfig", 0);
        Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public static void init(Context context) {
        String current = SystemUtils.getCurProcessName(context);
        String mainProcessName = context.getPackageName();
        if(!mainProcessName.equals(current)) {
            RLog.w(TAG, "Init. Current process : " + current);
        } else {
            RLog.i(TAG, "init : " + current);
            mContext = context;
            RongContext.init(context);
            RongIMClient.init(context, "vnroth0kv429o");
            InternalModuleManager.init(context);
            InternalModuleManager.getInstance().onInitialized("vnroth0kv429o");
        }
    }

    /** @deprecated */
    @Deprecated
    public void disconnect(boolean isReceivePush) {
        RongIMClient.getInstance().disconnect(isReceivePush);
    }

    public void logout() {
        String current = SystemUtils.getCurProcessName(mContext);
        String mainProcessName = mContext.getPackageName();
        if(!mainProcessName.equals(current)) {
            RLog.w(TAG, "only can logout in main progress! current process is:" + current);
        } else {
            RongIMClient.getInstance().logout();
            notificationQuiteHoursConfigured = false;
            MessageNotificationManager.getInstance().clearNotificationQuietHours();
        }
    }

    public static RongIM getInstance() {
        return RongIM.SingletonHolder.sRongIM;
    }

    static class SingletonHolder {
        static RongIM sRongIM = new RongIM();

        SingletonHolder() {
        }
    }
}

