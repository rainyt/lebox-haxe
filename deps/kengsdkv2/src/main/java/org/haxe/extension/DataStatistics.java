package org.haxe.extension;

import android.util.Log;
import zygame.core.KengSDK;
import zygame.utils.Utils;

/**
 * 数据统计类
 * 
 * @author linjiancheng
 *
 */
public class DataStatistics {

	/**
	 * 数据统计方法（不需要调用此方法）
	 * 
	 * @param func
	 * @param ret
	 */
	private static Boolean isExist() {
		if (!Utils.foundClass("lib.core.libextalkingdatav2.Talkingdata")) {
			Log.e("KengSDK", "没有打入talkingdata插件，无法使用统计服务，请核实情况！");
			Log.e("KengSDK", "没有打入talkingdata插件，无法使用统计服务，请核实情况！");
			Log.e("KengSDK", "没有打入talkingdata插件，无法使用统计服务，请核实情况！");
			return false;
		} else {
			Log.i("KengSDK", "数据统计方法调用(dataSetting)");
			return true;
		}

	}

	/**
	 * 用户数据统计方法
	 * 
	 * @param uid
	 *            用户唯一标识
	 * @param level1
	 *            等级（默认传1）
	 * @param gameserver
	 *            区服（默认传“史小坑服”）
	 * @param level2
	 *            升级等级（默认传2）
	 * @param nickname
	 *            用户昵称
	 */
	public static void userData(String uid, String level1, String gameserver, String level2, String nickname) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("user", uid, level1, gameserver, level2, nickname);
	}

	/**
	 * 支付数据统计方法
	 * 
	 * @param orderid
	 *            订单号
	 * @param commodityname
	 *            道具名称
	 * @param mount
	 *            金额（单位为元）
	 * @param commoditnum
	 *            道具数量
	 */
	public static void payData(String orderid, String commodityname, String mount, String commoditnum) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("pay", orderid, commodityname, mount, commoditnum);
	}

	/**
	 * 支付成功数据统计方法
	 * 
	 * @param orderid
	 *            订单号
	 */
	public static void paysuccessData(String orderid) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("paysuccess", orderid);
	}

	/**
	 * 奖励道具数据统计方法
	 * 
	 * @param commoditnum
	 *            道具数量
	 * @param commodityname
	 *            道具名称
	 */
	public static void rewarditemsData(String commoditnum, String commodityname) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("rewarditems", commoditnum, commodityname);
	}

	/**
	 * 使用道具数据统计方法
	 * 
	 * @param event
	 *            使用场景（自定义，例如：购买提示、跳过关卡等）
	 * @param commoditcount
	 *            使用道具次数
	 * @param commoditnum
	 *            使用道具数量
	 */
	public static void useitemsData(String event, String commoditcount, String commoditnum) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("useitems", event, commoditcount, commoditnum);
	}

	/**
	 * 关卡开始数据统计方法
	 * 
	 * @param levelname
	 *            关卡名称（中文）
	 */
	public static void levelstartData(String levelname) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("levelstart", levelname);
	}

	/**
	 * 关卡胜利数据统计方法
	 * 
	 * @param levelname
	 *            关卡名称（中文）
	 */
	public static void levelwinData(String levelname) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("levelwin", levelname);
	}

	/**
	 * 关卡失败数据统计方法
	 * 
	 * @param levelname
	 *            关卡名称（中文）
	 * @param reason
	 *            失败原因
	 */
	public static void levelfailedData(String levelname, String reason) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("levelfailed", levelname, reason);
	}

	/**
	 * 自定义统计事件
	 * 
	 * @param eventname
	 *            自定义事件名称
	 * @param value
	 *            自定义事件内容
	 */
	public static void customeventData(String eventname, String value) {
		if (!isExist()) {
			return;
		}
		KengSDK.getInstance().getPlugin().call("customevent", eventname, value);
	}
}
