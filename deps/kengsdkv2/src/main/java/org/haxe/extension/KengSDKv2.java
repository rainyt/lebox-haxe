package org.haxe.extension;

import org.haxe.lime.HaxeObject;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;

import java.io.File;

import zygame.activitys.AlertDialog;
import zygame.core.KengSDK;
import zygame.core.KengSDKEvents;
import zygame.data.UserData;
import zygame.listeners.AdListener;
import zygame.listeners.CommonCloseListener;
import zygame.listeners.DialogListener;
import zygame.listeners.EditTextListener;
import zygame.listeners.ExchangeListener;
import zygame.listeners.LoginDataListener;
import zygame.listeners.PayListener;
import zygame.listeners.VideoAdListener;
import zygame.utils.Utils;
import zygame.nativesdk.NativeADSDK;
import zygame.dialog.WebViewAlert;
import zygame.utils.ZLog;

/* 
	You can use the Android Extension class in order to hook
	into the Android activity lifecycle. This is not required
	for standard Java code, this is designed for when you need
	deeper integration.
	
	You can access additional references from the Extension class,
	depending on your needs:
	
	- Extension.assetManager (android.content.res.AssetManager)
	- Extension.callbackHandler (android.os.Handler)
	- Extension.mainActivity (android.app.Activity)
	- Extension.mainContext (android.content.Context)
	- Extension.mainView (android.view.View)
	
	You can also make references to static or instance methods
	and properties on Java classes. These classes can be included 
	as single files using <java path="to/File.java" /> within your
	project, or use the full Android Library Project format (such
	as this example) in order to include your own AndroidManifest
	data, additional dependencies, etc.
	
	These are also optional, though this example shows a static
	function for performing a single task, like returning a value
	back to Haxe from Java.
*/
public class KengSDKv2 extends Extension {

	private static HaxeObject _callBackObject;

	public static void onKengSDKPause()
	{
		Log.i("KengSDK","Haxe onPause");
	}

	public static void onKengSDKResume()
	{
		Log.i("KengSDK","Haxe onResume");
	}

	/**
	 * 初始化支付，否则无法受到支付回调
	 */
	public static void init(HaxeObject func)
	{
		Log.i("KengSDK","初始化KengSDKv2.init");
		_callBackObject = func;

		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().initCore();
				//初始化支付				
				KengSDKEvents.setPayListener(new PayListener() {
					
					@Override
					public void onPostPay(final Boolean isSuccess,final int id) {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Log.i("trace","支付完成："+id);
								if(_callBackObject != null) _callBackObject.call("onPayPost", new Object[]{isSuccess, id});
							}
						});
					}
					
					@Override
					public void onPostError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onPostQuery(final Boolean isSuccess,int arg0) {
						// TODO Auto-generated method stub
						
					}					

				});

				KengSDKEvents.setInterstitialAdListener(new AdListener() {
					
					@Override
					public void onShow() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_show"});
							}
						});
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_error"});
							}
						});
					}
					
					@Override
					public void onDataResuest() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onClose() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_close"});
							}
						});
					}
					
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						
					}
				});
				
				KengSDKEvents.setVideoAdListener(new VideoAdListener() {

					@Override
					public void onDataResuest() {

					}
					
					@Override
					public void onShow() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_video_start"});
							}
						});
					}
					
					@Override
					public void onReward() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_rewar"});
							}
						});
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_video_error"});
							}
						});
					}
					
					@Override
					public void onChannel() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onAdEvent", new Object[]{"ad_video_cannel"});
							}
						});
					}
				});

				//登录事件注册处理
				UserData.dataListener = new LoginDataListener() {
					
					@Override
					public void onLogin() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onLoginEvent", new Object[]{"logined",""});
							}
						});
					}
					
					@Override
					public void onError() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onLoginEvent", new Object[]{"loginerr",""});
							}
						});
					}
				};
			}
		});

		
	}


	/**
	 * 原生广告API接口
	 */

	/**
	 * 展示大横幅广告
	 */
	public static void showBigBannerAd() {
		Extension.mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				NativeADSDK.showBigBannerAd();
			}
		});
	}

	/**
	 * 展示大横幅广告
	 */
	public static void closeBigBannerAd() {
		Extension.mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				NativeADSDK.closeBigBannerAd();
			}
		});
	}

	/**
	 * 是否可以使用支付功能
	 */
	public static int isCanPay() {
		return KengSDK.getInstance().isCanPay() ? 1 : 0;
	}

	/**
	 * 确认大横幅广告是否可以展示
	 * 
	 * @return 0为不可展示 1为可展示
	 */
	public static int isCanShowBigBannerAd() {
		return NativeADSDK.isHasBigBannerAd() ? 1 : 0;
	}

	/**
	 * 获取游戏APP版本号
	 */
	public static int getAppVersion(){
		return KengSDK.getInstance().getAppVersion();
	}
	
	public static void showModelInputView(final String title,final String defaultText,final int textLength)
	{
		
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().showModelInputView(title, defaultText, textLength, new DialogListener() {
					
					@Override
					public void onSure(final AlertDialog arg0) {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onInputEvent", new Object[]{arg0.getEditMsg()});
							}
						});
					}
					
					@Override
					public void onChannel() {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
							}
						});
					}
				});
			}
		});
		
	}
	

	public static void showOrder(){
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
//				KengSDK.getInstance().sho
			}
		});
	}
	
	/**
	 * 展示视频广告 v2版本新增一个广告位的参数
	 */
	public static void showVideoAd(final String adPos){
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().showVideoAd(adPos);
			}
		});
	}

