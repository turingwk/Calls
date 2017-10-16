//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.userInfoCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;

import io.rong.common.RLog;
import io.rong.imkit.RongIM;
import io.rong.imkit.cache.RongCache;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.utils.StringUtils;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Conversation.PublicServiceType;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

public class RongUserInfoManager implements Callback {
    private static final String TAG = "RongUserInfoManager";
    private static final int USER_CACHE_MAX_COUNT = 256;
    private static final int PUBLIC_ACCOUNT_CACHE_MAX_COUNT = 64;
    private static final int GROUP_CACHE_MAX_COUNT = 128;
    private static final int DISCUSSION_CACHE_MAX_COUNT = 16;
    private static final int EVENT_INIT = 0;
    private static final int EVENT_CONNECT = 1;
    private static final int EVENT_GET_USER_INFO = 2;
    private static final int EVENT_GET_GROUP_INFO = 3;
    private static final int EVENT_GET_GROUP_USER_INFO = 4;
    private static final int EVENT_GET_DISCUSSION = 5;
    private static final int EVENT_UPDATE_USER_INFO = 7;
    private static final int EVENT_UPDATE_GROUP_USER_INFO = 8;
    private static final int EVENT_UPDATE_GROUP_INFO = 9;
    private static final int EVENT_UPDATE_DISCUSSION = 10;
    private static final int EVENT_LOGOUT = 11;
    private static final int EVENT_CLEAR_CACHE = 12;
    private static final String GROUP_PREFIX = "groups";
    private RongDatabaseDao mRongDatabaseDao;
    private RongCache<String, UserInfo> mUserInfoCache;
    private RongCache<String, GroupUserInfo> mGroupUserInfoCache;
    private RongCache<String, RongConversationInfo> mGroupCache;
    private RongCache<String, RongConversationInfo> mDiscussionCache;
    private RongCache<String, PublicServiceProfile> mPublicServiceProfileCache;
    private RongCache<String, String> mRequestCache;
    private IRongCacheListener mCacheListener;
    private boolean mIsCacheUserInfo;
    private boolean mIsCacheGroupInfo;
    private boolean mIsCacheGroupUserInfo;
    private Handler mWorkHandler;
    private String mAppKey;
    private String mUserId;
    private boolean mInitialized;
    private Context mContext;

    private RongUserInfoManager() {
        this.mIsCacheUserInfo = true;
        this.mIsCacheGroupInfo = true;
        this.mIsCacheGroupUserInfo = true;
        this.mUserInfoCache = new RongCache(256);
        this.mGroupUserInfoCache = new RongCache(256);
        this.mGroupCache = new RongCache(128);
        this.mDiscussionCache = new RongCache(16);
        this.mRequestCache = new RongCache(64);
        this.mPublicServiceProfileCache = new RongCache(64);
        HandlerThread workThread = new HandlerThread("RongUserInfoManager");
        workThread.start();
        this.mWorkHandler = new Handler(workThread.getLooper(), this);
        this.mInitialized = false;
    }

    public void setIsCacheUserInfo(boolean mIsCacheUserInfo) {
        this.mIsCacheUserInfo = mIsCacheUserInfo;
    }

    public void setIsCacheGroupInfo(boolean mIsCacheGroupInfo) {
        this.mIsCacheGroupInfo = mIsCacheGroupInfo;
    }

    public void setIsCacheGroupUserInfo(boolean mIsCacheGroupUserInfo) {
        this.mIsCacheGroupUserInfo = mIsCacheGroupUserInfo;
    }

    public static RongUserInfoManager getInstance() {
        return RongUserInfoManager.SingletonHolder.sInstance;
    }

