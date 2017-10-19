package io.rong.imkit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.rong.common.RLog;
import io.rong.imlib.model.Conversation;

/**
 * Created by apple on 2017/10/19.
 */

public class RongExtension extends LinearLayout {
    private static final String TAG = "RongExtension";
    private ImageView mPSMenu;
    private View mPSDivider;
    private LinearLayout mMainBar;
    private ViewGroup mExtensionBar;
    private ViewGroup mSwitchLayout;
    private ViewGroup mContainerLayout;
    private ViewGroup mPluginLayout;
    private ViewGroup mMenuContainer;
    private View mEditTextLayout;
    private EditText mEditText;
    private View mVoiceInputToggle;
    private FrameLayout mSendToggle;
    private ImageView mEmoticonToggle;
    private ImageView mPluginToggle;
    private ImageView mVoiceToggle;
    private OnClickListener mVoiceToggleClickListener;
    private Fragment mFragment;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    private String mUserId;
    boolean isKeyBoardActive = false;
    boolean collapsed = true;
    int originalTop = 0;
    int originalBottom = 0;

    public RongExtension(Context context) {
        super(context);
        this.initView();
    }

    public RongExtension(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public void onDestroy() {
        RLog.d("RongExtension", "onDestroy");

    }

    public void setConversation(Conversation.ConversationType conversationType, String targetId) {
        if(this.mConversationType == null && this.mTargetId == null) {
            this.mConversationType = conversationType;
            this.mTargetId = targetId;
        }

        this.mConversationType = conversationType;
        this.mTargetId = targetId;
    }

    public Conversation.ConversationType getConversationType() {
        return this.mConversationType;
    }

    public String getTargetId() {
        return this.mTargetId;
    }

    private void initView() {

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
