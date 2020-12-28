package com.ipk.xd.mgcbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kymjs.rxvolley.RxVolley;
import com.ledong.lib.leto.Leto;
import com.ledong.lib.minigame.bean.TabBean;
import com.leto.game.base.dialog.PrivacyWebDialog;
import com.leto.game.base.easypermissions.EasyPermissions;
import com.leto.reward.LetoRewardEvents;
import com.leto.reward.LetoRewardManager;
import com.leto.reward.listener.ILetoAnswerCallBack;
import com.leto.reward.listener.ILetoIdiomCallBack;
import com.leto.reward.listener.ILetoOpenRedPacketCallBack;
import com.leto.reward.listener.ILetoScratchCardCallBack;
import com.leto.reward.listener.ILetoTurntableCallBack;
import com.leto.reward.model.IdiomResultGame;
import com.leto.reward.model.QaGameRewardBean;
import com.leto.reward.model.TurnTableRewardBean;
import com.mgc.leto.game.base.LetoEvents;
import com.mgc.leto.game.base.LetoScene;
import com.mgc.leto.game.base.MgcAccountManager;
import com.mgc.leto.game.base.api.ApiContainer;
import com.mgc.leto.game.base.be.AdManager;
import com.mgc.leto.game.base.bean.LoginResultBean;
import com.mgc.leto.game.base.http.HttpCallbackDecode;
import com.mgc.leto.game.base.http.HttpParamsBuild;
import com.mgc.leto.game.base.http.RxVolleyManager;
import com.mgc.leto.game.base.interfaces.ILetoContainerProvider;
import com.mgc.leto.game.base.interfaces.ILetoGameContainer;
import com.mgc.leto.game.base.listener.ILetoLoginResultCallback;
import com.mgc.leto.game.base.listener.ILetoPlayedDurationListener;
import com.mgc.leto.game.base.listener.ILoginCallBack;
import com.mgc.leto.game.base.listener.SyncUserInfoListener;
import com.mgc.leto.game.base.login.LoginManager;
import com.mgc.leto.game.base.mgc.bean.CoinDialogScene;
import com.mgc.leto.game.base.mgc.bean.GetBenefitsSettingResultBean;
import com.mgc.leto.game.base.mgc.dialog.IMGCCoinDialogListener;
import com.mgc.leto.game.base.mgc.dialog.MGCInfoDialog;
import com.mgc.leto.game.base.mgc.model.MGCSharedModel;
import com.mgc.leto.game.base.mgc.util.MGCApiUtil;
import com.mgc.leto.game.base.mgc.util.MGCDialogUtil;
import com.mgc.leto.game.base.statistic.GameStatisticManager;
import com.mgc.leto.game.base.statistic.StatisticEvent;
import com.mgc.leto.game.base.trace.LetoTrace;
import com.mgc.leto.game.base.utils.BaseAppUtil;
import com.mgc.leto.game.base.utils.GlideUtil;
import com.mgc.leto.game.base.utils.IntentConstant;
import com.mgc.leto.game.base.utils.PermissionsUtil;
import com.mgc.letobox.happy.GameCenterTabActivity;
import com.mgc.letobox.happy.LeBoxLoginActivity;
import com.mgc.letobox.happy.NewerTaskManager;
import com.mgc.letobox.happy.TabCategoryFragment;
import com.mgc.letobox.happy.TabFindFragment;
import com.mgc.letobox.happy.TabGameFragment;
import com.mgc.letobox.happy.TabGameRankFragment;
import com.mgc.letobox.happy.TabMeFragment;
import com.mgc.letobox.happy.TabMiniGameFragment;
import com.mgc.letobox.happy.TabRewardFragment;
import com.mgc.letobox.happy.bean.VersionRequestBean;
import com.mgc.letobox.happy.bean.VersionResultBean;
import com.mgc.letobox.happy.dialog.VersionDialog;
import com.mgc.letobox.happy.event.DailyTaskRefreshEvent;
import com.mgc.letobox.happy.event.ShowRookieGuideEvent;
import com.mgc.letobox.happy.event.TabSwitchEvent;
import com.mgc.letobox.happy.me.bean.TaskResultBean;
import com.mgc.letobox.happy.me.bean.UserTaskStatusResultBean;
import com.mgc.letobox.happy.task.LeBoxTaskManager;
import com.mgc.letobox.happy.util.LeBoxConstant;
import com.mgc.letobox.happy.util.LeBoxUtil;
import com.mgc.letobox.happy.view.MyRadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zygame.utils.Utils;

