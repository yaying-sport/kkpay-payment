package com.kaiserkalep.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.kaiserkalep.base.BaseNetUtil;

/**
 * 版本更新实体
 */
public class UpdateDate implements Parcelable {


    /**
     * version : 1.0.0
     * state : 0
     * url : http://www.baidu.com
     * news : ["更新内容1","更新内容2","更新内容3","更新内容4","更新内容5"]
     */

    /*
     * 1.2
     * 1.3
     * */
    private String version;
    private String minVersion;//强更最低版本

    /**
     * 更新状态：1 推荐更新 2 强制更新
     */
    private String updateType;
    //    /**
//     * 版本状态：1已生效 2 未生效 3 已失效
//     */
//    private int release_status;
    private String downloadUrl;//下载地址
    private String description;

    private String md5 = "";

    /**
     * 默认的市场下载地址
     */
    private String downUrl = "";

    public boolean isForce_update() {
        return "2".equals(updateType);
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDownBrowserUrl() {
        return downUrl;
    }

    public void setDownBrowserUrl(String downBrowserUrl) {
        this.downUrl = downBrowserUrl;
    }

    public String getLast_version() {
        return minVersion;
    }

    public void setLast_version(String last_version) {
        this.minVersion = last_version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getState() {
        return updateType;
    }

    public void setState(String state) {
        this.updateType = state;
    }

    public String getUrl() {
        return BaseNetUtil.jointUpdateUrl(downloadUrl);
    }

    public void setUrl(String url) {
        this.downloadUrl = url;
    }

    public String getContent() {
        return description;
    }

    public void setContent(String content) {
        this.description = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
        dest.writeString(this.minVersion);
        dest.writeString(this.updateType);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.description);
        dest.writeString(this.md5);
        dest.writeString(this.downUrl);
    }

    public UpdateDate() {
    }

    protected UpdateDate(Parcel in) {
        this.version = in.readString();
        this.minVersion = in.readString();
        this.updateType = in.readString();
        this.downloadUrl = in.readString();
        this.description = in.readString();
        this.md5 = in.readString();
        this.downUrl = in.readString();
    }

    public static final Parcelable.Creator<UpdateDate> CREATOR = new Parcelable.Creator<UpdateDate>() {
        @Override
        public UpdateDate createFromParcel(Parcel source) {
            return new UpdateDate(source);
        }

        @Override
        public UpdateDate[] newArray(int size) {
            return new UpdateDate[size];
        }
    };
}
