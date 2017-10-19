//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imageloader.cache.disc.impl.ext.LruDiskCache;
import io.rong.imageloader.cache.disc.naming.Md5FileNameGenerator;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imageloader.core.ImageLoaderConfiguration.Builder;
import io.rong.imageloader.utils.L;
import io.rong.imageloader.utils.StorageUtils;
import io.rong.imkit.utils.RongAuthImageDownloader;
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
        ImageLoader.getInstance().init(this.getDefaultConfig(this.getApplicationContext()));
    }

    private ImageLoaderConfiguration getDefaultConfig(Context context) {
        String path = FileUtils.getInternalCachePath(context, "image");
        File cacheDir;
        if(TextUtils.isEmpty(path)) {
            cacheDir = StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + "/cache/image/");
        } else {
            cacheDir = new File(path);
        }

        ImageLoaderConfiguration config;
        try {
            config = (new Builder(context)).threadPoolSize(3).threadPriority(3).denyCacheImageMultipleSizesInMemory().diskCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 0L)).imageDownloader(new RongAuthImageDownloader(this)).defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();
            L.writeLogs(false);
            return config;
        } catch (IOException var6) {
            RLog.i("RongContext", "Use default ImageLoader config.");
            config = ImageLoaderConfiguration.createDefault(context);
            return config;
        }
    }

    public EventBus getEventBus() {
        return this.mBus;
    }


    public String getToken() {
        return this.getSharedPreferences("RongKitConfig", 0).getString("token", "");
    }
}
