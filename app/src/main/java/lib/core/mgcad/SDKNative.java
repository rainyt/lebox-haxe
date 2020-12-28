package lib.core.mgcad;

import org.json.JSONException;
import org.json.JSONObject;

import com.mgc.leto.game.base.LetoAdApi;

import android.content.Intent;
import zygame.config.AdParmasConfig;
import zygame.core.KengSDK;
import zygame.interfaces.ActivityLifeCycle;
import zygame.listeners.AdListener;
import zygame.modules.BannerRefresh;
import zygame.modules.NativeGameAd;
import zygame.utils.Utils;
import zygame.utils.ZLog;

public class SDKNative extends NativeGameAd implements ActivityLifeCycle {

	private Boolean isShowed, isLoaded;
	private AdListener mAdListener;
	private JSONObject feedParams;

	private LetoAdApi _api;
	private LetoAdApi.FeedAd _feedAd;

	@Override
	public void onInit(AdListener arg0) {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂原生广告初始化开始");

		isShowed = false;
		isLoaded = false;
		mAdListener = arg0;

		_api = new LetoAdApi(Utils.getContext());

		feedParams = new JSONObject();
		try {
			feedParams.put("gravity", "bottom");
			feedParams.put("margin", "33");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		onLoad();
	}

	public int intervalRefresh = 60;
	public long closeTime = 0;

	/**
	 * 是否允许重新请求原生广告
	 * 
	 * @return
	 */
	public Boolean isRefresh() {
		long now = System.currentTimeMillis() / 1000;
		Boolean bool = (now - closeTime) >= intervalRefresh;
		if (bool)
			closeTime = now;

		return bool;
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂原生广告主动加载");

		if (!isRefresh()) {
			ZLog.log("由于信息流销毁重新请求广告比较耗时，现在设定" + intervalRefresh + "s后再重新请求广告");
			return;
		}

		isLoaded = false;
		_feedAd = null;

		if (_feedAd == null) {
			_feedAd = _api.createFeedAd(null);
		}
		_feedAd.onError(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				String errorMsg = res.optString("errMsg");
				ZLog.log("梦工厂信息流广告--加载失败，输出失败原因：" + errorMsg);
				mAdListener.onError(404, errorMsg);
			}
		});

		_feedAd.onShow(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂信息流广告--展示");
				mAdListener.onShow();
			}
		});

		_feedAd.onClose(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂信息流广告--关闭");
				mAdListener.onClose();
			}
		});

		_feedAd.onClick(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂信息流广告--点击");
				mAdListener.onClick();
			}
		});

		_feedAd.onLoad(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂信息流广告--加载");
				isLoaded = true;
			}
		});

		_feedAd.load();
		mAdListener.onDataResuest();
	}

	@Override
	public Boolean isLoaded() {
		// TODO Auto-generated method stub
		return _feedAd != null && isLoaded;
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂原生广告主动关闭");
		if (_feedAd != null) {
			_feedAd.hide();
			if (isShowed) {
				isShowed = false;
				onLoad();
			}
		}

	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂原生广告主动展示");
		if (isLoaded() && !isShowed) {
			isShowed = true;
			_feedAd.show(feedParams);
		}
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (_feedAd != null) {
			_feedAd.destroy();
			_feedAd = null;
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
