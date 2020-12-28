package com.mgc.letobox.happy.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.mgc.leto.game.base.LetoConst;
import com.mgc.leto.game.base.bean.LetoError;
import com.mgc.leto.game.base.http.HttpCallbackDecode;
import com.mgc.leto.game.base.http.SdkApi;
import com.mgc.leto.game.base.login.LoginManager;
import com.mgc.leto.game.base.utils.BaseAppUtil;
import com.mgc.letobox.happy.me.bean.ExchangeRequestBean;
import com.mgc.letobox.happy.me.bean.TaskListRequestBean;
import com.mgc.letobox.happy.me.bean.UserTaskStatusRequestBean;

/**
 * Create by zhaozhihui on 2019-09-11
 **/
public class LeBoxUtil {

    private static final String TAG = "LeBoxUtil";

    private static void printUrl(String path) {
        Log.e(TAG, "http_url=" + SdkApi.getRequestUrl() + path);
    }

    //盒子初始化
    public static String getStartup() {
        return "";
//        return SdkApi.getRequestUrl() + "system/hzstartup";
    }

    //获取盒子版本的升级信息
    public static String getLatestVersion() {
        printUrl("upgrade/box_up");
        return SdkApi.getRequestUrl() + "upgrade/box_up";
    }



    //注销账号
    public static String deleteAccount() {
        printUrl("user/deleteAccount");
        return SdkApi.getRequestUrl() + "user/deleteAccount";
    }


    //反馈
    public static String getFeedBack() {
        printUrl("user/feedback");
        return SdkApi.getRequestUrl() + "user/feedback";
    }


    /**
     * 查询新手任务列表
     */
    public static void getNewPlayerTasklist(final Context ctx, final HttpCallbackDecode callback) {
        try {
            TaskListRequestBean requestBean = new TaskListRequestBean();
            requestBean.setTask_type(1);
            requestBean.setApp_id(BaseAppUtil.getChannelID(ctx));
            requestBean.setApi_v(1);

            String args = new Gson().toJson(requestBean);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(SdkApi.getTaskList() + "?data=" + args)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    /**
     * 查询新手任务列表
     */
    public static void getDailyTasklist(final Context ctx, final HttpCallbackDecode callback) {
        try {
            TaskListRequestBean requestBean = new TaskListRequestBean();
            requestBean.setTask_type(2);
            requestBean.setApp_id(BaseAppUtil.getChannelID(ctx));
            requestBean.setApi_v(1);

            String args = new Gson().toJson(requestBean);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(SdkApi.getTaskList() + "?data=" + args)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    /**
     * 查询用户新手任务列表
     */
    public static void getUserNewPlayerTasklist(final Context ctx, final HttpCallbackDecode callback) {
        try {
            TaskListRequestBean requestBean = new TaskListRequestBean();
            requestBean.setTask_type(1);
            requestBean.setApp_id(BaseAppUtil.getChannelID(ctx));
            requestBean.setMobile(LoginManager.getUserId(ctx));
            requestBean.setApi_v(1);

            String args = new Gson().toJson(requestBean);
            String url = SdkApi.getUserTaskList() + "?data=" + args;

            Log.i(TAG, "getUserNewPlayerTasklist: " + url);

            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(url)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    /**
     * 查询用户新手任务列表
     */
    public static void getUserDailyTasklist(final Context ctx, final HttpCallbackDecode callback) {
        try {
            TaskListRequestBean requestBean = new TaskListRequestBean();
            requestBean.setTask_type(2);
            requestBean.setApp_id(BaseAppUtil.getChannelID(ctx));
            requestBean.setMobile(LoginManager.getUserId(ctx));
            requestBean.setApi_v(1);

            String args = new Gson().toJson(requestBean);
            String url = SdkApi.getUserTaskList() + "?data=" + args;

            Log.i(TAG, "getUserDailyTasklist: " + url);

            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(url)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    /**
     * 上报用户任务状态
     */
    public static void reportUserTasklist(final Context ctx, int task_id, long progress, final HttpCallbackDecode callback) {
        try {
            Log.i(TAG, "reportUserTasklist:  task_id=" + task_id + " ---- progress= " + progress);
            UserTaskStatusRequestBean requestBean = new UserTaskStatusRequestBean();
            requestBean.setChannel_task_id(task_id);
            requestBean.setProgress(progress);
            requestBean.setApp_id(BaseAppUtil.getChannelID(ctx));
            requestBean.setMobile(LoginManager.getUserId(ctx));
            requestBean.setApi_v(1);

            String args = new Gson().toJson(requestBean);
            Log.i(TAG, "reportUserTasklist: url= " + SdkApi.updateUserTask() + "?data=" + args);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(SdkApi.updateUserTask() + "?data=" + args)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }


    /**
     * 进群兑换码兑换
     */
    public static void exchange(final Context ctx, String code, final HttpCallbackDecode callback) {
        try {
            ExchangeRequestBean requestBean = new ExchangeRequestBean();
            requestBean.setCode(code);
            requestBean.setChannel_id(Integer.parseInt(BaseAppUtil.getChannelID(ctx)));
            requestBean.setMobile(LoginManager.getMobile(ctx));

            String args = new Gson().toJson(requestBean);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(SdkApi.exchange() + "?data=" + args)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    /**
     * 进群兑换码兑换
     */
    public static void getJoinWechatContent(final Context ctx, final HttpCallbackDecode callback) {
        try {
            ExchangeRequestBean requestBean = new ExchangeRequestBean();
            requestBean.setChannel_id(Integer.parseInt(BaseAppUtil.getChannelID(ctx)));

            String args = new Gson().toJson(requestBean);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(SdkApi.getJoinWeChatQrcode() + "?data=" + args)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    public static void getShakeResult(Context ctx, String gameId, final HttpCallbackDecode callback) {
        try {

            String url = SdkApi.getShakeReward() + "?channel_id=" + BaseAppUtil.getChannelID(ctx) + "&open_token=" + LetoConst.SDK_OPEN_TOKEN + "&game_id=" + gameId + "&mobile=" + LoginManager.getUserId(ctx);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(url)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }

    public static void deleteAccount(Context ctx, final HttpCallbackDecode callback) {
        try {

            String url = deleteAccount() + "?channel_id=" + BaseAppUtil.getChannelID(ctx) + "&open_token=" + LetoConst.SDK_OPEN_TOKEN + "&mobile=" + LoginManager.getUserId(ctx);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(url)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }


    public static void feedBack(Context ctx, String message, final HttpCallbackDecode callback) {
        try {

            String url = getFeedBack() + "?channel_id=" + BaseAppUtil.getChannelID(ctx) + "&content=" + message + "&open_token=" + LetoConst.SDK_OPEN_TOKEN + "&mobile=" + LoginManager.getUserId(ctx);
            (new RxVolley.Builder())
                    .setTag(ctx)
                    .shouldCache(false)
                    .url(url)
                    .callback(callback)
                    .doTask();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(LetoError.UNKNOW_EXCEPTION, e.getLocalizedMessage());
            }
        }
    }
}
