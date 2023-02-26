package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaiserkalep.R;
import com.kaiserkalep.base.ViewBase;
import com.kaiserkalep.eventbus.MainDoubleClickIndexEvent;
import com.kaiserkalep.interfaces.MainViewCallBackListener;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

import q.rorbin.badgeview.QBadgeView;

/**
 * 首页底部tab
 *
 * @Auther: Jack
 * @Date: 2019/11/27 15:17
 * @Description:
 */
public class MainHomeBottomTab extends ViewBase implements View.OnClickListener {

    private ViewPager mViewPager;
    private List<LinearLayout> linearLayouts;
    private QBadgeView newMessage;
    private LinearLayout messageView;
    private ImageView tvTabBg;

    public MainHomeBottomTab(Context context) {
        super(context);
    }

    public MainHomeBottomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainHomeBottomTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public int getViewId() {
        return R.layout.view_main_home_bottom_tab;
    }

    @Override
    public void afterViews(View v) {
        linearLayouts = new ArrayList<>();
        tvTabBg = findViewById(R.id.tv_tab_bg);
        linearLayouts.add(findViewById(R.id.ll_tab_1));
        linearLayouts.add(findViewById(R.id.ll_tab_2));
        linearLayouts.add(findViewById(R.id.ll_tab_3));
        messageView = findViewById(R.id.ll_tab_4);
        linearLayouts.add(messageView);
        findViewById(R.id.btn_tab_center).setOnClickListener(this);
        initStatus();
    }

    /**
     * 初始状态及点击事件
     */
    private void initStatus() {
        if (CommonUtils.ListNotNull(linearLayouts)) {
            for (int i = 0; i < linearLayouts.size(); i++) {
                LinearLayout linearLayout = linearLayouts.get(i);
                linearLayout.setSelected(i == 0);
                linearLayout.setTag(i);
                linearLayout.setOnClickListener(this);
            }
        }
    }


    /**
     * 绑定viewpage
     *
     * @param mViewPager
     */
    public void bindData(ViewPager mViewPager, MainViewCallBackListener listener) {
        this.listener = listener;
        this.mViewPager = mViewPager;
        this.mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setIndex(position);
            }
        });
    }

    MainViewCallBackListener listener;

    /**
     * 设置选择
     *
     * @param index 为负数清除选中状态
     */
    public void setIndex(int index) {
        for (int i = 0; i < linearLayouts.size(); i++) {
            LinearLayout linearLayout = linearLayouts.get(i);
            if (index < 0) {
                linearLayout.setSelected(false);
            } else {
                linearLayout.setSelected(i == index);
            }
        }
        if (mViewPager != null && index >= 0) {
            if (index == mViewPager.getCurrentItem()) {
                if (listener != null) {
                    listener.succeedCallBack(true);
                }
                return;
            }
            mViewPager.setCurrentItem(index);
        }
    }

    private int MIN_CLICK_DELAY_TIME = 600;
    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_tab_center) {
            if (listener != null) {
                listener.cameraCallBack();
            }
        } else {
            int index = (int) view.getTag();
            if (view.isSelected()) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime < MIN_CLICK_DELAY_TIME) {
                    EventBusUtil.post(new MainDoubleClickIndexEvent(index));
                    LogUtils.d("双击__" + (currentTime - lastClickTime));
                }
                lastClickTime = currentTime;
                return;
            }
            if (index == MainActivity.TAB_MINE && !checkLogin()) {
                return;
            }
            tvTabBg.setBackgroundResource(index == MainActivity.TAB_MINE ? R.color.activity_bg : R.color.view_bg);
            setIndex(index);
        }
    }

    public void showNewMessage(int count) {
        if (newMessage == null) {
            newMessage = new QBadgeView(getContext());
            newMessage.setBadgeBackgroundColor(SkinResourcesUtils.getColor(R.color.unread_message_dot));
            newMessage.setBadgePadding(3, true);
            newMessage.setBadgeTextSize(4, true);
            newMessage.setGravityOffset(25, 7, true);
            if (messageView != null) {
                newMessage.bindTarget(messageView);
            }
        }

        if (count > 0) {
            newMessage.setBadgeNumber(-1);
        } else {
            hideNewMessage();
        }
    }

    private void hideNewMessage() {
        if (newMessage != null) {
            newMessage.hide(true);
        }
    }
}
