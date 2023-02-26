package com.kaiserkalep.base;

/**
 * @Auther: Administrator
 * @Date: 2019/3/22 0022 11:09
 * @Description:
 */
public abstract class PageBaseFragment extends ZZFragment {

    /**
     * 是否需要初始化
     * 默认true需要初始化
     */
    protected volatile boolean isNeedInit = true;

    /**
     * onActivityCreated
     */
    @Override
    protected void afterViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 外层每次选中/onReStart时调用
     */
    public void onUserViewVisible() {
        if (isNeedInit) {
            isNeedInit = false;
            onUserFirstVisible();
        }else{
            onUserDoubleVisible();
        }
    }

    /**
     * 第一次初始化
     */
    protected void onUserFirstVisible() {

    }

    /**
     * 重复显示时调用
     */
    protected void onUserDoubleVisible() {

    }

    /**
     * ViewPager中下标跳转
     *
     * @param index
     */
    public void setIndex(int index) {

    }

    /**
     * 下次显示时，是否需要刷新页面数据
     *
     * @param refresh true刷新
     */
    public void setNextShowRefresh(boolean refresh) {

    }

    public void scrollTop() {

    }

}