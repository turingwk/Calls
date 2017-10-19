package io.rong.imkit.manager;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by apple on 2017/10/19.
 */

public class InternalModuleManager {
    private static final String TAG = "InternalModuleManager";
    private static IExternalModule callModule;

    private InternalModuleManager() {
    }

    public static InternalModuleManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public static void init(Context context) {
        RLog.i("InternalModuleManager", "init");

        try {
            String e = "io.rong.callkit.RongCallModule";
            Class cls = Class.forName(e);
            Constructor constructor = cls.getConstructor(new Class[0]);
            callModule = (IExternalModule)constructor.newInstance(new Object[0]);
            callModule.onCreate(context);
        } catch (Exception var4) {
            RLog.i("InternalModuleManager", "Can not find RongCallModule.");
        }

    }

    public void onInitialized(String appKey) {
        RLog.i("InternalModuleManager", "onInitialized");
        if(callModule != null) {
            callModule.onInitialized(appKey);
        }

    }

    public List<IPluginModule> getExternalPlugins(Conversation.ConversationType conversationType) {
        ArrayList pluginModules = new ArrayList();
        if(callModule != null && (conversationType.equals(Conversation.ConversationType.PRIVATE) || conversationType.equals(Conversation.ConversationType.DISCUSSION) || conversationType.equals(Conversation.ConversationType.GROUP))) {
            pluginModules.addAll(callModule.getPlugins(conversationType));
        }

        return pluginModules;
    }

    public void onConnected(String token) {
        RLog.i("InternalModuleManager", "onConnected");
        if(callModule != null) {
            callModule.onConnected(token);
        }

    }

    public void onLoaded() {
        RLog.i("InternalModuleManager", "onLoaded");
        if(callModule != null) {
            callModule.onViewCreated();
        }

    }

    static class SingletonHolder {
        static InternalModuleManager sInstance = new InternalModuleManager();

        SingletonHolder() {
        }
    }
}
