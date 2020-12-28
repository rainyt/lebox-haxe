package lib.core.mgcad;

import org.json.JSONObject;

import com.mgc.leto.game.base.LetoAdApi;

import android.content.Intent;
import zygame.interfaces.ActivityLifeCycle;
import zygame.listeners.AdListener;
import zygame.modules.BannerAd;
import zygame.utils.Utils;
import zygame.utils.ZLog;

public class SDKBanner extends BannerAd implements ActivityLifeCycle {

	private AdListener mAdListener;

	private LetoAdApi.BannerAd _bannerAd;
	private LetoAdApi _api;

	@Override
	public void onInit(AdListener arg0) {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂横幅广告初始化开始");

		mAdListener = arg0;
		_bannerAd = null;

		_api = new LetoAdApi(Utils.getContext());

		onLoad();
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂横幅广告主动加载");
		
		if (_bannerAd == null) {
			_bannerAd = _api.createBannerAd();
		}

		_bannerAd.onError(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				String errorMsg = res.optString("errMsg");
				ZLog.log("梦工厂横幅广告--失败，输出失败原因：" + errorMsg);
				mAdListener.onError(404, errorMsg);
			}
		});

		_bannerAd.onClick(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂横幅广告--点击");
				mAdListener.onClick();
			}
		});

		_bannerAd.onClose(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂横幅广告--关闭");
				mAdListener.onClose();
			}
		});

		_bannerAd.onShow(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂横幅广告--展示");
				mAdListener.onShow();
			}
		});

		_bannerAd.onLoad(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂横幅广告--加载");
			}
		});

		mAdListener.onDataResuest();
	}

	@Override
	public Boolean isLoaded() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂横幅广告主动关闭");
		if (_bannerAd != null) {
			_bannerAd.hide();
			_bannerAd.destroy();
			_bannerAd = null;
		}
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂横幅广告主动展示");
		if (_bannerAd == null) {
			_bannerAd = _api.createBannerAd();
		}
		_bannerAd.show();
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (_bannerAd != null) {
			_bannerAd.destroy();
			_bannerAd = null;
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
