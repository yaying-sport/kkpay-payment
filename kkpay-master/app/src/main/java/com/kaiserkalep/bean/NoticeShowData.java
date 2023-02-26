package com.kaiserkalep.bean;

/**
 * 公告显示时机
 *
 * @Auther: Jack
 * @Date: 2019/10/13 10:26
 * @Description:
 */
public class NoticeShowData {

    public String id;//公告id
    public long time;//每天显示一次时上次时间

    public NoticeShowData(String id, long time) {
        this.id = id;
        this.time = time;
    }
}
