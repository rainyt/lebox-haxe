package lib.core.mgcad;

import com.mgc.leto.game.base.LetoCore;
import com.mgc.leto.game.base.sdk.LetoAdSdk;
import com.mgc.leto.game.base.trace.LetoTrace;

import zygame.listeners.CommonCallListener;
import zygame.modules.ApplicationInit;
import zygame.utils.Utils;
import zygame.utils.ZLog;

public class SDKInit extends ApplicationInit {

	@Override
	public void onInit(CommonCallListener arg0) {
		// TODO Auto-generated method stub
		ZLog.warring("梦工厂广告application初始化开始，日志是否打开：" + "open".equals(Utils.getMetaDataKey("MGC_LOG")));

		// 竞价广告：true-开启；false-关闭
		LetoCore.useBiddingAdPolicy(true);

		// 梦工厂广告初始化
		LetoAdSdk.init(Utils.getContext().getApplicationContext());

		// 梦工厂日志：true-开启；false-关闭
		LetoTrace.setDebugMode("open".equals(Utils.getMetaDataKey("MGC_LOG")));
	}

}