//	/**
//	 * 点击原生广告触发 v2版本已弃用
//	 */
//	public static void clickNativeAd()
//	{
//		AdTaskHandler.getInstance().clickNativeAd();
//	}

//	/**
//	 * 关闭原生广告触发 v2版本已弃用
//	 */
//	public static void closeNativeAd()
//	{
//		AdTaskHandler.getInstance().closeNativeAd();
//	}

	/**
	 * 调用扩展API
	 * @param func 扩展方法名
	 * @param strings 参数传递，多个参数请使用,分隔
	 */
	public static void callExtendsApi(String func,String strings){
		if("".equals(strings)){
			String[] args = {};
			KengSDK.getInstance().getPlugin().callArgs(func, args);
		}
		else
			KengSDK.getInstance().getPlugin().callArgs(func, strings.split(","));
	}



	/**
	 * 游戏胜利统计
	 */
	public static void gameWin(String levelName)
	{
		DataStatistics.levelwinData(levelName);
	}

	/**
	 * 游戏失败统计
	 */
	public static void gameOver(String levelName, String reason)
	{
		DataStatistics.levelfailedData(levelName,reason);
	}

	/**
	 * 游戏开始统计
	 */
	public static void gameStart(String levelName)
	{
		DataStatistics.levelstartData(levelName);
	}

	/**
	 * 触发自定义事件
	 */
	public static void onEvent(String event,String value)
	{
		DataStatistics.customeventData(event,value);
	}

	/**
	 * 获取在线参数，如果获取不到会返回error json
	 */
	public static String getOnlineString(String key)
	{
		return KengSDK.getInstance().getOnlineData(key, null);
	}

	/**
	 * 获取共享数据
	 */
	public static String getShareData(String key)
	{
		return Utils.getMetaDataKey(key);
	}

	/**
	 * 获取OPEN唯一ID，如果不存在，则无法获取
	 */
	public static String getOpenID()
	{
		if(UserData.userData != null)
			return UserData.userData.openid;
		return null;
	}

	/**
	 * 获取账号昵称，如果不存在，则无法获取
	 */
	public static String getNickName()
	{
		if(UserData.userData != null)
			return UserData.userData.nickName;
		return null;
	}

	/**
	 * 获取头像，如果不存在，则无法获取
	 */
	public static String getAvatar()
	{
		if(UserData.userData != null)
			return UserData.userData.avatar;
		return null;
	}

	/**
	 * 获取性别，如果不存在，则无法获取
	 */
	public static String getSex()
	{
		if(UserData.userData != null)
			return UserData.userData.sex;
		return "2";
	}

	/**
	 * 获取渠道标示
	 */
	public static String getChannel()
	{
		return Utils.getChannel();
	}

	/**
	 * 获取设备ID，当获取不到时，会以UUID代替
	 */
	public static String getDeviceId()
	{
		return Utils.getDeviceId();
	}

	/**
	 * 获取广告位是否启用
	 */
	public static int isOpen(String adPos)
	{
		Boolean bool = KengSDK.getInstance().isOpen(adPos);
		return bool?1:0;
	}

	/**
	 * 检查应用是否存在
	 */
	public static int cheakApp(String pkg)
	{
		return Utils.cheakApp(pkg)?1:0;
	}

	/**
	 * 启动对应包名的APP应用
	 */
	public static int startApp(String pkg)
	{
		return Utils.openApp(pkg)?1:0;
	}

	/**
	 * 下载对应的APP
	 */
	public static void downloadApp(String pkg,String url)
	{
		KengSDK.getInstance().getAppStore().downloadApp(pkg,url);
	}

	/**
	 * 更新APK包体
	 * @param url
	 */
	public static void updateApk(String url){
		if (DownloadImpl.getInstance().exist(url)) {
			return;
		}
		if (Utils.existApk(url)) {
			// 已存在本地，开始安装
			Utils.installApk(Utils.getApkFilePath(url));
			callHaxeObject("callApkDownloadState",new Object[] {0,100});
			return;
		}
		// 开始下载
		DownloadImpl.getInstance().with(url).enqueue(new DownloadListenerAdapter() {
			@Override
			public void onStart(String url, String userAgent, String contentDisposition, String mimetype,
								long contentLength, Extra extra) {
				super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
			}

			@Override
			public void onProgress(String url, long downloaded, long length, long usedTime) {
				super.onProgress(url, downloaded, length, usedTime);
				Long downloaded2 = downloaded;
				Long length2 = length;
				callHaxeObject("callApkDownloadState",new Object[] {0, downloaded2.doubleValue() / length2.doubleValue()});
				ZLog.log(" progress:" + downloaded + "/" + length + " url:" + url);
			}

			@Override
			public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
				ZLog.warring(
						"Downloaded: path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
				// 缓存APK路径
				Utils.cacheApk(url, path.getPath());
				Utils.installApk(path.getPath());
				Boolean bool = super.onResult(throwable, path, url, extra);
				return bool;
			}
		});
	}
	
	/**
	 * 打开键盘输入框，用于原生输入文本
	 */
	public static void showKeyboard(final String defaultText,final int textLength)
	{
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				KengSDK.getInstance().showInput(defaultText, textLength, new CommonCloseListener() {
					
					@Override
					public void onClose() {
						// TODO Auto-generated method stub
						callHaxeObject("onInputEvent",new Object[] {"close"});
					}
				}, new EditTextListener() {
					
					@Override
					public void getText(String arg0) {
						// TODO Auto-generated method stub
						callHaxeObject("onInputEvent",new Object[] {"change",arg0});
					}
				});
			}
		});
		
	}

	/**
	 * 展示史小坑隐私协议
	 */
	public static void showSxkPrivate(){
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				WebViewAlert.show("file:///android_asset/sxk_private.html", "【史小坑游戏】隐私协议");
			}
		});
	}

	/**
	 * 展示史小坑公开协议
	 */
	public static void showSxkPublic(){
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				WebViewAlert.show("file:///android_asset/sxk_public.html", "【史小坑游戏】用户协议");
			}
		});
	}

	/**
	 * 是否审核中
	 */
	public static int isReviewing()
	{
		Boolean bool = KengSDK.getInstance().isInReview();
		return bool?1:0;
	}

	/**
	 * isVideoReady 接口在v2已经废弃，请转使用isAvailable接口
	 */
	public static int isAvailable(String adPos)
	{
		Boolean bool = KengSDK.getInstance().isAvailable(adPos);
		return bool?1:0;
	}

	/**
	 * 获取是否全面屏，如果是全面屏
	 */
	public static int isNotch()
	{
		Boolean bool = KengSDK.getInstance().isNotch();
		return bool?1:0;
	}

	/**
	 * 是否支持V3Api访问
	 */
	public static int isV3Support() {
		Boolean bool = KengSDK.getInstance().isV3Support();
		return bool?1:0;
	}

	/**
	 * 该接口在v2版本不再使用，默认弹出兑换框
	 */
	public static void exchange(final String cdkey)
	{
		showExchangeView();
	}
		
	/**
	 * 调用Haxe事件处理
	 * @param event
	 * @param param
	 */
	private static void callHaxeObject(final String event,final Object[] param) {
		Extension.mainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(_callBackObject != null) _callBackObject.call(event, param);
			}
		});
	}

	/**
	 * 展示兑换码窗口
	 */
	private static void showExchangeView()
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().showExchangeCode(new ExchangeListener() {
					
					@Override
					public void onSuccess(final JSONObject arg0) {
						// TODO Auto-generated method stub
						Extension.mainActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onProducts", new Object[]{"success",arg0.toString()});
							}
						});
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("KengSDK", "兑换失败原因："+arg1);
						Extension.mainActivity.runOnUiThread(new Runnable() {
			
							@Override
							public void run() {
								if(_callBackObject != null) _callBackObject.call("onProducts", new Object[]{"error"});
							}
						});
					}
					
					@Override
					public void onChannel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
	}

	/**
	 * 退出接口
	 */
	public static void exit()
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().exit();
			}
		});
	}
	
	/**
	 * 清理广告数量
	 * @param adPos
	 */
	public static void clearAdCount(String adPos)
	{
		KengSDK.getInstance().clearAdIntervalCount(adPos);
	}

	/**
	 * 展示广告，该接口是万能的，可以展示插屏、横幅甚至是视频，根据广告位类型决定。
	 */
	public static void showAd(final String adPos,final int isNative)
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.i("KengSDK","尝试弹出插屏广告位："+adPos);
				KengSDK.getInstance().showAd(adPos);
			}
		});
	}

	/**
	 * 展示横幅（v2版本将会增加多一个横幅广告位的参数）
	 */
	public static void showBannerAd(final String bannerPos)
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().showBannerAd(bannerPos);
			}
		});
	}

	/**
	 * 关闭横幅
	 */
	public static void closeBannerAd()
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().closeBanner();
			}
		});
	}

	/**
	 * 设置横幅的显示位置
	 */
	public static void setBannerAdGravity(final int pGravity, final int pointy)
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
//				AdTaskHandler.getInstance().setBannerAdGravity(pGravity, pointy);
			}
		});
	}

	/**
	 * 登录账号
	 */
	public static void login()
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(!KengSDK.getInstance().login())
				{
					//登录失败
					if(_callBackObject != null) _callBackObject.call("onLoginEvent", new Object[]{"loginnotsupport",""});
				}
			}
		});
	}

	
	/**
	 * 更多游戏
	 */
	public static void moreGame()
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().openMoreGame();
			}
		});
	}
		
	/**
	 * 打开评论页面
	 * @param levelKey
	 * @param group
	 * @param token
	 */
	public static void openCommentView(final String levelKey,final String group,final String token)
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().openCommentView(levelKey, group, token, new CommonCloseListener() {
					
					@Override
					public void onClose() {
						// TODO Auto-generated method stub
						callHaxeObject("onCommontClose",new Object[] {});
					}
				} );
			}
		});
	}
	

	/**
	 * 弹出指定网页
	 */
	 public static void openUrl(final String url)
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Utils.openWebView(url, 0xffffffff);
			}
		});
	}

	/**
	 * 弹出公告
	 */
	public static void openAnnouncement()
	{
		((Activity) Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().openAnnouncement();
			}
		});
	}
	
	/**
	 * 支付
	 */
	public static void pay(final int code)
	{
		((Activity)Utils.getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				KengSDK.getInstance().pay(code);
			}
		});
	}

	/**
	 * Called when an activity you launched exits, giving you the requestCode 
	 * you started it with, the resultCode it returned, and any additional data 
	 * from it.
	 */
	public boolean onActivityResult (int requestCode, int resultCode, Intent data) {
		KengSDK.getInstance().onActivityResult(requestCode, resultCode, data);
		return true;
		
	}

	/**
	 * Called when the activity receives th results for permission requests.
	 */
	public boolean onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		
		KengSDK.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
		return true;

	}
	
	
	/**
	 * Called when the activity is starting.
	 */
	public void onCreate (Bundle savedInstanceState) {
		//初始化KengSDK
		Log.i("trace","HaxeKengSDK 初始化开始");
		KengSDK.getInstance().init(Extension.mainActivity,savedInstanceState);
	}
	
	
	/**
	 * Perform any final cleanup before an activity is destroyed.
	 */
	public void onDestroy () {
		KengSDK.getInstance().onDestroy();
	}
	
	
	/**
	 * Called as part of the activity lifecycle when an activity is going into
	 * the background, but has not (yet) been killed.
	 */
	public void onPause () {
		KengSDK.getInstance().onPause();
	}
	
	
	/**
	 * Called after {@link #onStop} when the current activity is being 
	 * re-displayed to the user (the user has navigated back to it).
	 */
	public void onRestart () {
		
		KengSDK.getInstance().onRestart();
		
	}
	
	
	/**
	 * Called after {@link #onRestart}, or {@link #onPause}, for your activity 
	 * to start interacting with the user.
	 */
	public void onResume () {
		KengSDK.getInstance().onResume();
	}
	
	
	/**
	 * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when  
	 * the activity had been stopped, but is now again being displayed to the 
	 * user.
	 */
	public void onStart () {
		
		KengSDK.getInstance().onStart();
		
	}
	
	
	/**
	 * Called when the activity is no longer visible to the user, because 
	 * another activity has been resumed and is covering this one. 
	 */
	public void onStop () {
		
		KengSDK.getInstance().onStop();
		
	}
	
	
}