package com.kaiserkalep.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * 广告实体
 * <p>
 * {@link }
 *
 * @Auther: Administrator
 * @Date: 2019/6/8 0008 19:52
 * @Description:
 */
public class AdvertData {

    public static class ChildBean implements Parcelable, Cloneable {
        /**
         * image : https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2023065142,420687810&fm=26&gp=0.jpg
         * url : http://www.google.com
         * title :
         */

        private String id;
        private String image;
        private String url;
        private String title;
        private transient Drawable drawable;
        private File file;

        /**
         * 广告生效状态, 0,未开始   1,生效中
         */
        private int status;


        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public ChildBean clone() {
            ChildBean data = null;
            try {
                data = (ChildBean) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.image);
            dest.writeString(this.url);
            dest.writeString(this.title);
            dest.writeSerializable(this.file);
            dest.writeInt(this.status);
        }

        public ChildBean() {
        }

        protected ChildBean(Parcel in) {
            this.id = in.readString();
            this.image = in.readString();
            this.url = in.readString();
            this.title = in.readString();
            this.file = (File) in.readSerializable();
            this.status = in.readInt();
        }

        public static final Creator<ChildBean> CREATOR = new Creator<ChildBean>() {
            @Override
            public ChildBean createFromParcel(Parcel source) {
                return new ChildBean(source);
            }

            @Override
            public ChildBean[] newArray(int size) {
                return new ChildBean[size];
            }
        };
    }
}
