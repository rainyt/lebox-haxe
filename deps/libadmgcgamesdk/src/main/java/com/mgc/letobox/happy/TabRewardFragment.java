package com.mgc.letobox.happy;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mgc.leto.game.base.api.ApiContainer;
import com.mgc.leto.game.base.api.adext.FeedAd;
import com.mgc.leto.game.base.config.AppConfig;
import com.mgc.leto.game.base.db.LoginControl;
import com.mgc.leto.game.base.event.DataRefreshEvent;
import com.mgc.leto.game.base.event.GetCoinEvent;
import com.mgc.leto.game.base.mgc.AppChannel;
import com.mgc.leto.game.base.trace.LetoTrace;
import com.mgc.leto.game.base.utils.BaseAppUtil;
import com.mgc.leto.game.base.utils.GameUtil;
import com.mgc.leto.game.base.utils.IntentConstant;
import com.mgc.leto.game.base.utils.MResource;
import com.mgc.letobox.happy.event.DailyTaskRefreshEvent;
import com.mgc.letobox.happy.event.NewerTaskRefreshEvent;
import com.mgc.letobox.happy.me.IRewardAdRequest;
import com.mgc.letobox.happy.me.IRewardAdResult;
import com.mgc.letobox.happy.me.adapter.MeHomeAdapter;
import com.mgc.letobox.happy.me.bean.MeFeedAdModuleBean;
import com.mgc.letobox.happy.me.bean.MeModuleBean;
import com.mgc.letobox.happy.util.LeBoxConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TabRewardFragment extends Fragment implements ApiContainer.IApiResultListener {
    // views
    private RecyclerView _recyclerView;
    private SwipeRefreshLayout _refreshLayout;
    View _rootView;

    ViewGroup _adContainer;

    MeHomeAdapter _meHomeAdapter;

    // tracking login info version
    private int _loginInfoVersion;

    // feed广告
    private ApiContainer _apiContainer;
    private FeedAd _feedAd;
    private boolean _feedAdUsed;


    private IRewardAdRequest _rewardAdRequest;

    IRewardAdResult _rewardAdResult;

    @Keep
    public static TabRewardFragment newInstance() {
        TabRewardFragment fragment = new TabRewardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get login info version
        _loginInfoVersion = getLoginInfoVersion();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // load view
        Context ctx = getActivity();
        _rootView = inflater.inflate(R.layout.leto_mgc_me_new_fragment, container, false);
        _refreshLayout = _rootView.findViewById(MResource.getIdByName(getActivity(), "R.id.refreshLayout"));
        _recyclerView = _rootView.findViewById(MResource.getIdByName(ctx, "R.id.recyclerView"));
        _adContainer = _rootView.findViewById(MResource.getIdByName(ctx, "R.id.ad_container"));

        _refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (_meHomeAdapter != null) {
//                    // clear task list so that it will be reloaded
//                    NewerTaskManager.clearTask();

                    // reload
                    _meHomeAdapter.notifyDataSetChanged();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        _refreshLayout.setRefreshing(false);
                    }
                });

            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        _rewardAdRequest = new IRewardAdRequest() {
            @Override
            public void requestRewardAd(Context context, IRewardAdResult result) {
                _rewardAdResult = result;
                _apiContainer.showVideo(new ApiContainer.IApiResultListener() {
                    @Override
                    public void onApiSuccess(ApiContainer.ApiName n, Object data) {
                        if (_rewardAdResult != null) {
                            _rewardAdResult.onSuccess();
                        }
                    }

                    @Override
                    public void onApiFailed(ApiContainer.ApiName n, Object data, boolean aborted) {
                        if (_rewardAdResult != null) {
                            _rewardAdResult.onFail("-1", "加载视频广告失败");
                        }
                    }
                });
            }
        };


        _meHomeAdapter = new MeHomeAdapter(getActivity(), _rewardAdRequest);
        _meHomeAdapter.setAdContainer(_adContainer);
        _meHomeAdapter.setFragment(this);

        initModules();

        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _recyclerView.setAdapter(_meHomeAdapter);

        // return
        return _rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // create api container
        Context ctx = getContext();
        if (_apiContainer == null) {
            _apiContainer = new ApiContainer(ctx, null, null);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        // destroy extended ad
        if (_feedAd != null && _apiContainer != null) {
            _apiContainer.destroyFeedAd(this, _feedAd.getAdId());
            _feedAd.destroy();
            _feedAd = null;
        }

        // destroy container
        if (_apiContainer != null) {
            _apiContainer.destroy();
            _apiContainer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // update if login info changed
        if (isLoginInfoUpdated(_loginInfoVersion)) {
            // clear task list so that it will be reloaded
            NewerTaskManager.clearTask();

            // now reload list
            _meHomeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        // update
        if (!hidden && isLoginInfoUpdated(_loginInfoVersion)) {
            _meHomeAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void refreshCoin(GetCoinEvent coinEvent) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                _meHomeAdapter.notifyDataSetChanged();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void refreshGame(DataRefreshEvent event) {
        LetoTrace.d("refresh DataRefreshEvent");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                _meHomeAdapter.notifyDataSetChanged();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void refreshDailyTask(DailyTaskRefreshEvent event) {
        LetoTrace.d("refresh daily tasks");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                _meHomeAdapter.notifyDataSetChanged();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void refreshGame(NewerTaskRefreshEvent event) {
        LetoTrace.d("refresh newer tasks");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                _meHomeAdapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isLoginInfoUpdated(int ver) {
        return getLoginInfoVersion() > ver;
    }

    private int getLoginInfoVersion() {
        return GameUtil.loadInt(getContext(), LoginControl.FILE_LOGIN_INFO_VERSION);
    }

    private void initModules() {
        List<MeModuleBean> moduleBeanList = new ArrayList<>();
        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_REWARD_COIN));

        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_OPEN_REDPACKET));

        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_REWARD));

        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_SIGININ));

//        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_REWARD_BUTTON));
//        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_REWARD_CHAT));

//        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_REWARD_GAME));



        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_NEWER_TASK));
        moduleBeanList.add(new MeModuleBean(LeBoxConstant.LETO_ME_MODULE_DAILY_TASK));
        _meHomeAdapter.setModels(moduleBeanList);

    }

    @Override
    public void onApiSuccess(ApiContainer.ApiName n, Object data) {
        if (n == ApiContainer.ApiName.LOAD_FEED_AD) {
            // get extended ad
            int adId = (Integer) data;
            _feedAd = _apiContainer.getFeedAd(adId);

            // insert feed ad module bean and reload
            if (_feedAd != null) {
                _feedAdUsed = true;
                List<MeModuleBean> models = _meHomeAdapter.getModels();
                MeFeedAdModuleBean bean = new MeFeedAdModuleBean(LeBoxConstant.LETO_ME_FEED_AD);
                bean.setFeedAd(_feedAd);
                models.add(2, bean);
                _meHomeAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onApiFailed(ApiContainer.ApiName n, Object data, boolean aborted) {

    }

    public IRewardAdRequest getAdRequestListener() {
        return _rewardAdRequest;
    }
}
