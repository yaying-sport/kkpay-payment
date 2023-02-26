package com.kaiserkalep.bean;

import java.util.List;

/**
 * 公告弹框(重要公告)
 *
 * @Auther: Jack
 * @Date: 2020/12/22 17:37
 * @Description:
 */
public class NoticeDialogData {


    public List<DialogList> dialogList;

    public NoticeDialogData() {
    }

    public NoticeDialogData(List<DialogList> dialogList) {
        this.dialogList = dialogList;
    }

    public static class DialogList {

        /**
         * noticeId : 85738
         * noticeTitle : fwaefa
         * noticeType : 2
         * noticeContent : fawefawef
         */

        private int noticeId;
        private String noticeTitle;
        private String noticeType;
        private String noticeContent;

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

    public static class DialogTag {

        /**
         * 上次提示时间
         */
        public long time;
        /**
         * 今日不提示
         */
        public boolean rememberNoTip;

        public DialogTag() {
        }

        public DialogTag(long time, boolean rememberNoTip) {
            this.time = time;
            this.rememberNoTip = rememberNoTip;
        }
    }
}
