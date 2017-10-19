//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.content.Context;

import io.rong.common.RLog;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.utils.SystemUtils;
import io.rong.imlib.RongIMClient;

public class RongIM {
    private static final String TAG = RongIM.class.getSimpleName();
    private static Context mContext;

    private RongIM() {
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

