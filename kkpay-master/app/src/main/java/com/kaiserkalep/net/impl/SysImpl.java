package com.kaiserkalep.net.impl;

import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.CallbackBase;
import com.kaiserkalep.base.NetManagerBase;
import com.kaiserkalep.net.api.SysApi;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import static com.kaiserkalep.constants.StringConstants.*;
import static com.kaiserkalep.net.url.HomeUrl.*;

/**
 * 系统相关api
 *
 * @Auther: Jack
 * @Date: 2020/8/15 13:47
 * @Description:
 */
public class SysImpl extends NetManagerBase implements SysApi {


    public SysImpl(CallbackBase callBack) {
        super(callBack);
    }

    /**
     * 消息中心（公告/通知）列表
     *
     * @param noticeType 通知类型 1 通知 2 公告
     * @param pageSize   一页条数
     * @param pageNum    页码
     */
    @Override
    public void sysNoticeList(String noticeType, String pageSize, String pageNum) {
        HashMap<String, String> params = new HashMap<>();
        params.put(PAGE_SIZE, pageSize);
        params.put(PAGE_NUM, pageNum);
        params.put(NOTICE_TYPE, noticeType);
        BaseNetUtil.post(SYS_NOTICE_LIST, params, callBack);
    }

    /**
     * 标记已读
     *
     * @param ids 消息id，多个用英文逗号分隔
     */
    @Override
    public void readMsg(String ids) {
        HashMap<String, String> params = new HashMap<>();
        params.put(IDS, ids);
        BaseNetUtil.post(MSG_READ_TAG, params, callBack);
    }

    /**
     * 删除公告/订单信息
     *
     * @param ids 公告/订单信息id
     */
    @Override
    public void sysNoticeDelete(String ids) {
        HashMap<String, String> params = new HashMap<>();
        params.put(IDS, ids);
        BaseNetUtil.post(SYS_NOTICE_DELETE, params, callBack);
    }

    /**
     * 获取未读消息数量
     */
    @Override
    public void sysNoticeUnreadNum() {
        BaseNetUtil.post(SYS_NOTICE_UNREAD_NUM, null, callBack);
    }

    /**
     * 获取意见反馈未读消息数量
     */
    @Override
    public void sysFeedbackUnreadNum() {
        BaseNetUtil.post(SYS_FEEDBACK_UNREAD_NUM, null, callBack);
    }

    /**
     * 版本更新
     */
    @Override
    public void getUpdateVersion() {
        BaseNetUtil.get(GET_VERSION_UPDATE, null, callBack);
    }


    /**
     * 获取当前的服务器时间
     */
    @Override
    public void getServiceDate() {
        BaseNetUtil.post(GET_SERVICE_DATE, null, callBack);
    }

    /**
     * 意见反馈标题
     */
    @Override
    public void getFeedTitle() {
        BaseNetUtil.post(GET_FEEDTITLE, null, callBack);
    }

    /**
     * 新增意见反馈
     *
     * @param titleType
     * @param content
     */
    @Override
    public void addFeedBack(String titleType,String content) {
        HashMap<String, String> params = new HashMap<>();
        params.put(TITLETYPE, titleType);
        params.put(CONTENT, content);
        BaseNetUtil.post(GET_FEEDBACK_ADD, params, callBack);
    }

    /**
     * 意见反馈列表
     *
     * @param pageNum
     * @param pageSize
     */
    @Override
    public void getMyFeedBackList(String pageNum, String pageSize) {
        HashMap<String, String> params = new HashMap<>();
        params.put(PAGE_NUM, pageNum);
        params.put(PAGE_SIZE, pageSize);
        BaseNetUtil.post(GET_MY_FEEDBACK_LIST, params, callBack);
    }

    /**
     * 意见反馈列表详情
     *
     * @param id
     */
    @Override
    public void getFeedBackDetail(String id) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ID, id);
        BaseNetUtil.post(GET_FEEDBACK_DETAIL, params, callBack);
    }

    /**
     * 会员回复反馈列表
     * @param id
     * @param content
     */
    @Override
    public void getFeedBackReply(String id, String content) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ID, id);
        params.put(CONTENT, content);
        BaseNetUtil.post(GET_FEEDBACK_REPLY, params, callBack);
    }

    /**
     * 弹窗公告列表
     */
    @Override
    public void getAlertNoticeList() {
        BaseNetUtil.get(GET_ALERT_NOTICE_LIST, null, callBack);
    }

}
