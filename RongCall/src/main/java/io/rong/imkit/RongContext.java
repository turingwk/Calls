//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.eventbus.EventBus;
import io.rong.imlib.model.Conversation.ConversationType;

public class RongContext extends ContextWrapper {
    private static final String TAG = "RongContext";
    private static final int NOTIFICATION_CACHE_MAX_COUNT = 64;
    private static RongContext sContext;
    private EventBus mBus = EventBus.getDefault();
    private ExecutorService executorService;
    private List<ConversationType> mReadReceiptConversationTypeList = new ArrayList();
    private List<String> mCurrentConversationList = new ArrayList();
    Handler mHandler = new Handler(this.getMainLooper());

    public static void init(Context context) {
        if(sContext == null) {
            sContext = new RongContext(context);
        }

    }

    public static RongContext getInstance() {
        return sContext;
    }

    protected RongContext(Context base) {
        super(base);
        this.mReadReceiptConversationTypeList.add(ConversationType.PRIVATE);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public EventBus getEventBus() {
        return this.mBus;
    }


    public String getToken() {
        return this.getSharedPreferences("RongKitConfig", 0).getString("token", "");
    }
}
