package io.rong.imkit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import io.rong.imlib.model.Conversation;

/**
 * Created by apple on 2017/10/19.
 */

public class RongExtension extends LinearLayout {
    private static final String TAG = "RongExtension";
    private Conversation.ConversationType mConversationType;
    private String mTargetId;

    public RongExtension(Context context) {
        super(context);
    }

    public RongExtension(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
