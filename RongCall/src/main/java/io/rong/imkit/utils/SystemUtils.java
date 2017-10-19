package io.rong.imkit.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.Iterator;
import java.util.List;

/**
 * Created by apple on 2017/10/19.
 */

public class SystemUtils {
    public SystemUtils() {
    }

    public static String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        if(runningAppProcessInfos == null) {
            return null;
        } else {
            Iterator var4 = runningAppProcessInfos.iterator();

            ActivityManager.RunningAppProcessInfo appProcess;
            do {
                if(!var4.hasNext()) {
                    return null;
                }

                appProcess = (ActivityManager.RunningAppProcessInfo)var4.next();
            } while(appProcess.pid != pid);

            return appProcess.processName;
        }
    }
}