public class MainActivity extends org.haxe.lime.GameActivity implements MyRadioGroup.OnCheckedChangeListener, ILetoContainerProvider {

    private static final String TAG = "GameCenterActivity";

    private static final int REQUEST_CODE_WRITE_PERMISSION = 2003;

    RadioButton tabGameCenterBtn;
    RadioButton tabRankBtn;
    RadioButton tabChallengeBtn;
    RadioButton tabCategoryBtn;
    RadioButton tabMeBtn;
    RadioButton tabFindBtn;
    RadioButton tabRewardBtn;
    RadioButton tabMiniGameBtn;
    MyRadioGroup tabGroup;

    Fragment curFragment;

    // fragments
    private Map<Integer, Fragment> _fragments;
    private Map<Integer, Class> _fragmentClasses;

    String orientation = "portrait";
    String srcAppId;
    String srcAppPath;

    MGCInfoDialog alertDialog;
    int mTabIndex;
    int mRIndex;

    // tab id array
    private List<Integer> _tabIds;
    private List<RadioButton> _tabBtns;

    // censor mode
    private boolean _censorMode = false;


    VersionDialog mVersionDialog;

    private static final String BUNDLE_FRAGMENTS_KEY = "android:support:fragments";


    ILetoPlayedDurationListener playedDurationListener;

    ILetoAnswerCallBack _anserCallBack;
    ILetoIdiomCallBack _idiomCallBack;
    ILetoOpenRedPacketCallBack _openRedPacketCallBack;
    ILetoScratchCardCallBack _scratchCardCallBack;
    ILetoTurntableCallBack _turntableCallBack;

    LeBoxTaskManager _taskManager;  //声明变量

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Utils.init(this);

        super.onCreate(savedInstanceState);

        TabGameFragment.surfaceView = mSurface;

        // init
        _fragments = new HashMap<>();
        _fragmentClasses = new HashMap<>();
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_find, TabFindFragment.class);
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_game, TabMiniGameFragment.class);
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_minigame, TabGameFragment.class);
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_rank, TabGameRankFragment.class);
//        _fragmentClasses.put(R.id.tab_challenge, TabChallengeFragment.class);
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_category, TabCategoryFragment.class);
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_me, TabMeFragment.class);
        _fragmentClasses.put(com.mgc.letobox.happy.R.id.tab_reward, TabRewardFragment.class);

        // init leto
        Leto.init(this);

        // set content view
        setContentView(com.mgc.letobox.happy.R.layout.activity_tab_gamecenter);

        // get extra
        Bundle extra = getIntent().getExtras();
        //报空，给注释掉了
