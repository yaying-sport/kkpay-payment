package com.kaiserkalep.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 通知/公告
 *
 * @Auther: Jack
 * @Date: 2020/8/17 14:43
 * @Description:
 */
public class SysNoticeListData {


    /**
     * total : 2
     * pageNum : 1
     * pageSize : 2
     * rows : [{"noticeId":79082,"noticeTitle":"双十一巅峰钜惠","noticeType":"1","noticeContent":"尊贵的亚盈会员：亚盈体育新上线【11月11日狂欢盛宴】最低门槛100元，最高可获得18888元，活动时间为：11月11日00:00~23:59(北京时间)，立即查看详情活动哦！","status":"0","source":0,"id":3683836},{"noticeId":79083,"noticeTitle":"【WM 例行维护通知】","noticeType":"1","noticeContent":"尊敬的亚盈体育会员：WM真人馆将于北京时间11月4日(周三)10：00-16：00进行例行维护，维护期间请选择其他游戏进行娱乐，带来不便敬请谅解，谢谢！","status":"0","source":0,"id":3683837}]
     */

    private int total;
    private int pageNum;
    private int pageSize;
    private List<RowsBean> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean implements Parcelable {
        /**
         * noticeId : 79082
         * noticeTitle : 双十一巅峰钜惠
         * noticeType : 1
         * noticeContent : 尊贵的亚盈会员：亚盈体育新上线【11月11日狂欢盛宴】最低门槛100元，最高可获得18888元，活动时间为：11月11日00:00~23:59(北京时间)，立即查看详情活动哦！
         * status : 0
         * source : 0
         * id : 3683836
         */

        /**
         * 消息id，用于查看详情
         */
        private String noticeId;
        private String noticeTitle;
        private int noticeType;
        private String noticeContent;
        /**
         * 状态，0未读，1已读
         */
        private int status;
        private int source;
        /**
         * 发送id，用于标记已读、删除
         */
        private String id;

        private String createTime;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        private boolean isSelect;


        public String getNoticeId() {
            return noticeId;
        }

        public void setNoticeId(String noticeId) {
            this.noticeId = noticeId;
        }

        public String getNoticeTitle() {
            return noticeTitle;
        }

        public void setNoticeTitle(String noticeTitle) {
            this.noticeTitle = noticeTitle;
        }

        public int getNoticeType() {
            return noticeType;
        }

        public void setNoticeType(int noticeType) {
            this.noticeType = noticeType;
        }

        public String getNoticeContent() {
            return noticeContent;
        }

        public void setNoticeContent(String noticeContent) {
            this.noticeContent = noticeContent;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.noticeId);
            dest.writeString(this.noticeTitle);
            dest.writeInt(this.noticeType);
            dest.writeString(this.noticeContent);
            dest.writeInt(this.status);
            dest.writeInt(this.source);
            dest.writeString(this.id);
            dest.writeString(this.createTime);
            dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        }

        public RowsBean() {
        }

        protected RowsBean(Parcel in) {
            this.noticeId = in.readString();
            this.noticeTitle = in.readString();
            this.noticeType = in.readInt();
            this.noticeContent = in.readString();
            this.status = in.readInt();
            this.source = in.readInt();
            this.id = in.readString();
            this.createTime = in.readString();
            this.isSelect = in.readByte() != 0;
        }

        public static final Parcelable.Creator<RowsBean> CREATOR = new Parcelable.Creator<RowsBean>() {
            @Override
            public RowsBean createFromParcel(Parcel source) {
                return new RowsBean(source);
            }

            @Override
            public RowsBean[] newArray(int size) {
                return new RowsBean[size];
            }
        };
    }
}
