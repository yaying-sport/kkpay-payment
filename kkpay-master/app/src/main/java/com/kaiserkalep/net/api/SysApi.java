package com.kaiserkalep.net.api;

/**
 * 系统相关api
 *
 * @Auther: Jack
 * @Date: 2020/8/15 13:47
 * @Description:
 */
public interface SysApi {

    /**
     * 消息中心（公告/通知）列表
     *
     * @param noticeType 通知类型 1 通知 2 公告
     * @param pageSize   一页条数
     * @param pageNum    页码
     */
    void sysNoticeList(String noticeType, String pageSize, String pageNum);

    /**
     * 标记已读
     *
     * @param ids 消息id，多个用英文逗号分隔
     */
    void readMsg(String ids);

    /**
     * 删除公告/订单信息
     *
     * @param ids 消息/公告id
     */
    void sysNoticeDelete(String ids);

    /**
     * 获取未读消息数量
     */
    void sysNoticeUnreadNum();

    /**
     * 获取意见反馈未读消息数量
     */
    void sysFeedbackUnreadNum();

    /**
     * 版本更新
     */
    void getUpdateVersion();

    /**
     * 获取当前的服务器时间
     */
    void getServiceDate();

    /**
     * 意见反馈标题
     */
    void getFeedTitle();

    /**
     * 新增意见反馈
     *
     * @param titleType
     * @param content
     */
    void addFeedBack(String titleType, String content);

    /**
     * 意见反馈列表
     *
     * @param pageNum
     * @param pageSize
     */
    void getMyFeedBackList(String pageNum, String pageSize);

    /**
     * 意见反馈列表详情
     *
     * @param id
     */
    void getFeedBackDetail(String id);

    /**
     * 会员回复反馈列表
     *
     * @param id
     * @param content
     */
    void getFeedBackReply(String id, String content);

    /**
     * 弹窗公告列表
     */
    void getAlertNoticeList();
}
