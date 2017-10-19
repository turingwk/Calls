package com.wk.calls;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.rong.callkit.RongCallKit;
import io.rong.imkit.RongContext;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.model.Event;
import io.rong.imlib.RongIMClient;

public class MainActivity extends Activity {
    boolean is156 = false;
    Button btn;
    Button btnDisconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is156) {
                    RongCallKit.startSingleCall(MainActivity.this, "15210510879", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                } else {
                    RongCallKit.startSingleCall(MainActivity.this, "15652924953", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                }
            }
        });
        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "断开", Toast.LENGTH_SHORT).show();
                RongIMClient.getInstance().logout();
            }
        });
        connect();
    }
    public void connect() {
        String token = "";
        if (is156) {
            token = "2dvrOG/UlPXcCLOB298BOswh1vb/piBykAmRrhy0oH2D3NVrhenp2wO4+S2/z12KWmpsZsjHC8mLTq7ar/mlVrJ+L3fRhLHC";
        } else {
            token = "pPYdgwemgWT7vfxDxxZV1LPBnvKp96bBJHcQ2POslAg/Q9oYLkzw7PjVkJ94blT9OwAJpA0g78xc8jn7DPvdJhhtBkthT7KD";
        }
        final String finalToken = token;
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            public void onSuccess(String userId) {
                Log.e("wk", "onSuccess " + userId);
                RongContext.getInstance().getEventBus().post(Event.ConnectEvent.obtain(true));
                InternalModuleManager.getInstance().onConnected(finalToken);
            }

            public void onError(RongIMClient.ErrorCode e) {
                RongContext.getInstance().getEventBus().post(Event.ConnectEvent.obtain(false));
            }

            public void onTokenIncorrect() {
            }
        });
    }
}
