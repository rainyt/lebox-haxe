package com.mgc.letobox.happy.circle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.mgc.leto.game.base.login.LoginManager;
import com.mgc.leto.game.base.utils.BaseAppUtil;
import com.mgc.leto.game.base.utils.DensityUtil;
import com.mgc.leto.game.base.utils.GlideUtil;
import com.mgc.letobox.happy.R;
import com.mgc.letobox.happy.circle.bean.CircleTieZiListResponse;
import com.mgc.letobox.happy.find.FindConst;
import com.mgc.letobox.happy.find.ui.KOLActivitiy;

/**
 * Created by DELL on 2018/8/11.
 */

public class ThreePicItemProvider extends BaseItemProvider<CircleTieZiListResponse, BaseViewHolder> {

    private CircleDetailsClickListener listener;

    public ThreePicItemProvider(CircleDetailsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int viewType() {
        return CircleListAdapter.THREE_PICS_NEWS;
    }

    @Override
    public int layout() {
        return R.layout.item_weibo_three_pics;
    }

    @Override
    public void convert(final BaseViewHolder helper, final CircleTieZiListResponse data, final int position) {
        if (null != data.getPics() && data.getPics().size() > 0) {
            int dp10 = DensityUtil.dip2px(helper.getView(R.id.ll_weibo).getContext(), 10);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) helper.getView(R.id.ll_weibo).getLayoutParams();
            layoutParams.setMargins(dp10, 0, dp10, 0);
            helper.getView(R.id.ll_weibo).setLayoutParams(layoutParams);

            View viewLine = helper.getView(R.id.viewLine);
            viewLine.setVisibility(View.VISIBLE);
            Context ctx = viewLine.getContext();

            int screen_width = BaseAppUtil.getDeviceWidth(helper.getView(R.id.ll_weibo).getContext());
            int image_width = (screen_width - 2 * DensityUtil.dip2px(helper.getView(R.id.ll_weibo).getContext(), 10)) / 3;
            int image_height = (screen_width - 2 * DensityUtil.dip2px(helper.getView(R.id.ll_weibo).getContext(), 10)) / 4;

            ImageView imgView1 = ((ImageView) helper.getView(R.id.iv_img1));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgView1.getLayoutParams();
            params.height = image_height;
            params.width = image_width;
            imgView1.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) imgView1.getLayoutParams();
            params2.height = image_height;
            params2.width = image_width;
            params2.leftMargin = DensityUtil.dip2px(ctx, 5);
            params2.rightMargin = DensityUtil.dip2px(ctx, 5);
            (helper.getView(R.id.iv_img2)).setLayoutParams(params2);

            (helper.getView(R.id.iv_img3)).setLayoutParams(params);

            if (data.getPics().size() > 0) {
                imgView1.setVisibility(View.VISIBLE);
                GlideUtil.loadRoundedCorner(ctx,
                    data.getPics().get(0).getUrl() + String.format(FindConst.image_url_with_size, 640, 420),
                    imgView1,
                    4,
                    R.mipmap.default_image_2);
            }
            if (data.getPics().size() > 1) {
                (helper.getView(R.id.iv_img2)).setVisibility(View.VISIBLE);
                GlideUtil.loadRoundedCorner(ctx,
                    data.getPics().get(1).getUrl() + String.format(FindConst.image_url_with_size, 640, 420),
                    (ImageView) helper.getView(R.id.iv_img2),
                    4,
                    R.mipmap.default_image_2);
            }
            if (data.getPics().size() > 2) {
                (helper.getView(R.id.iv_img3)).setVisibility(View.VISIBLE);
                GlideUtil.loadRoundedCorner(ctx,
                    data.getPics().get(2).getUrl() + String.format(FindConst.image_url_with_size, 640, 420),
                    (ImageView) helper.getView(R.id.iv_img3),
                    4,
                    R.mipmap.default_image_2);
            }

            ((TextView) helper.getView(R.id.tv_title)).setText(data.getTitle());
            Glide.with(helper.getView(R.id.iv_avatar).getContext()).load(data.getKol().getCover_pic()).into((ImageView) helper.getView(R.id.iv_avatar));
            Glide.with(helper.getView(R.id.iv_grade).getContext()).load(data.getKol().getLevel_pic()).into((ImageView) helper.getView(R.id.iv_grade));
            ((TextView) helper.getView(R.id.tv_name)).setText(data.getKol().getNickname());
            ((TextView) helper.getView(R.id.tv_time)).setText(data.getDate());
            ((TextView) helper.getView(R.id.tv_comment_num)).setText(data.getComment());

            if (data.getKol().getIsfollow() == 0) { // 未关注
                ((CheckBox) helper.getView(R.id.cb_follow)).setChecked(false);
            } else { // 已关注
                ((CheckBox) helper.getView(R.id.cb_follow)).setChecked(true);
            }

            if (data.getPost_id() == Integer.valueOf(LoginManager.getMemId(ctx))) {
                ((ImageView) helper.getView(R.id.imageView_click)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) helper.getView(R.id.imageView_click)).setVisibility(View.GONE);
            }

            if (LoginManager.getMemId(ctx) != null) {
                // 判断如果是用户自己就不显示关注按钮
                if (data.getKol().getId() == Integer.valueOf(LoginManager.getMemId(ctx))) {
                    ((CheckBox) helper.getView(R.id.cb_follow)).setVisibility(View.GONE);
                } else {
                    ((CheckBox) helper.getView(R.id.cb_follow)).setVisibility(View.VISIBLE);
                }
            } else {
                ((CheckBox) helper.getView(R.id.cb_follow)).setVisibility(View.GONE);
            }

            ((ImageView) helper.getView(R.id.iv_avatar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KOLActivitiy.startActivity(helper.getView(R.id.iv_avatar).getContext(), data.getKol().getId());
                }
            });

            ((CheckBox) helper.getView(R.id.cb_follow)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCheckBox(((CheckBox) helper.getView(R.id.cb_follow)).isChecked(), ((CheckBox) helper.getView(R.id.cb_follow)), data.getKol().getId(), position);
                    }
                }
            });

            if (data.getIs_edit() == 0 && data.getIs_delete() == 0) {
                ((ImageView) helper.getView(R.id.imageView_click)).setVisibility(View.GONE);
            } else {
                ((ImageView) helper.getView(R.id.imageView_click)).setVisibility(View.VISIBLE);
            }

            ((ImageView) helper.getView(R.id.imageView_click)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onShowCircle(((ImageView) helper.getView(R.id.imageView_click)), position, data.getIs_edit(), data.getIs_delete(), data);
                    }
                }
            });

            ((LinearLayout) helper.getView(R.id.ll_weibo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(position, data);
                    }
                }
            });
        }
    }
}