    public boolean handleMessage(Message msg) {
        String userId;
        String groupId;
        Group group;
        GroupUserInfo groupUserInfo;
        Discussion discussion;
        RongConversationInfo conversationInfo;
        RongConversationInfo oldConversationInfo;
        UserInfo userInfo1;
        switch(msg.what) {
            case 0:
                this.mRongDatabaseDao = new RongDatabaseDao();
                if(!TextUtils.isEmpty(this.mUserId)) {
                    this.mRongDatabaseDao.open(this.mContext, this.mAppKey, this.mUserId);
                }
                break;
            case 1:
                userId = (String)msg.obj;
                if(TextUtils.isEmpty(this.mUserId)) {
                    this.mUserId = userId;
                    RLog.d("RongUserInfoManager", "onConnected, userId = " + userId);
                    this.updateCachedUserId(userId);
                    if(this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.open(this.mContext, this.mAppKey, this.mUserId);
                    }
                } else if(!this.mUserId.equals(userId)) {
                    RLog.d("RongUserInfoManager", "onConnected, user changed, old userId = " + this.mUserId + ", userId = " + userId);
                    this.clearUserInfoCache();
                    this.mUserId = userId;
                    this.updateCachedUserId(userId);
                    if(this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.close();
                        this.mRongDatabaseDao.open(this.mContext, this.mAppKey, this.mUserId);
                    }
                }
                break;
            case 2:
                userId = (String)msg.obj;
                userInfo1 = null;
                if(this.mRongDatabaseDao != null) {
                    userInfo1 = this.mRongDatabaseDao.getUserInfo(userId);
                }

                if(userInfo1 != null && userInfo1.getPortraitUri() != null) {
                    Uri oldUserInfo1 = userInfo1.getPortraitUri();
                    if(oldUserInfo1.toString().toLowerCase().startsWith("file://")) {
                        File file = new File(oldUserInfo1.toString().substring(7));
                        if(!file.exists()) {
                            userInfo1 = null;
                        }
                    } else if(oldUserInfo1.toString().equals("")) {
                        userInfo1 = null;
                    }
                }

                if(userInfo1 == null) {
                    if(this.mCacheListener != null) {
                        userInfo1 = this.mCacheListener.getUserInfo(userId);
                    }

                    if(userInfo1 != null) {
                        this.putUserInfoInDB(userInfo1);
                    }
                }

                if(userInfo1 != null) {
                    this.putUserInfoInCache(userInfo1);
                    this.mRequestCache.remove(userId);
                    if(this.mCacheListener != null) {
                        this.mCacheListener.onUserInfoUpdated(userInfo1);
                    }
                }
                break;
            case 3:
                groupId = (String)msg.obj;
                group = null;
                String cacheGroupId = "groups" + groupId;
                if(this.mRongDatabaseDao != null) {
                    group = this.mRongDatabaseDao.getGroupInfo(groupId);
                }

                if(group != null && group.getPortraitUri() != null) {
                    Uri groupUserInfo1 = group.getPortraitUri();
                    if(groupUserInfo1.toString().toLowerCase().startsWith("file://")) {
                        File discussionId1 = new File(groupUserInfo1.toString().substring(7));
                        if(!discussionId1.exists()) {
                            group = null;
                        }
                    } else if(groupUserInfo1.toString().equals("")) {
                        group = null;
                    }
                }

                if(group == null) {
                    if(this.mCacheListener != null) {
                        group = this.mCacheListener.getGroupInfo(groupId);
                    }

                    if(group != null && this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.putGroupInfo(group);
                    }
                }

                if(group != null) {
                    RongConversationInfo groupUserInfo2 = new RongConversationInfo(ConversationType.GROUP.getValue() + "", group.getId(), group.getName(), group.getPortraitUri());
                    this.mGroupCache.put(groupId, groupUserInfo2);
                    this.mRequestCache.remove(cacheGroupId);
                    if(this.mCacheListener != null) {
                        this.mCacheListener.onGroupUpdated(group);
                    }
                }
                break;
            case 4:
                groupUserInfo = null;
                groupId = StringUtils.getArg1((String)msg.obj);
                userId = StringUtils.getArg2((String)msg.obj);
                if(this.mRongDatabaseDao != null) {
                    groupUserInfo = this.mRongDatabaseDao.getGroupUserInfo(groupId, userId);
                }

                if(groupUserInfo == null) {
                    if(this.mCacheListener != null) {
                        groupUserInfo = this.mCacheListener.getGroupUserInfo(groupId, userId);
                    }

                    if(groupUserInfo != null && this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.putGroupUserInfo(groupUserInfo);
                    }
                }

                if(groupUserInfo != null) {
                    this.mGroupUserInfoCache.put((String)msg.obj, groupUserInfo);
                    this.mRequestCache.remove((String)msg.obj);
                    if(this.mCacheListener != null) {
                        this.mCacheListener.onGroupUserInfoUpdated(groupUserInfo);
                    }
                }
                break;
            case 5:
                final String discussionId = (String)msg.obj;
                discussion = null;
                if(this.mRongDatabaseDao != null) {
                    discussion = this.mRongDatabaseDao.getDiscussionInfo(discussionId);
                }

                if(discussion != null) {
                    RongConversationInfo key1 = new RongConversationInfo(ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), (Uri)null);
                    this.mDiscussionCache.put(discussionId, key1);
                    if(this.mCacheListener != null) {
                        this.mCacheListener.onDiscussionUpdated(discussion);
                    }
                } else {
                    RongIM.getInstance().getDiscussion(discussionId, new ResultCallback<Discussion>() {
                        public void onSuccess(Discussion discussion) {
                            if(discussion != null) {
                                if(RongUserInfoManager.this.mRongDatabaseDao != null) {
                                    RongUserInfoManager.this.mRongDatabaseDao.putDiscussionInfo(discussion);
                                }

                                RongConversationInfo conversationInfo = new RongConversationInfo(ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), (Uri)null);
                                RongUserInfoManager.this.mDiscussionCache.put(discussionId, conversationInfo);
                                if(RongUserInfoManager.this.mCacheListener != null) {
                                    RongUserInfoManager.this.mCacheListener.onDiscussionUpdated(discussion);
                                }
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
            case 6:
            default:
                break;
            case 7:
                userInfo1 = (UserInfo)msg.obj;
                UserInfo oldUserInfo = this.putUserInfoInCache(userInfo1);
                if(oldUserInfo == null || oldUserInfo.getName() == null || oldUserInfo.getPortraitUri() == null || userInfo1.getName() != null || userInfo1.getPortraitUri() != null) {
                    this.putUserInfoInDB(userInfo1);
                    this.mRequestCache.remove(userInfo1.getUserId());
                    if(this.mCacheListener != null) {
                        this.mCacheListener.onUserInfoUpdated(userInfo1);
                    }
                }
                break;
            case 8:
                groupUserInfo = (GroupUserInfo)msg.obj;
                String key = StringUtils.getKey(groupUserInfo.getGroupId(), groupUserInfo.getUserId());
                GroupUserInfo oldGroupUserInfo = (GroupUserInfo)this.mGroupUserInfoCache.put(key, groupUserInfo);
                if(oldGroupUserInfo == null || oldGroupUserInfo.getNickname() != null && groupUserInfo.getNickname() != null && !oldGroupUserInfo.getNickname().equals(groupUserInfo.getNickname())) {
                    this.mRequestCache.remove(key);
                    if(this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.putGroupUserInfo(groupUserInfo);
                    }

                    if(this.mCacheListener != null) {
                        this.mCacheListener.onGroupUserInfoUpdated(groupUserInfo);
                    }
                }
                break;
            case 9:
                group = (Group)msg.obj;
                conversationInfo = new RongConversationInfo(ConversationType.GROUP.getValue() + "", group.getId(), group.getName(), group.getPortraitUri());
                oldConversationInfo = (RongConversationInfo)this.mGroupCache.put(conversationInfo.getId(), conversationInfo);
                if(oldConversationInfo == null || oldConversationInfo.getName() == null || oldConversationInfo.getUri() == null || conversationInfo.getName() != null || conversationInfo.getUri() != null) {
                    String userInfo = "groups" + group.getId();
                    this.mRequestCache.remove(userInfo);
                    if(this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.putGroupInfo(group);
                    }

                    if(this.mCacheListener != null) {
                        this.mCacheListener.onGroupUpdated(group);
                    }
                }
                break;
            case 10:
                discussion = (Discussion)msg.obj;
                conversationInfo = new RongConversationInfo(ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), (Uri)null);
                oldConversationInfo = (RongConversationInfo)this.mDiscussionCache.put(conversationInfo.getId(), conversationInfo);
                if(oldConversationInfo == null || oldConversationInfo.getName() != null && conversationInfo.getName() != null && !oldConversationInfo.getName().equals(conversationInfo.getName())) {
                    if(this.mRongDatabaseDao != null) {
                        this.mRongDatabaseDao.putDiscussionInfo(discussion);
                    }

                    if(this.mCacheListener != null) {
                        this.mCacheListener.onDiscussionUpdated(discussion);
                    }
                }
                break;
            case 11:
                this.clearUserInfoCache();
                this.mInitialized = false;
                this.mCacheListener = null;
                this.mUserId = null;
                this.mAppKey = null;
                if(this.mRongDatabaseDao != null) {
                    this.mRongDatabaseDao.close();
                    this.mRongDatabaseDao = null;
                }

                this.updateCachedUserId("");
                break;
            case 12:
                this.mRequestCache.clear();
        }

        return false;
    }

    public void init(Context context, String appKey, IRongCacheListener listener) {
        if(TextUtils.isEmpty(appKey)) {
            RLog.e("RongUserInfoManager", "init, appkey is null.");
        } else if(this.mInitialized) {
            RLog.d("RongUserInfoManager", "has been init, no need init again");
        } else {
            this.mContext = context;
            this.mUserId = this.getCachedUserId();
            this.mAppKey = appKey;
            this.mCacheListener = listener;
            this.mInitialized = true;
            this.mWorkHandler.sendEmptyMessage(0);
            RLog.d("RongUserInfoManager", "init, mUserId = " + this.mUserId);
        }
    }

    public void onConnected(String userId) {
        if(TextUtils.isEmpty(userId)) {
            RLog.e("RongUserInfoManager", "onConnected, appkey is null.");
        } else {
            Message message = Message.obtain();
            message.what = 1;
            message.obj = userId;
            this.mWorkHandler.sendMessage(message);
        }
    }

    private void clearUserInfoCache() {
        if(this.mUserInfoCache != null) {
            this.mUserInfoCache.clear();
        }

        if(this.mDiscussionCache != null) {
            this.mDiscussionCache.clear();
        }

        if(this.mGroupCache != null) {
            this.mGroupCache.clear();
        }

        if(this.mGroupUserInfoCache != null) {
            this.mGroupUserInfoCache.clear();
        }

        if(this.mPublicServiceProfileCache != null) {
            this.mPublicServiceProfileCache.clear();
        }

        this.mRequestCache.clear();
    }

    public void uninit() {
        RLog.i("RongUserInfoManager", "uninit");
        this.mWorkHandler.sendEmptyMessage(11);
    }

    private String getCachedUserId() {
        if(this.mContext != null) {
            SharedPreferences preferences = this.mContext.getSharedPreferences("RongKitConfig", 0);
            return preferences.getString("userID", (String)null);
        } else {
            return null;
        }
    }

    private void updateCachedUserId(String userId) {
        if(this.mContext != null) {
            SharedPreferences preferences = this.mContext.getSharedPreferences("RongKitConfig", 0);
            Editor editor = preferences.edit();
            editor.putString("userID", userId);
            editor.commit();
        }

    }

    private UserInfo putUserInfoInCache(UserInfo info) {
        return this.mUserInfoCache != null?(UserInfo)this.mUserInfoCache.put(info.getUserId(), info):null;
    }

    private void insertUserInfoInDB(UserInfo info) {
        if(this.mRongDatabaseDao != null) {
            this.mRongDatabaseDao.insertUserInfo(info);
        }

    }

    private void putUserInfoInDB(UserInfo info) {
        if(this.mRongDatabaseDao != null) {
            this.mRongDatabaseDao.putUserInfo(info);
        }

    }

    public UserInfo getUserInfo(String id) {
        RLog.i("RongUserInfoManager", "getUserInfo : " + id);
        if(TextUtils.isEmpty(id)) {
            return null;
        } else {
            UserInfo info = null;
            if(this.mIsCacheUserInfo) {
                info = (UserInfo)this.mUserInfoCache.get(id);
                if(info == null) {
                    String cachedId = (String)this.mRequestCache.get(id);
                    if(cachedId != null) {
                        return null;
                    }

                    this.mRequestCache.put(id, id);
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = id;
                    this.mWorkHandler.sendMessage(message);
                    if(!this.mWorkHandler.hasMessages(12)) {
                        this.mWorkHandler.sendEmptyMessageDelayed(12, 30000L);
                    }
                }
            } else if(this.mCacheListener != null) {
                info = this.mCacheListener.getUserInfo(id);
            }

            return info;
        }
    }

    public GroupUserInfo getGroupUserInfo(String gId, String id) {
        if(gId != null && id != null) {
            String key = StringUtils.getKey(gId, id);
            GroupUserInfo info = null;
            if(this.mIsCacheGroupUserInfo) {
                info = (GroupUserInfo)this.mGroupUserInfoCache.get(key);
                if(info == null) {
                    String cachedId = (String)this.mRequestCache.get(key);
                    if(cachedId != null) {
                        return null;
                    }

                    this.mRequestCache.put(key, key);
                    Message message = Message.obtain();
                    message.what = 4;
                    message.obj = key;
                    this.mWorkHandler.sendMessage(message);
                    if(!this.mWorkHandler.hasMessages(12)) {
                        this.mWorkHandler.sendEmptyMessageDelayed(12, 30000L);
                    }
                }
            } else if(this.mCacheListener != null) {
                info = this.mCacheListener.getGroupUserInfo(gId, id);
            }

            return info;
        } else {
            return null;
        }
    }

    public Group getGroupInfo(String id) {
        if(id == null) {
            return null;
        } else {
            RLog.i("RongUserInfoManager", "getGroupInfo : " + id);
            Group groupInfo = null;
            if(this.mIsCacheGroupInfo) {
                RongConversationInfo info = (RongConversationInfo)this.mGroupCache.get(id);
                if(info == null) {
                    String cachedId = (String)this.mRequestCache.get(id);
                    if(cachedId != null) {
                        return null;
                    }

                    this.mRequestCache.put(id, id);
                    Message message = Message.obtain();
                    message.what = 3;
                    message.obj = id;
                    this.mWorkHandler.sendMessage(message);
                    if(!this.mWorkHandler.hasMessages(12)) {
                        this.mWorkHandler.sendEmptyMessageDelayed(12, 30000L);
                    }
                } else {
                    groupInfo = new Group(info.getId(), info.getName(), info.getUri());
                }
            } else if(this.mCacheListener != null) {
                groupInfo = this.mCacheListener.getGroupInfo(id);
            }

            return groupInfo;
        }
    }

    public Discussion getDiscussionInfo(String id) {
        if(id == null) {
            return null;
        } else {
            Discussion discussionInfo = null;
            RongConversationInfo info = (RongConversationInfo)this.mDiscussionCache.get(id);
            if(info == null) {
                Message message = Message.obtain();
                message.what = 5;
                message.obj = id;
                this.mWorkHandler.sendMessage(message);
            } else {
                discussionInfo = new Discussion(info.getId(), info.getName());
            }

            return discussionInfo;
        }
    }

    public PublicServiceProfile getPublicServiceProfile(final PublicServiceType type, final String id) {
        if(type != null && id != null) {
            final String key = StringUtils.getKey(type.getValue() + "", id);
            PublicServiceProfile info = (PublicServiceProfile)this.mPublicServiceProfileCache.get(key);
            if(info == null) {
                this.mWorkHandler.post(new Runnable() {
                    public void run() {
                        RongIM.getInstance().getPublicServiceProfile(type, id, new ResultCallback<PublicServiceProfile>() {
                            public void onSuccess(PublicServiceProfile result) {
                                if(result != null) {
                                    RongUserInfoManager.this.mPublicServiceProfileCache.put(key, result);
                                    if(RongUserInfoManager.this.mCacheListener != null) {
                                        RongUserInfoManager.this.mCacheListener.onPublicServiceProfileUpdated(result);
                                    }
                                }

                            }

                            public void onError(ErrorCode e) {
                            }
                        });
                    }
                });
            }

            return info;
        } else {
            return null;
        }
    }

    public void setUserInfo(UserInfo info) {
        if(this.mIsCacheUserInfo) {
            Message message = Message.obtain();
            message.what = 7;
            message.obj = info;
            this.mWorkHandler.sendMessage(message);
        } else if(this.mCacheListener != null) {
            this.mCacheListener.onUserInfoUpdated(info);
        }

    }

    public void setGroupUserInfo(GroupUserInfo info) {
        if(this.mIsCacheGroupUserInfo) {
            Message message = Message.obtain();
            message.what = 8;
            message.obj = info;
            this.mWorkHandler.sendMessage(message);
        } else if(this.mCacheListener != null) {
            this.mCacheListener.onGroupUserInfoUpdated(info);
        }

    }

    public void setGroupInfo(Group group) {
        if(this.mIsCacheGroupInfo) {
            Message message = Message.obtain();
            message.what = 9;
            message.obj = group;
            this.mWorkHandler.sendMessage(message);
        } else if(this.mCacheListener != null) {
            this.mCacheListener.onGroupUpdated(group);
        }

    }

    public void setDiscussionInfo(Discussion discussion) {
        Message message = Message.obtain();
        message.what = 10;
        message.obj = discussion;
        this.mWorkHandler.sendMessage(message);
    }

    public void setPublicServiceProfile(PublicServiceProfile profile) {
        String key = StringUtils.getKey(profile.getConversationType().getValue() + "", profile.getTargetId());
        PublicServiceProfile oldInfo = (PublicServiceProfile)this.mPublicServiceProfileCache.put(key, profile);
        if((oldInfo == null || oldInfo.getName() != null && profile.getName() != null && !oldInfo.getName().equals(profile.getName()) || oldInfo.getPortraitUri() != null && profile.getPortraitUri() != null && !oldInfo.getPortraitUri().toString().equals(profile.getPortraitUri().toString())) && this.mCacheListener != null) {
            this.mCacheListener.onPublicServiceProfileUpdated(profile);
        }

    }

    private static class SingletonHolder {
        static RongUserInfoManager sInstance = new RongUserInfoManager();

        private SingletonHolder() {
        }
    }
}
