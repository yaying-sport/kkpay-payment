package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.StatusBarUtil;
import com.kaiserkalep.utils.UIUtils;

/**
 * @Auther: Jack
 * @Date: 2020/12/26 15:51
 * @Description:
 */
public class MyTopSnackBar extends LinearLayout {

    TextView tvMsg;
    private Animation in;
    private Animation out;
    private ViewGroup parent;
    private int time = 2500;
    private boolean isShow;

    public MyTopSnackBar(Context context) {
        super(context);
    }

    public MyTopSnackBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTopSnackBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void showMsg(ViewGroup parent, String msg) {
        if (parent != null && this.parent == null) {
            this.parent = parent;
            View view = View.inflate(parent.getContext(), R.layout.view_top_snack_bar, this);
            View rl = view.findViewById(R.id.rl_content);
            tvMsg = view.findViewById(R.id.tv_msg);
            UIUtils.setMargins2(rl, 0, StatusBarUtil.getStatusBarHeight(getContext()), 0, 0);
            in = getIn();
            out = getOut();
        }
        tvMsg.setText(CommonUtils.StringNotNull(msg) ? msg : MyApp.getLanguageString(getContext(),R.string.toast_common_system_net_error));
        if (!isShow) {
            this.parent.removeView(this);
            this.parent.addView(this);
            startAnimation(in);
        }
        MyApp.removeCallbacks(runnable);
        MyApp.postDelayed(runnable, time);
    }

    private Runnable runnable = () -> startAnimation(out);

    private Animation getIn() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.top_in);
        animation.setInterpolator(new FastOutSlowInInterpolator());
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShow = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    private Animation getOut() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.top_out);
        animation.setInterpolator(new FastOutSlowInInterpolator());
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShow = false;
                if (parent != null) {
                    parent.removeView(MyTopSnackBar.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }
}
