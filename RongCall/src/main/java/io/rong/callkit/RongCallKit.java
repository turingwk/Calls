package io.rong.callkit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import io.rong.call.R;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


public class RongCallKit {

    public enum CallMediaType {
        CALL_MEDIA_TYPE_AUDIO, CALL_MEDIA_TYPE_VIDEO
    }


    /**
     * 发起单人通话。
     *
     * @param context   上下文
     * @param targetId  会话 id
     * @param mediaType 会话媒体类型
     */
    public static void startSingleCall(Context context, String targetId, CallMediaType mediaType) {
        if (checkEnvironment(context, mediaType)) {
            String action;
            if (mediaType.equals(CallMediaType.CALL_MEDIA_TYPE_AUDIO)) {
                action = RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO;
            } else {
                action = RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO;
            }
            Intent intent = new Intent(action);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            intent.putExtra("targetId", targetId);
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.setPackage(context.getPackageName());
            context.startActivity(intent);
        }
    }

    /**
     * 检查应用音视频授权信息
     * 检查网络连接状态
     * 检查是否在通话中
     *
     * @param context   启动的 activity
     * @param mediaType 启动音视频的媒体类型
     * @return 是否允许启动通话界面
     */
    private static boolean checkEnvironment(Context context, CallMediaType mediaType) {
        if (context instanceof Activity) {
            String[] permissions;
            if (mediaType.equals(CallMediaType.CALL_MEDIA_TYPE_AUDIO)) {
                permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            } else {
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
            }
            if (!PermissionCheckUtil.requestPermissions((Activity) context, permissions)) {
                return false;
            }
        }

        RongCallSession callSession = RongCallClient.getInstance().getCallSession();
        if (callSession != null && callSession.getActiveTime() > 0) {
            Toast.makeText(context,
                    callSession.getMediaType() == RongCallCommon.CallMediaType.AUDIO ?
                            context.getResources().getString(R.string.rc_voip_call_audio_start_fail) :
                            context.getResources().getString(R.string.rc_voip_call_video_start_fail),
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            Toast.makeText(context, context.getResources().getString(R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
