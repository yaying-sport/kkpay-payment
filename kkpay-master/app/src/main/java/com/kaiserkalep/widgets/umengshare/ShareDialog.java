package com.kaiserkalep.widgets.umengshare;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaiserkalep.R;
import com.kaiserkalep.base.DialogCommBase;


/**
 *  分享弹框
 *
 * @Auther: Administrator
 * @Date: 2019/3/23 0023 13:54
 * @Description:
 */
public class ShareDialog extends DialogCommBase {

    private TextView share_wechat_view, share_friends_view, share_qq_view, custom_share_cancel_view, share_copy_view;

    protected static int defaultStyle = R.style.BottomDialog;

    public ShareDialog(Activity mContext) {
        super(mContext, defaultStyle);
    }

    @Override
    public void initView() {
        setContentView(R.layout.custom_share_layout);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        share_wechat_view = findViewById(R.id.custom_share_wechat_view);
        share_friends_view = findViewById(R.id.custom_share_friends_view);
        share_qq_view = findViewById(R.id.custom_share_qq_view);
        share_copy_view = findViewById(R.id.custom_share_copy_view);
        custom_share_cancel_view = findViewById(R.id.custom_share_cancel_view);
        setFriendVisible(View.GONE);
    }

    protected int getWindowManagerWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    public void setOnListener(View.OnClickListener onClickListener) {
        share_wechat_view.setOnClickListener(onClickListener);
        share_friends_view.setOnClickListener(onClickListener);
        share_qq_view.setOnClickListener(onClickListener);
        share_copy_view.setOnClickListener(onClickListener);
        custom_share_cancel_view.setOnClickListener(onClickListener);
    }

    public void setWChatVisible(int visible) {
        if (share_wechat_view != null) {
            share_wechat_view.setVisibility(visible);
        }
    }

    public void setQQVisible(int visible) {
        if (share_qq_view != null) {
            share_qq_view.setVisibility(visible);
        }
    }

    public void setCopyVisible(int visible) {
        if (share_copy_view != null) {
            share_copy_view.setVisibility(visible);
        }
    }

    public void setFriendVisible(int visible) {
        if (share_friends_view != null) {
            share_friends_view.setVisibility(visible);
        }
    }


    public void showDialog() {
        super.show();
    }
}