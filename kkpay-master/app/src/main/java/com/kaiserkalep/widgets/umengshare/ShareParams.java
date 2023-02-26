package com.kaiserkalep.widgets.umengshare;

/**
 * @Auther: Administrator
 * @Date: 2019/3/23 0023 13:53
 * @Description:
 */
public class ShareParams {
    /**
     * 分享内容
     */
    private String content;
    /**
     * 分享标题
     */
    private String title;
    /**
     * 分享图片
     */
    private String image;
    /**
     * 分享链接
     */
    private String url;


    public ShareParams() {

    }

    public ShareParams(String title, String content, String image, String url) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}

