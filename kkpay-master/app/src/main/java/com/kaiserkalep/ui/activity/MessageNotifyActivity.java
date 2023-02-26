package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PagerBaseAdapter;
import com.kaiserkalep.adapter.ZZNavigatorAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ViewPagerHelper;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.UnreadNum;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.ui.fragmnet.MessageNotifyFragment;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.NumberUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.CommonNavigator;
import com.kaiserkalep.widgets.ViewPagerFixed;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import q.rorbin.badgeview.QBadgeView;

import static com.kaiserkalep.constants.Config.ZERO;


/**
 * 消息通知,活动,公告
 *
 * @Auther: Jack
 * @Date: 2020/8/18 10:12
 * @Description:
 */
public class MessageNotifyActivity extends ZZActivity implements View.OnClickListener, CommTitle.OnBackListener {


    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_page)
    ViewPagerFixed mViewPager;

    private List<String> classify = new ArrayList<String>();
    private List<PageBaseFragment> mDataList = new ArrayList<>();
    private int position;
    private TextView tvBack;
    public TextView tvFunction;
    public boolean isEdit;
    public boolean selectAll;
    private List<TextView> titles;
    private List<QBadgeView> unreadView;
    public static int CHECK_DETAIL_CODE = 110;
    private int index;
    private String title = "";


    @Override
    public int getViewId() {
        return R.layout.activity_message_notify;
    }

    private void setListTitle() {
        if (classify.size() > ZERO) {
            classify.clear();
        }

        classify.add(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_tz));
        classify.add(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_gg));
    }

    @Override
    public void afterViews() {
        setListTitle();
        title = MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_MessageCenter);
        commTitle.init(title, "",  MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_edit), 0, this);
        tvBack = commTitle.getTvBack();
        tvFunction = commTitle.getTvFunction();
        tvFunction.setVisibility(View.GONE);
        commTitle.setOnBackListener(this);

        index = NumberUtils.stringToInt(getStringParam(StringConstants.INDEX));

        Context context = getContext();
        assert context != null;
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdjustMode(true);
        mDataList.add(getContentFragment(Config.ONE));
        mDataList.add(getContentFragment(Config.TWO));

        PagerBaseAdapter pagerBaseAdapter = new PagerBaseAdapter(getSupportFragmentManager(), mDataList);
        mViewPager.setAdapter(pagerBaseAdapter);
        mViewPager.setOffscreenPageLimit(mDataList.size());

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (isEdit) {//编辑中切换退出编辑
                    cancelEdit();
                }
                checkList(position);//校验列表隐藏编辑按钮
                MessageNotifyActivity.this.position = position;
                ((MessageNotifyFragment) mDataList.get(position)).onUserViewVisible();
            }
        });

        ZZNavigatorAdapter navigatorAdapter = new ZZNavigatorAdapter(context, classify, mViewPager,
                SkinResourcesUtils.getColor(R.color.rr_navigator_select), SkinResourcesUtils.getColor(R.color.rr_navigator_normal));
        navigatorAdapter.setTextSize(15);
        commonNavigator.setAdapter(navigatorAdapter);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
        titles = commonNavigator.getTitles();

        mViewPager.post(() -> {
            mViewPager.setCurrentItem(index);
            ((MessageNotifyFragment) mDataList.get(index)).onUserViewVisible();
        });
    }

    /**
     * 获取关注/粉丝
     *
     * @return
     */
    private MessageNotifyFragment getContentFragment(int type) {
        MessageNotifyFragment fragment = new MessageNotifyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(StringConstants.TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 获取未读消息
     */
    public void getMsgNum() {
        new SysImpl(new ZZNetCallBack<UnreadNum>(this, UnreadNum.class) {
            @Override
            public void onSuccess(UnreadNum response) {
                setUnReadMsg(response);
            }
        }.setNeedDialog(false)).sysNoticeUnreadNum();
    }

    /**
     * 设置tab未读消息
     *
     * @param response
     */
    private void setUnReadMsg(UnreadNum response) {
        if (response != null && CommonUtils.ListNotNull(titles)) {
            if (!CommonUtils.ListNotNull(unreadView)) {
                unreadView = new ArrayList<>();
                for (View title : titles) {
                    if (title != null) {
                        QBadgeView newMessage = new QBadgeView(getContext());
                        newMessage.setBadgeBackgroundColor(SkinResourcesUtils.getColor(R.color.unread_message_dot));
                        newMessage.setBadgePadding(5, true);
                        newMessage.setBadgeTextSize(9, true);
                        newMessage.setGravityOffset(24, 8, true);
                        newMessage.bindTarget(title);
                        unreadView.add(newMessage);
                    }
                }
            }
            setUnreadView(unreadView.get(0), response.getType1());
            setUnreadView(unreadView.get(1), response.getType2());
        }
    }

    /**
     * 设置未读消息数量
     *
     * @param qBadgeView
     * @param unreadNum
     */
    private void setUnreadView(QBadgeView qBadgeView, int unreadNum) {
        if (qBadgeView != null) {
            if (unreadNum > 0) {
                qBadgeView.setBadgeNumber(unreadNum);
            } else {
                qBadgeView.hide(true);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_function) {
            if (!currentHasList(position)) {//无列表时点击无效，有该页面列表加载完成后设置编辑按钮显示与隐藏
                return;
            }
            if (!isEdit) {//进入编辑
                startEdit();
            } else {
                selectAll(!selectAll, true);
            }
        }
    }

    /**
     * 无列表时无编辑按钮
     *
     * @param position
     */
    private void checkList(int position) {
        tvFunction.setVisibility(currentHasList(position) ? View.VISIBLE : View.GONE);
    }

    /**
     * 当前页列表是否有数据
     *
     * @param position
     * @return
     */
    private boolean currentHasList(int position) {
        MessageNotifyFragment currentF = getCurrentF(position);
        if (currentF != null) {
            return CommonUtils.ListNotNull(currentF.list);
        }
        return false;
    }

    /**
     * 设置权限状态
     *
     * @param selectAll  是否全选
     * @param needNotify 是否刷新列表
     */
    public void selectAll(boolean selectAll, boolean needNotify) {
        tvFunction.setText(selectAll ? MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_UnselectAll) : MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_SelectAll));
        if (needNotify) {
            MessageNotifyFragment currentF = getCurrentF();
            if (currentF != null) {
                currentF.selectAll(selectAll);
            }
        }
        this.selectAll = selectAll;
    }

    /**
     * 进入编辑状态
     */
    public void startEdit() {
        isEdit = true;
        UIUtils.setLeftDrawable(getContext(), tvBack, R.drawable.icon_left_close);
        tvBack.setCompoundDrawablePadding(UIUtils.dip2px(10));
        selectAll = false;
        tvFunction.setText(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_SelectAll));
        MessageNotifyFragment currentF = getCurrentF();
        if (currentF != null) {
            currentF.startEdit();
        }
    }

    /**
     * 退出编辑状态
     */
    public void cancelEdit() {
        if (!isEdit) {
            return;
        }
        isEdit = false;
        UIUtils.setLeftDrawable(getContext(), tvBack, R.drawable.left_black);
        tvBack.setCompoundDrawablePadding(UIUtils.dip2px(10));
        selectAll(false, true);
        tvFunction.setText(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_edit));
        MessageNotifyFragment currentF = getCurrentF();
        if (currentF != null) {
            currentF.cancelEdit();
        }
    }

    /**
     * 获取当前页面
     *
     * @return
     */
    private MessageNotifyFragment getCurrentF() {
        return getCurrentF(position);
    }

    /**
     * 获取当前页面
     *
     * @return
     */
    private MessageNotifyFragment getCurrentF(int position) {
        if (CommonUtils.ListNotNull(mDataList)) {
            return (MessageNotifyFragment) mDataList.get(position);
        }
        return null;
    }

    @Override
    public void back() {
        if (isEdit) {//返回键退出编辑/关闭
            cancelEdit();
        } else {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
        getMsgNum();
    }


    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == CHECK_DETAIL_CODE) {//查看详情/删除消息,刷新页面
                    MessageNotifyFragment currentF = getCurrentF();
                    if (currentF != null) {
                        currentF.autoRefresh();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
