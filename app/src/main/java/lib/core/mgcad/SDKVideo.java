package lib.core.mgcad;

import org.json.JSONException;
import org.json.JSONObject;

import com.mgc.leto.game.base.LetoAdApi;

import android.content.Intent;
import android.os.Handler;

import java.util.Date;

import zygame.core.KengSDK;
import zygame.interfaces.ActivityLifeCycle;
import zygame.listeners.AdStatisticsListener;
import zygame.listeners.VideoAdListener;
import zygame.modules.VideoAd;
import zygame.utils.Utils;
import zygame.utils.ZLog;

public class SDKVideo extends VideoAd implements ActivityLifeCycle {

	private AdStatisticsListener mAdListener;
	private long time = 0;

	private LetoAdApi _api;
	private LetoAdApi.RewardedVideo videoAd;
	private LetoAdApi.RewardedVideo videoAd2;

	@Override
	public void onInit(VideoAdListener arg0) {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂视频广告初始化开始");
		mAdListener = (AdStatisticsListener)arg0;
		videoAd = null;

		_api = new LetoAdApi(Utils.getContext());

		onLoad();
	}

	public void onVideoLoad(final LetoAdApi.RewardedVideo video){
		video.onLoad(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂视频广告--加载", res.toString());
				if(videoAd != video)
					videoAd2 = video;
			}
		});

		video.onClose(new LetoAdApi.ILetoAdApiCallback () {
			@Override
			public void onApiEvent(JSONObject res) {
				videoAd.destroy();
				if (res.optBoolean("isEnded")) {
					ZLog.log("梦工厂视频广告--用户看完了视频");
					mAdListener.onReward();
					if(videoAd2 != null && videoAd != videoAd2) {
						videoAd = videoAd2;
						videoAd2 = null;
						ZLog.log("梦工厂视频广告--秒同步新视频");
					}
				} else {
					ZLog.log("梦工厂视频广告--用户没有看完视频");
				}
			}
		});

		video.onError(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				String errorMsg = res.optString("errMsg");
				ZLog.log("梦工厂视频广告--失败，输出失败原因：" + errorMsg);
				if(videoAd != null)
					videoAd.destroy();
				videoAd = null;
//				onLoad();
				mAdListener.onError(404, errorMsg);
			}
		});

		video.onClick(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {

				try {
					ZLog.log("梦工厂视频广告--点击");
					JSONObject adInfo = res.getJSONObject("adInfo");
					KengSDK.getInstance().getPlugin().call("videoClickEvent", getPlatformName(adInfo),
							adInfo.getString("adPlaceId"));
					mAdListener.onClick();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		video.onShow(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				try {
					JSONObject adInfo = res.getJSONObject("adInfo");
					ZLog.log("梦工厂视频广告--展示", getPlatformName(adInfo));
					KengSDK.getInstance().getPlugin().call("videoShowEvent", getPlatformName(adInfo),
							adInfo.getString("adPlaceId"), "1");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mAdListener.onShow();
				onVideoLoad(_api.createRewardedVideoAd());
			}
		});
		video.load();
		mAdListener.onDataResuest();
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂视频广告主动加载");
		if (videoAd == null) {
			videoAd = _api.createRewardedVideoAd();
			onVideoLoad(videoAd);
		}
	}

	/**
	 * 获取梦工厂的广告平台，转为热云读取的渠道
	 * 穿山甲(csj)、优量汇(ylh)、百青藤(bqt)、快手(ks)、Sigmob(sigmob)、Mintegral(mintegral)、OneWay(oneway)、Vungle(vungle)
	 *
	 * @param adInfo
	 * @return
	 * @throws JSONException
	 */
	public String getPlatformName(JSONObject adInfo) throws JSONException {
		switch (adInfo.getString("adPlatform")) {
			case "gdt":
				// 广点通
				return "gdt";
			case "toutiao":
				return "csj";
			case "baidu":
				return "bqt";
			case "kssdk":
				return "ks";
			// 原值返回
			case "shandianhezisdk":
				return "vungle";
			case "limobidsp":
				return "fb";

		}
		return "null";
	}

	@Override
	public Boolean isLoaded() {
		// TODO Auto-generated method stub
		return videoAd != null && videoAd.isLoaded();
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂视频广告主动展示");
		if (isLoaded()) {
			long now = System.currentTimeMillis();
			ZLog.log("观看间隔："+(now - time));
			if (now - time > 1000) {
				time = now;
				ZLog.log("梦工厂视频真实调起");
				videoAd.show();
			}
			else{
				ZLog.log("观看视频太过频繁");
			}
		} else {
			if(videoAd != null)
				videoAd.destroy();
			videoAd = null;
			onLoad();
		}
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (videoAd != null) {
			videoAd.destroy();
		}
	}

	@Override
	public void onNewIntent(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestPermissionsResult(int arg0, String[] arg1, int[] arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRestart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
