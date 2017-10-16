package com.wk.calls;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.rong.callkit.RongCallKit;
import io.rong.imlib.RongIMClient;

public class MainActivity extends Activity {
    boolean is156 = true;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is156) {
                    RongCallKit.startSingleCall(MainActivity.this, "13265315159", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                } else {
                    RongCallKit.startSingleCall(MainActivity.this, "15652924953", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                }
            }
        });
        connect();
    }
    public void connect() {
        String loginToken = "";
        if (is156) {
            loginToken = "2dvrOG/UlPXcCLOB298BOswh1vb/piBykAmRrhy0oH2D3NVrhenp2wO4+S2/z12KWmpsZsjHC8mLTq7ar/mlVrJ+L3fRhLHC";
        } else {
            loginToken = "/mym6fqDbose9nQXYgfFqybLgE0IHM1CODwYf+xe6w44eLlRTJVJXdTyt3AzmQECHhxz4TGdGOSLp4DZKlhy8J3Cmo9pSYlA";
        }
        RongIMClient.connect(loginToken, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("kk", "onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                Log.e("kk", "onSuccess " + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("kk", "onError");
            }
        });
    }
}
