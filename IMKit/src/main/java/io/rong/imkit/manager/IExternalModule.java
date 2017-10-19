package io.rong.imkit.manager;

import android.content.Context;

import java.util.List;

import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by apple on 2017/10/19.
 */

public interface IExternalModule {
    void onCreate(Context var1);

    void onInitialized(String var1);

    void onConnected(String var1);

    void onViewCreated();

    List<IPluginModule> getPlugins(Conversation.ConversationType var1);

    void onDisconnected();
}
