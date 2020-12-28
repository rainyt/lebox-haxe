package com.ipk.xd.mgcbox.wxapi;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.ipk.xd.mgcbox.MainActivity;
//import com.mgc.leto.game.base.MgcAccountManager;
//import com.tencent.mm.opensdk.constants.ConstantsAPI;
//import com.tencent.mm.opensdk.modelbase.BaseReq;
//import com.tencent.mm.opensdk.modelbase.BaseResp;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.UnsupportedEncodingException;
//import java.lang.ref.WeakReference;
//
//import zygame.core.KengSDK;
//import zygame.data.UserData;
//import zygame.handler.ApiHandler;
//import zygame.listeners.PostListener;
//import zygame.utils.Utils;
//import zygame.utils.ZLog;
//
//public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
//    private static String TAG = "MicroMsg.WXEntryActivity";
//
//    private IWXAPI api;
//    private MyHandler handler;
//
//    @Override
//    public void onReq(BaseReq req) {
//        ZLog.warring("resp.onReq = " + req.openId);
//
//        switch (req.getType()) {
//            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
////				goToGetMsg();
//                break;
//            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
////				goToShowMsg((ShowMessageFromWX.Req) req);
//                break;
//            default:
//                break;
//        }
//        ZLog.warring("释放WxEntryActivtiy2");
//        finish();
//    }
//
//    @Override
//    public void onResp(BaseResp resp) {
//        int result = resp.errCode;
//
//        ZLog.warring("resp.errCode = " + resp.errCode + "  当前SHA1签名：" + Utils.getSHA1());
//
//
//        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//            if(result == 0) {
//
//                SendAuth.Resp authResp = (SendAuth.Resp) resp;
//                final String code = authResp.code;
//                ApiHandler.post("https://api.weixin.qq.com/sns/oauth2/access_token?" +
//                        "appid=" + MainActivity.APP_ID + "&secret=" + MainActivity.APP_SECRET + "&code=" + code + "&grant_type=authorization_code", null, new PostListener() {
//                    @Override
//                    public void onSuccess(JSONObject jsonObject) {
//                        final UserData data = new UserData();
//                        String accessToken = null;
//                        try {
//                            data.openid = jsonObject.getString("openid");
//                            accessToken = jsonObject.getString("access_token");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        ApiHandler.post("https://api.weixin.qq.com/sns/userinfo?" +
//                                "access_token=" + accessToken + "&openid=" + data.openid, null, new PostListener() {
//                            @Override
//                            public void onSuccess(JSONObject jsonObject) {
//                                try {
//                                    String nickName = jsonObject.getString("nickname");
//									data.nickName = new String(nickName.getBytes("ISO-8859-1"), "UTF-8");
////                                    data.nickName = nickName;
//                                    data.avatar = jsonObject.getString("headimgurl");
//                                    int sex = jsonObject.getInt("sex");
//                                    switch (sex) {
//                                        case 0:
//                                            data.sex = "2";
//                                            break;
//                                        case 1:
//                                            data.sex = "1";
//                                            break;
//                                        default:
//                                            data.sex = "0";
//                                            break;
//                                    }
//                                    //同步到梦工厂
//                                    MgcAccountManager.syncAccount(Utils.getContext(),
//                                            data.openid,
//                                            "",
//                                            data.nickName,
//                                            data.avatar,
//                                            sex,
//                                            true,null
//                                    );
//                                } catch (JSONException | UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                }
//                                UserData.init(data);
//                                KengSDK.getInstance().getPlugin().call("loginEvent",data.openid);
//                                ZLog.warring("json=" + jsonObject.toString());
//                            }
//
//                            @Override
//                            public void onError(String s) {
//
//                            }
//                        });
//                        ZLog.warring("json=" + jsonObject.toString());
//                    }
//
//                    @Override
//                    public void onError(String s) {
//                        ZLog.warring("json=" + s);
//                    }
//                });
//            }
//            else{
//                ZLog.warring("登录失败");
//                UserData.init(null);
//            }
//        }
//        ZLog.warring("释放WxEntryActivtiy");
//        finish();
//    }
//
//    private static class MyHandler extends Handler {
//        private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;
//
//        public MyHandler(WXEntryActivity wxEntryActivity){
//            wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            int tag = msg.what;
//            switch (tag) {
//                case 1: {
//                    Bundle data = msg.getData();
//                    JSONObject json = null;
//                    try {
//                        json = new JSONObject(data.getString("result"));
//                        String openId, accessToken, refreshToken, scope;
//                        openId = json.getString("openid");
//                        accessToken = json.getString("access_token");
//                        refreshToken = json.getString("refresh_token");
//                        scope = json.getString("scope");
//                        Intent intent = new Intent(wxEntryActivityWeakReference.get(), MainActivity.class);
//                        intent.putExtra("openId", openId);
//                        intent.putExtra("accessToken", accessToken);
//                        intent.putExtra("refreshToken", refreshToken);
//                        intent.putExtra("scope", scope);
//                        wxEntryActivityWeakReference.get().startActivity(intent);
//                        ZLog.warring("openid:" + openId);
//                    } catch (JSONException e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        api = WXAPIFactory.createWXAPI(this, MainActivity.APP_ID, false);
//        handler = new MyHandler(this);
//
//        ZLog.warring("WxEntryActivity create!");
//        try {
//            Intent intent = getIntent();
//            api.handleIntent(intent, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        api.handleIntent(data, this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }
//}

import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {


}