//        _censorMode = extra.getBoolean("censorMode", true);

        orientation = getIntent().getStringExtra(IntentConstant.ACTION_APP_ORIENTATION);
        srcAppId = getIntent().getStringExtra(IntentConstant.SRC_APP_ID);
        srcAppPath = getIntent().getStringExtra(IntentConstant.SRC_APP_PATH);

        tabGameCenterBtn = findViewById(com.mgc.letobox.happy.R.id.tab_game);
        tabRankBtn = findViewById(com.mgc.letobox.happy.R.id.tab_rank);
        tabChallengeBtn = findViewById(com.mgc.letobox.happy.R.id.tab_challenge);
        tabCategoryBtn = findViewById(com.mgc.letobox.happy.R.id.tab_category);
        tabMeBtn = findViewById(com.mgc.letobox.happy.R.id.tab_me);
        tabFindBtn = findViewById(com.mgc.letobox.happy.R.id.tab_find);
        tabGroup = findViewById(com.mgc.letobox.happy.R.id.tab_group);
        tabRewardBtn = findViewById(com.mgc.letobox.happy.R.id.tab_reward);
        _tabBtns = Arrays.asList(
                tabGameCenterBtn,
                tabRankBtn,
                tabChallengeBtn,
                tabCategoryBtn,
                tabMeBtn,
                tabFindBtn,
                tabRewardBtn
        );

        _tabIds = Arrays.asList(
                com.mgc.letobox.happy.R.id.tab_minigame,
                com.mgc.letobox.happy.R.id.tab_game,
//                    R.id.tab_rank,
//                    R.id.tab_challenge,
                com.mgc.letobox.happy.R.id.tab_reward,
//                    R.id.tab_category,
                com.mgc.letobox.happy.R.id.tab_me
        );

        for (RadioButton btn : _tabBtns) {
            if (_tabIds.indexOf(btn.getId()) == -1) {
                btn.setVisibility(View.GONE);
            }
        }

        // detect system version, if too low, give a hint
        checkSystemVersion();

        //init rxVolley
        RxVolleyManager.init(MainActivity.this);

        tabGroup.setOnCheckedChangeListener(this);
        tabGroup.check(_tabIds.get(0));

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        playedDurationListener = new ILetoPlayedDurationListener() {
            @Override
            public void getPlayedDurations(String gameId, long duration) {

                Log.i(TAG, "gameId: " + gameId + "-------------duration: " + duration);
                //如果是语聊，则更新到本地
                if (!TextUtils.isEmpty(gameId) && LetoRewardManager.isChatGame(gameId)) {
                    LetoRewardManager.updateChatGameProgress(MainActivity.this, duration);
                }

                reportTaskProgress(duration);
            }
        };

        LetoEvents.addLetoPlayedDurationListener(playedDurationListener);

        NewerTaskManager.getNewPlayerTaskList(this, null);
        NewerTaskManager.getDailyTaskList(this, null);

        AdManager.getInstance().getTmTaskList(this);


        getVersion();

        getPrivacy_content();


        //预加载广告
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AdManager.preloadAd(getApplication());
            }
        }, 2000);

        initRewardListener();


