package lib.core.mgcad;

import org.json.JSONObject;

import com.mgc.leto.game.base.LetoAdApi;

import android.widget.RelativeLayout;
import zygame.listeners.AdListener;
import zygame.modules.StartAd;
import zygame.utils.Utils;
import zygame.utils.ViewGroup;
import zygame.utils.ZLog;

public class SDKStart extends StartAd {

	private RelativeLayout mContainer;

	private LetoAdApi _api;
	private LetoAdApi.SplashAd _splashAd;

	@Override
	public void onInit(final AdListener mAdListener) {
		// TODO Auto-generated method stub
		ZLog.log("梦工厂开屏开始初始化");

		_splashAd = null;
		mContainer = ViewGroup.getContainer(Utils.getContext(), "");

		_api = new LetoAdApi(Utils.getContext());
		_splashAd = _api.createSplashAd(mContainer);

		_splashAd.onError(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				String errorMsg = res.optString("errMsg");
				ZLog.log("梦工厂开屏广告--加载失败，输出失败原因：" + errorMsg);
				mAdListener.onError(404, errorMsg);
			}
		});

		_splashAd.onShow(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂开屏广告--展示");
				mAdListener.onShow();
			}
		});

		_splashAd.onClose(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂开屏广告--关闭");
				mAdListener.onClose();

				if (_splashAd != null) {
					_splashAd.destroy();
				}
			}
		});

		_splashAd.onClick(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂开屏广告--点击");
				mAdListener.onClick();
			}
		});

		_splashAd.onLoad(new LetoAdApi.ILetoAdApiCallback() {
			@Override
			public void onApiEvent(JSONObject res) {
				ZLog.log("梦工厂开屏广告--加载");
			}
		});

		_splashAd.load();
		mAdListener.onDataResuest();

		ZLog.log("梦工厂开屏广告开始展示");
		_splashAd.show();

	}

}
