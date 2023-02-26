package com.kaiserkalep.bean;

import java.io.File;
import java.util.List;

/**
 * @Auther: Jack
 * @Date: 2020/8/25 16:21
 * @Description:
 */
public class AdGuideData {
    /**
     * overStatus : 1
     * list : [{"id":1,"title":"测试一个","url":"http://www.baidu.com","image":"http://www.baidu.com","showTime":12,"linkType":0},{"id":2,"title":"测试两个","url":"http://www.baidu.com","image":"http://www.baidu.com","showTime":13,"linkType":0}]
     */

    /**
     * 是否允许跳过，1是，0否
     */
    private int overStatus;
    private List<ListBean> list;

    public int getOverStatus() {
        return overStatus;
    }

    public void setOverStatus(int overStatus) {
        this.overStatus = overStatus;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Cloneable {
        /**
         * id : 1
         * title : 测试一个
         * url : http://www.baidu.com
         * image : http://www.baidu.com
         * showTime : 12
         * linkType : 0
         */

        private int id;
        private String title;
        private String url;
        private String image;
        private int showTime;
        /**
         * 跳转类型，0无跳转，1内部浏览器，2外部浏览器
         */
        private int linkType;
        private File file;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getShowTime() {
            return showTime;
        }

        public void setShowTime(int showTime) {
            this.showTime = showTime;
        }

        public int getLinkType() {
            return linkType;
        }

        public void setLinkType(int linkType) {
            this.linkType = linkType;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        @Override
        public ListBean clone() {
            ListBean data = null;
            try {
                data = (ListBean) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return data;
        }
    }
}