//        this.hideBottomUIMenu();
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.BLACK);

        this.syncAccount();

        _taskManager = new LeBoxTaskManager(MainActivity.this);

        setCustomLogin();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        // 隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        // android p 适配处理
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = this.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            this.getWindow().setAttributes(lp);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);  //不保存Bundle数据
        if (outState != null) {//存在Bundle数据,去除fragments的状态保存，解决Fragme错乱问题。
            outState.remove(BUNDLE_FRAGMENTS_KEY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // deliver to api container
        ApiContainer.handleActivityResult(requestCode, resultCode, data);

        if (requestCode == LeBoxConstant.REQUEST_CODE_TASK_INVITE_CODE) {
            LetoTrace.d("invite code call back: resultCode =" + resultCode);
            if (resultCode == 1) {
                reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_BIND_INVITE, 1);
            }

        } else if (requestCode == LeBoxConstant.REQUEST_CODE_TASK_PHONE_LOGIN) {
            LetoTrace.d("phone login call back: resultCode =" + resultCode);
            if (resultCode == 1) {
                reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_BIND_PHONE, 1);
            }
        }

        if (_taskManager != null) {
            _taskManager.onActivityResult(requestCode, resultCode, data);
        }

        // for this
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void checkSystemVersion() {
        if (!BaseAppUtil.supportSystem()) {

            alertDialog = new MGCInfoDialog(MainActivity.this, "温馨提示", "手机版本过低，建议升级系统");
            alertDialog.setRightButton("确定并退出", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    finish();
                }
            });

            alertDialog.show();
        }
    }

    @Keep
    public static void start(Context context, String orientation, String appId, String appPath) {
        if (null != context) {
            Intent intent = new Intent(context, GameCenterTabActivity.class);
            intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
            intent.putExtra(IntentConstant.SRC_APP_ID, appId);
            intent.putExtra(IntentConstant.SRC_APP_PATH, appPath);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // check imei
        PermissionsUtil.delayCheckPermissionIfNeeded(this);

        // check rookie guide
        showRookieGuideIfNeeded();

        // get benefit settings
        if (!MGCSharedModel.isBenefitSettingsInited()) {
            doGetBenefitSettings();
        }

        onGameResume();
    }

    public void syncAccount() {
        if (!LoginManager.isSignedIn(this)) {
            MgcAccountManager.syncAccount(this, "", "", false, new SyncUserInfoListener() {
                @Override
                public void onSuccess(LoginResultBean data) {

                }

                @Override
                public void onFail(String code, String message) {

                }
            });
        }
    }

    private void doGetBenefitSettings() {
        MGCApiUtil.getBenefitSettings(this, new HttpCallbackDecode<GetBenefitsSettingResultBean>(this, null) {
            @Override
            public void onDataSuccess(GetBenefitsSettingResultBean data) {
            }

            @Override
            public void onFailure(String code, String msg) {
                super.onFailure(code, msg);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        // 取消权限检查, 有些机型他会导致app回到前台
        PermissionsUtil.cancelDelayCheckPermission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        GlideUtil.clearMemory(this);

        try {
            RxVolley.getRequestQueue().cancelAll(MainActivity.this);
            RxVolley.getRequestQueue().getCache().clear();
        } catch (Throwable e) {

        }

        // unregister event bus
        EventBus.getDefault().unregister(this);

        if (mVersionDialog != null && mVersionDialog.isShowing()) {
            mVersionDialog.dismiss();
        }
        mVersionDialog = null;

        if (_taskManager != null) {
            _taskManager.destroy();
        }

        LetoEvents.removeLetoPlayedDurationListener(playedDurationListener);
    }

    private void setCustomLogin(){
        //如果盒子是设置的微信登陆， 需要注册第三方登陆回调
        if (BaseAppUtil.getMetaBooleanValue(this, "MGC_ENABLE_WECHAT_LOGIN")) {
            LetoEvents.setCustomLogin(this, new ILoginCallBack() {
                @Override
                public void show(Context context) {
                    LetoTrace.d(TAG, "show wechat login ");
                    LeBoxLoginActivity.start(context);
                }
            });
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchTab(TabSwitchEvent event) {
        if (event != null) {
            if (event.tabindex >= 0) {
                int tabIndex = event.tabindex;
                if (tabIndex >= 0 && tabIndex < _tabIds.size()) {
                    tabGroup.check(_tabIds.get(tabIndex));
                }
            } else {
                int idx = _tabIds.indexOf(event.tabid);
                if (idx != -1) {
                    tabGroup.check(_tabIds.get(idx));
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(MyRadioGroup group, int i) {

        // lazy create fragment
        Fragment fragment = _fragments.get(i);
        if (fragment == null) {
            try {
                Class klass = _fragmentClasses.get(i);
                Method m = klass.getDeclaredMethod("newInstance");
                fragment = (Fragment) m.invoke(klass);
            } catch (Throwable e) {
            }
            if (fragment != null) {
                _fragments.put(i, fragment);
            }
        }

        // set tab index
        mTabIndex = _tabIds.indexOf(i);
        mRIndex = i;

        MGCSharedModel.OPEN_TYPE_BOX_TAB = mTabIndex + 1;

        reportTabClick(mTabIndex + 1);

        // add fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.add(com.mgc.letobox.happy.R.id.container, fragment);
        }
        if (curFragment == fragment) {
            return;
        }

        // switch fragment
        if (null != curFragment) {
            //fragmentTransaction.hide(curFragment).show(fragment).replace(R.id.container, fragment).commit();
            fragmentTransaction.hide(curFragment).show(fragment).commitAllowingStateLoss();
        } else {
            fragmentTransaction.commitAllowingStateLoss();
        }
        getSupportFragmentManager().executePendingTransactions();

        this.onGameResume();

        curFragment = fragment;
    }

    public void onGameResume() {
        if(mRIndex == R.id.tab_minigame){
            this.resumeNativeThread();
        }
        else{
            this.pauseNativeThread();
        }
    }

    @Override
    public ILetoGameContainer getLetoContainer() {
        TabGameFragment gameFragment = (TabGameFragment) _fragments.get(R.id.tab_minigame);
        return gameFragment.getLetoContainer();
    }


    List<Fragment> mFragmentList = new ArrayList<>();
    List<TabBean> mTabBeans = new ArrayList<>();

    public void reportTaskProgress(long progress) {
        boolean hasNewTask = false;
        boolean hasDailyTask = false;
        List<TaskResultBean> taskResultBeans = NewerTaskManager.getCompleteNewerTaskId(this, "", progress);
        if (taskResultBeans != null && taskResultBeans.size() > 0) {
            hasNewTask = true;
        }

        List<TaskResultBean> dailyTaskResultBeans = NewerTaskManager.getCompleteDailyTaskIdOnPlayGame(this, "", progress);

        if (dailyTaskResultBeans != null && dailyTaskResultBeans.size() > 0) {
            taskResultBeans.addAll(dailyTaskResultBeans);
            hasDailyTask = true;
        }
        if (taskResultBeans != null && taskResultBeans.size() > 0) {
            showTaskDialog(taskResultBeans, 0, 0);
        }

        EventBus.getDefault().post(new DailyTaskRefreshEvent());
    }

    public void reportDailyTaskProgressByTaskType(int taskType, long progress) {

        List<TaskResultBean> taskResultBeans = NewerTaskManager.getCompleteDailyTaskId(this, taskType, "", progress);
        if (taskResultBeans != null && taskResultBeans.size() > 0) {
            showTaskDialog(taskResultBeans, 0, 0);
        }

        EventBus.getDefault().post(new DailyTaskRefreshEvent());
    }

    Handler mTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            List<TaskResultBean> taskBeans = (List<TaskResultBean>) msg.obj;
            int pos = msg.arg1;
            int action = msg.arg2;

            showTaskDialog(taskBeans, pos, action);

        }

    };


    private void showTaskDialog(final List<TaskResultBean> taskBeans, final int pos, final int action) {
        if (this.isFinishing()) {
            return;
        }

        MGCDialogUtil.showMGCCoinDialogWithOrderId(this, null, taskBeans.get(pos).getAward_coins(), 1, CoinDialogScene.ROOKIE_TASK,
                taskBeans.get(pos).getChannel_task_id(), new IMGCCoinDialogListener() {
                    @Override
                    public void onExit(boolean video, int coinGot) {
                        if (taskBeans.size() > pos + 1) {
                            Message msg = new Message();
                            msg.obj = taskBeans;
                            msg.arg1 = pos + 1;
                            msg.arg2 = action;
                            mTaskHandler.sendMessage(msg);
                        }
                    }
                });
    }

    boolean isCheckedVersion = false;

    private void getVersion() {
        isCheckedVersion = true;
        VersionRequestBean versionRequestBean = new VersionRequestBean();
        versionRequestBean.setType(1);
        versionRequestBean.setVersion(String.valueOf(BaseAppUtil.getAppVersionCode(MainActivity.this)));
        try {
            versionRequestBean.setChannel_id(Integer.parseInt(BaseAppUtil.getChannelID(MainActivity.this)));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(new Gson().toJson(versionRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<VersionResultBean>(this, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(VersionResultBean data) {
                if (data != null) {
                    try {
                        int curCode = BaseAppUtil.getAppVersionCode(MainActivity.this);
                        int latestCode = Integer.parseInt(data.getVersion());
                        if (latestCode > curCode) {
                            if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                showVersionDialog(data);
                            } else {
                                EasyPermissions.requestPermissions(MainActivity.this, "需要存储权限更新版本",
                                        REQUEST_CODE_WRITE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String code, String msg) {
                Log.d(TAG, "获取版本信息失败: " + msg);
            }
        };
        RxVolley.post(LeBoxUtil.getLatestVersion(), httpParamsBuild.getHttpParams(), httpCallbackDecode);

    }


    private void showVersionDialog(final VersionResultBean version) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mVersionDialog != null) {
                        mVersionDialog.dismiss();
                        mVersionDialog = null;
                    }
                    mVersionDialog = new VersionDialog();

                    //强制更新
                    mVersionDialog.showDialog(MainActivity.this, version, new VersionDialog.ConfirmDialogListener() {
                        @Override
                        public void ok() {


                        }

                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void dismiss() {

                        }
                    });
                } catch (Throwable e) {

                }
            }
        });
    }

    public String getAppVersionName() {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            versionName = pi.versionName;
//			versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public void getPrivacy_content() {
        PrivacyWebDialog.show(this);
    }

    private void showRookieGuideIfNeeded() {
        if (MGCSharedModel.isRookieGiftAvailable()) {
            String dstGameId = BaseAppUtil.getMetaStringValue(this, "MGC_GAMEID");
            if (!TextUtils.isEmpty(dstGameId)) {
                EventBus.getDefault().postSticky(new ShowRookieGuideEvent());
            }
        }
    }

    public void initRewardListener() {

        _anserCallBack = new ILetoAnswerCallBack() {
            @Override
            public void onAnswer(Context ctx, QaGameRewardBean request) {
                LetoTrace.d("dati callback");
                if (request.getIs_correct() == 1) {
                    reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_REWARD_ANSWER, 1);
                }
            }
        };
        _idiomCallBack = new ILetoIdiomCallBack() {
            @Override
            public void onAnswer(Context ctx, IdiomResultGame request) {
                LetoTrace.d("idiom callback");
                if (request.getIs_correct() == 1) {
                    reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_REWARD_IDIOM, 1);
                }
            }
        };
        _scratchCardCallBack = new ILetoScratchCardCallBack() {
            @Override
            public void onScratch(Context ctx, TurnTableRewardBean request) {
                LetoTrace.d("scratch callback");

                reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_REWARD_SCRATCH_CARD, 1);

            }
        };
        _turntableCallBack = new ILetoTurntableCallBack() {
            @Override
            public void onTurntable(Context ctx, TurnTableRewardBean request) {
                LetoTrace.d("turntable callback");
                reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_REWARD_TURNTABLE, 1);
            }
        };
        LetoRewardEvents.setAnserCallBack(_anserCallBack);
        LetoRewardEvents.setScratchCardCallBack(_scratchCardCallBack);
        LetoRewardEvents.setTurntableCallBack(_turntableCallBack);
        LetoRewardEvents.setIdiomCallBack(_idiomCallBack);

        LetoEvents.setLetoLoginResultCallback(new ILetoLoginResultCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onLoginSuccess(LoginResultBean data) {
                LetoTrace.d(TAG, "setLetoLoginResultCallback onLoginSuccess...");
                try {
                    if (data != null && !LoginManager.isTempAccount(data.getMobile())) {
                        LetoTrace.d(TAG, "finish bind phone task...");
                        NewerTaskManager.getUserNewPlayerTaskStatus(MainActivity.this, new HttpCallbackDecode<List<UserTaskStatusResultBean>>(MainActivity.this, null, new TypeToken<List<UserTaskStatusResultBean>>() {
                        }.getType()) {
                            @Override
                            public void onDataSuccess(final List<UserTaskStatusResultBean> data) {
                                reportDailyTaskProgressByTaskType(LeBoxConstant.LETO_TASK_TYP_BIND_PHONE, 1);
                            }

                            @Override
                            public void onFailure(String code, String msg) {
                                super.onFailure(code, msg);
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();

                            }
                        });
                    }
                } catch (Throwable e) {

                }
            }
        });
    }


    private void reportTabClick(int position) {
        GameStatisticManager.statisticGameLog(this, BaseAppUtil.getChannelID(this), StatisticEvent.LETO_BOX_TAB_CLICK.ordinal(), 0, LetoScene.DEFAULT.ordinal(), "" + System.currentTimeMillis(), 0, 0, "", 0, "", "", false, 0, 0, 0, 0, position, 0, 0, "", null);
    }

}

