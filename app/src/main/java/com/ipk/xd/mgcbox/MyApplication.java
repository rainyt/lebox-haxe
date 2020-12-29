package com.ipk.xd.mgcbox;

import com.ledong.lib.leto.Leto;
import com.mgc.leto.game.base.trace.LetoTrace;
import com.mgc.letobox.happy.LetoApplication;

/**
 * Create by zhaozhihui on 2020/12/15
 **/
public class MyApplication extends LetoApplication {
    @Override
    public void onCreate() {
        // init leto
        LetoTrace.setDebugMode(true);
        // init leto
        Leto.init(this);
        super.onCreate();
    }
}
