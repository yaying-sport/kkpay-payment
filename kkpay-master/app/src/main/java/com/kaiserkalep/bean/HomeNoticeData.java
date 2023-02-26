package com.kaiserkalep.bean;

/**
 * 首页公告栏
 *
 * @Auther: Jack
 * @Date: 2020/12/10 17:31
 * @Description:
 */
public class HomeNoticeData {


    /**
     * noticeId : 84741
     * noticeTitle : 【银行停用公告】
     * noticeType : 2
     * noticeContent : 【最新通告】：因财务业务清算需要，公司入款【建设银行-连海浩6217003******025611】已停用入款，敬请会员入款前进入存款页面获取最新账号或咨询在线客服再进行存款，请及时删除存款记录，如入款到停用账号，公司无法查收将不予负责，敬请互相转告，带来不便敬请谅解!
     */

    private int noticeId;
    private String noticeTitle;
    private String noticeType;
    private String noticeContent = "暂无公告，敬请留意平台最新动态！";
    private boolean noNotice;

    public boolean isNoNotice() {
        return noNotice;
    }

    public HomeNoticeData setNoNotice(boolean noNotice) {
        this.noNotice = noNotice;
        return this;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }
}
