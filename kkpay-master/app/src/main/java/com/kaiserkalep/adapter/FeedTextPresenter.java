package com.kaiserkalep.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.bean.FeedDetailBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.glide.GlideUtil;

public class FeedTextPresenter extends BaseChatViewHolder {

    private Context tContext;
    private TextView tvTime, tvMsgMessage;
    private ImageView ivLeft, ivRight;
    private final String userAvatar;

    public FeedTextPresenter(Context context, View view) {
        super(context, view);
        this.tContext = context;
        userAvatar = SPUtil.getUserAvatar();
        tvTime = view.findViewById(R.id.tv_time);
        tvMsgMessage = view.findViewById(R.id.tv_message);
        ivLeft = view.findViewById(R.id.iv_left);
        ivRight = view.findViewById(R.id.iv_right);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void setMessage(FeedDetailBean data) {
        super.setMessage(data);
        if (null != data) {
            String initType = data.getInitType();
            if (Config.ONE_STRING.equals(initType)) {
                GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(userAvatar), ivLeft, R.drawable.icon_default_photo);
                GlideUtil.loadCircleImage("", ivRight, R.drawable.icon_transparent_bg);
                tvMsgMessage.setGravity(Gravity.START | Gravity.LEFT);
                ivLeft.setVisibility(View.VISIBLE);
                ivRight.setVisibility(View.INVISIBLE);
            } else {
                GlideUtil.loadCircleImage("", ivLeft, R.drawable.icon_transparent_bg);
                GlideUtil.loadCircleImage("", ivRight, R.drawable.icon_kkpay_logo);
                tvMsgMessage.setGravity(Gravity.END | Gravity.RIGHT);
                ivLeft.setVisibility(View.INVISIBLE);
                ivRight.setVisibility(View.VISIBLE);
            }

            tvTime.setText(data.getCreateTime());
            tvMsgMessage.setText(data.getContent());
        }
    }
}
