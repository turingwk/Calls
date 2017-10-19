package io.rong.imkit.plugin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import io.rong.imkit.RongExtension;

/**
 * Created by apple on 2017/10/19.
 */

public interface IPluginModule {
    String obtainTitle(Context var1);

    void onClick(Fragment var1, RongExtension var2);

    void onActivityResult(int var1, int var2, Intent var3);
}
