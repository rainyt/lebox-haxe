package lib.core.mgcad;

import org.json.JSONObject;

import com.mgc.leto.game.base.LetoAdApi;

import android.content.Intent;

import zygame.interfaces.ActivityLifeCycle;
import zygame.listeners.AdListener;
import zygame.listeners.AdStatisticsListener;
import zygame.modules.InterstitialAd;
import zygame.utils.Utils;
import zygame.utils.ZLog;

public class SDKInter extends InterstitialAd implements ActivityLifeCycle {

	private AdStatisticsListener mAdListener;
	private long time = 0;

	private LetoAdApi _api;
	private LetoAdApi.FullVideo videoAd;

	@Override
	public void onInit(AdListener arg0) {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂全屏视频广告初始化开始");
		mAdListener = (AdStatisticsListener) arg0;
		videoAd = null;

		_api = new LetoAdApi(Utils.getContext());

		onLoad();
	}

	public void onVideoLoad(final LetoAdApi.FullVideo video) {

		video.onLoad(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂全屏视频广告--加载", res.toString());
			}
		});

		video.onClose(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				videoAd.destroy();
				ZLog.log("梦工厂全屏视频广告--用户关闭视频");
				mAdListener.onClose();
				videoAd = null;
				onLoad();
			}
		});

		video.onError(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				String errorMsg = res.optString("errMsg");
				ZLog.log("梦工厂全屏视频广告--失败，输出失败原因：" + errorMsg);
				if (videoAd != null)
					videoAd.destroy();
				videoAd = null;
//				onLoad();
				mAdListener.onError(404, errorMsg);
			}
		});

		video.onClick(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂全屏视频广告--点击");
				mAdListener.onClick();
			}
		});

		video.onShow(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂全屏视频广告--展示");
				mAdListener.onShow();
				onVideoLoad(_api.createFullVideoAd());
			}
		});

//		video.onClose(new LetoAdApi.ILetoAdApiCallback() {
//			@Override
//			public void onApiEvent(JSONObject jsonObject) {
//				mAdListener.onClose();
//			}
//		});
		video.load();
		mAdListener.onDataResuest();
	}


	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂全屏视频广告主动加载");
		if (videoAd == null) {
			videoAd = _api.createFullVideoAd();
			onVideoLoad(videoAd);
		}
	}

	@Override
	public Boolean isLoaded() {
		// TODO Auto-generated method stub
		return videoAd != null && videoAd.isLoaded();
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂全屏视频广告主动展示");
		if (isLoaded()) {
			long now = System.currentTimeMillis();
			ZLog.log("观看间隔："+(now - time));
			if (now - time > 1000) {
				ZLog.log("梦工厂全屏视频真实调起");
				videoAd.show();
			}else{
				ZLog.log("观看视频太过频繁");
			}
			time = now;
		} else {
			if (videoAd != null)
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
			videoAd = null;
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
