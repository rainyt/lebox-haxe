package com.mgc.letobox.happy.me.bean;

import android.support.annotation.Keep;

import com.ledong.lib.minigame.bean.GameCenterData_Signin;
import com.mgc.leto.game.base.mgc.bean.GetUserCoinResultBean;

import java.util.List;

/**
 * Create by zhaozhihui on 2019-08-19
 **/
@Keep
public class SigninStatusResultBean {


    public List<GameCenterData_Signin> getSignlist() {
        return signlist;
    }

    public void setSignlist(List<GameCenterData_Signin> signlist) {
        this.signlist = signlist;
    }

    public GetUserCoinResultBean getCoins() {
        return coinslist;
    }

    public void setCoins(GetUserCoinResultBean coins) {
        this.coinslist = coins;
    }

    GetUserCoinResultBean coinslist;

    List<GameCenterData_Signin> signlist;
}
