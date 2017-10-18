//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imlib.model.Message;

public class RongExtensionManager {
    private static final String TAG = "RongExtensionManager";
    private static String mAppKey;
    private static List<IExtensionModule> mExtModules;
    private static final String DEFAULT_REDPACKET = "com.jrmf360.rylib.modules.JrmfExtensionModule";
    private static final String DEFAULT_BQMM = "com.melink.bqmmplugin.rc.BQMMExtensionModule";

    private RongExtensionManager() {
        if(mExtModules != null) {
            Class e;
            Constructor constructor;
            IExtensionModule bqmm;
            try {
                e = Class.forName("com.jrmf360.rylib.modules.JrmfExtensionModule");
                constructor = e.getConstructor(new Class[0]);
                bqmm = (IExtensionModule)constructor.newInstance(new Object[0]);
                RLog.i("RongExtensionManager", "add module " + bqmm.getClass().getSimpleName());
                mExtModules.add(bqmm);
                bqmm.onInit(mAppKey);
            } catch (Exception var5) {
                RLog.i("RongExtensionManager", "Can\'t find com.jrmf360.rylib.modules.JrmfExtensionModule");
            }

            try {
                e = Class.forName("com.melink.bqmmplugin.rc.BQMMExtensionModule");
                constructor = e.getConstructor(new Class[0]);
                bqmm = (IExtensionModule)constructor.newInstance(new Object[0]);
                RLog.i("RongExtensionManager", "add module " + bqmm.getClass().getSimpleName());
                mExtModules.add(bqmm);
                bqmm.onInit(mAppKey);
            } catch (Exception var4) {
                RLog.i("RongExtensionManager", "Can\'t find com.melink.bqmmplugin.rc.BQMMExtensionModule");
            }
        }

    }

    public static RongExtensionManager getInstance() {
        return RongExtensionManager.SingletonHolder.sInstance;
    }

    static void init(Context context, String appKey) {
        RLog.d("RongExtensionManager", "init");
        RongUtils.init(context);
        mAppKey = appKey;
        mExtModules = new ArrayList();
    }

    public void registerExtensionModule(IExtensionModule extensionModule) {
        if(mExtModules == null) {
            RLog.e("RongExtensionManager", "Not init in the main process.");
        } else if(extensionModule != null && !mExtModules.contains(extensionModule)) {
            RLog.i("RongExtensionManager", "registerExtensionModule " + extensionModule.getClass().getSimpleName());
            if(mExtModules.size() <= 0 || !((IExtensionModule)mExtModules.get(0)).getClass().getCanonicalName().equals("com.jrmf360.rylib.modules.JrmfExtensionModule") && !((IExtensionModule)mExtModules.get(0)).getClass().getCanonicalName().equals("com.melink.bqmmplugin.rc.BQMMExtensionModule")) {
                mExtModules.add(extensionModule);
            } else {
                mExtModules.add(0, extensionModule);
            }

            extensionModule.onInit(mAppKey);
        } else {
            RLog.e("RongExtensionManager", "Illegal extensionModule.");
        }
    }

    public void unregisterExtensionModule(IExtensionModule extensionModule) {
        if(mExtModules == null) {
            RLog.e("RongExtensionManager", "Not init in the main process.");
        } else if(extensionModule != null && mExtModules.contains(extensionModule)) {
            RLog.i("RongExtensionManager", "unregisterExtensionModule " + extensionModule.getClass().getSimpleName());
            mExtModules.remove(extensionModule);
        } else {
            RLog.e("RongExtensionManager", "Illegal extensionModule.");
        }
    }

    public List<IExtensionModule> getExtensionModules() {
        return mExtModules;
    }

    public void connect(String token) {
        Iterator var2 = mExtModules.iterator();

        while(var2.hasNext()) {
            IExtensionModule extensionModule = (IExtensionModule)var2.next();
            extensionModule.onConnect(token);
        }

    }

    public void disconnect() {
        if(mExtModules != null) {
            Iterator var1 = mExtModules.iterator();

            while(var1.hasNext()) {
                IExtensionModule extensionModule = (IExtensionModule)var1.next();
                extensionModule.onDisconnect();
            }

        }
    }

    void onReceivedMessage(Message message) {
        Iterator var2 = mExtModules.iterator();

        while(var2.hasNext()) {
            IExtensionModule extensionModule = (IExtensionModule)var2.next();
            extensionModule.onReceivedMessage(message);
        }

    }

    private static class SingletonHolder {
        static RongExtensionManager sInstance = new RongExtensionManager();

        private SingletonHolder() {
        }
    }
}
