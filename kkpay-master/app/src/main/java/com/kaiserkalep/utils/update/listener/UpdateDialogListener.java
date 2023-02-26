package com.kaiserkalep.utils.update.listener;

/**
 * @author hule
 * @date 2019/7/10 15:48
 * description:弹出的更新对话框的接口
 */
public interface UpdateDialogListener {


    /**
     * 点击立即更新，用来开启权限检查和下载服务
     */
    void updateDownLoad();

    /**
     * 重试按钮，进行重新下载
     */
    void updateRetry();

    /**
     * 若应用下载失败，可以选择去应用市场下载或者去浏览器下载
     */
    void downFromBrowser();

    /**
     *  取消更新
     */
    void cancelUpdate();

}
