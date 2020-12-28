package com.mgc.letobox.happy.me.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mgc.leto.game.base.bean.GameExtendInfo;
import com.mgc.letobox.happy.me.IRewardAdRequest;
import com.mgc.letobox.happy.me.bean.MeModuleBean;
import com.mgc.letobox.happy.me.holder.CoinHolder;
import com.mgc.letobox.happy.me.holder.CommonViewHolder;
import com.mgc.letobox.happy.me.holder.DailyTaskHolder;
import com.mgc.letobox.happy.me.holder.FeedAdHolder;
import com.mgc.letobox.happy.me.holder.GamesHolder;
import com.mgc.letobox.happy.me.holder.HighCoinHolder;
import com.mgc.letobox.happy.me.holder.MeSigninHolder;
import com.mgc.letobox.happy.me.holder.NewerTaskHolder;
import com.mgc.letobox.happy.me.holder.OpenRedpacketHolder;
import com.mgc.letobox.happy.me.holder.OtherHolder;
import com.mgc.letobox.happy.me.holder.RewardButtonHolder;
import com.mgc.letobox.happy.me.holder.RewardChatHolder;
import com.mgc.letobox.happy.me.holder.RewardGameHolder;
import com.mgc.letobox.happy.me.holder.RewardGridHolder;
import com.mgc.letobox.happy.me.holder.UserCoinHolder;
import com.mgc.letobox.happy.util.LeBoxConstant;

import java.util.ArrayList;
import java.util.List;

public class MeHomeAdapter extends RecyclerView.Adapter<CommonViewHolder> {
    private List<MeModuleBean> _models;
    private List<Integer> _itemTypes;
    private Context mContext;


    private String mOrientation;
    private String mSrcAppId;
    private String mSrcAppPath;

    private int gc_id;
    private String gc_source;


    ViewGroup _adContainer;

    GameExtendInfo gameExtendInfo;

    Fragment _fragment;

    IRewardAdRequest _rewardAdRequest;

    public MeHomeAdapter(Context ctx, IRewardAdRequest rewardAdRequest) {
        mContext = ctx;
        _models = new ArrayList<>();

        _rewardAdRequest = rewardAdRequest;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _models == null ? 0 : _models.size();
    }

    @Override
    public int getItemViewType(int position) {
        return _models.get(position).getType();
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case LeBoxConstant.LETO_ME_MODULE_COIN:
                return CoinHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_SIGININ:
                return MeSigninHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_MYGAMES:
                return GamesHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_FEED_AD:
                return FeedAdHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_NEWER_TASK:
                return NewerTaskHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_DAILY_TASK:
                return DailyTaskHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_HIGH_COIN_TASK:
                return HighCoinHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_REWARD:
                return RewardGameHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_REWARD_GAME:
                return RewardGridHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_OPEN_REDPACKET:
                return OpenRedpacketHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_REWARD_COIN:
                return UserCoinHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_REWARD_BUTTON:
                return RewardButtonHolder.create(mContext, parent);
            case LeBoxConstant.LETO_ME_MODULE_REWARD_CHAT:
                return RewardChatHolder.create(mContext, parent);
            default:
                return OtherHolder.create(mContext, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int pos) {
        CommonViewHolder vh = (CommonViewHolder) holder;
        //在bind之前配置
        vh.setAdContainer(_adContainer);
        vh.setRewardAdRequest(_rewardAdRequest);

        vh.onBind(_models.get(pos), pos);
    }

    public List<MeModuleBean> getModels() {
        return _models;
    }

    public void setModels(List<MeModuleBean> moduleList) {

        if (moduleList == null || moduleList.size() == 0) {
            return;
        }

        _models.clear();

        _models.addAll(moduleList);

    }

    public void setAdContainer(ViewGroup adContainer) {
        _adContainer = adContainer;
    }

    public void setFragment(Fragment fragment) {
        _fragment = fragment;
    }


    public void setRewardAdRequest(IRewardAdRequest adRequest){
        _rewardAdRequest = adRequest;
    }
}
