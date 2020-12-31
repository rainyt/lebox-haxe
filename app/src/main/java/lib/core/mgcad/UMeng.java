package lib.core.mgcad;

import com.mgc.leto.game.base.http.HttpCallbackDecode;
import com.mgc.leto.game.base.mgc.WithdrawActivity;
import com.mgc.leto.game.base.mgc.bean.AddCoinResultBean;
import com.mgc.leto.game.base.mgc.bean.GetUserCoinResultBean;
import com.mgc.leto.game.base.mgc.util.MGCApiUtil;
import com.mgc.letobox.happy.LeBoxLoginActivity;
import com.umeng.analytics.MobclickAgent;

import zygame.listeners.CommonCallListener;
import zygame.modules.Plugin;
import zygame.utils.SharedObject;
import zygame.utils.Utils;
import zygame.utils.ZLog;

public class UMeng extends Plugin {

	@Override
	public void onInit(CommonCallListener arg0) {
		// TODO Auto-generated method stub
		this.synchronizeCoin();
	}

	/**
	 * 事件统计
	 * @param eventid
	 */
	public void umengEvent(String eventid) {
		MobclickAgent.onEvent(Utils.getContext(),eventid);
	}

	/**
	 * 添加梦工厂盒子的金币
	 * @param coin
	 */
	public void addMgcBoxCoin(String coin) {
		int addcoin =  Integer.parseInt(coin);
		SharedObject.updateKey("mgc_coin",SharedObject.getInt("mgc_coin") + addcoin);
		MGCApiUtil.addCoin(Utils.getContext(), Utils.getMetaDataKey("MGC_APPID"),addcoin, "", 50, new HttpCallbackDecode<AddCoinResultBean>(Utils.getContext(), null) {
			@Override
			public void onDataSuccess(AddCoinResultBean data) {
				if(data != null)
					SharedObject.updateKey("mgc_coin",data.getTotal_coins());
			}

			@Override
			public void onFailure(String code, String msg) {
				super.onFailure(code, msg);
			}
		});
	}

	/**
	 * 打开第三方提现
	 */
	public void openWithdraw(){
		WithdrawActivity.start(Utils.getContext());
	}

	/**
	 * 打开第三方登录
	 */
	public void openWithLogin() {
//		WxMainActivity.login();
		LeBoxLoginActivity.start(Utils.getContext());
	}

	/**
	 * 同步盒子的金币数据，同步后，请使用getShareData('mgc_coin')接口获取
	 */
	public void synchronizeCoin(){
		MGCApiUtil.getUserCoin(Utils.getContext(), new HttpCallbackDecode<GetUserCoinResultBean>(Utils.getContext(), null) {
			@Override
			public void onDataSuccess(GetUserCoinResultBean data) {
				if (data != null) {
					ZLog.log("同步梦工厂金币数据："+data.getCoins());
					SharedObject.updateKey("mgc_coin",data.getCoins());
				} else {
				}
			}

			@Override
			public void onFailure(String code, String msg) {
				super.onFailure(code, msg);
			}
		});
//		MGCApiUtil.getDrawCashNumber(Utils.getContext(), new ILetoDrawCashNumberListener(){
//
//			@Override
//			public void onSucces(DrawCashNumberResultBean drawCashNumber) {
//				ZLog.log("get draw cash number: " + drawCashNumber.cash_num);
//			}
//
//			@Override
//			public void onFail(String code, String message) {
//				ZLog.log("onFailure:  " + message);
//			}
//		});
	}

}
