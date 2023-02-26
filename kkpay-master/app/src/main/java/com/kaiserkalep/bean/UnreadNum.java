package com.kaiserkalep.bean;

/**
 * 未读消息数量
 *
 * @Auther: Jack
 * @Date: 2020/12/17 20:44
 * @Description:
 */
public class UnreadNum {


    /**
     * type2 : 0
     * type1 : 30
     */

    private int type2;//公告
    private int type1;//通知


    public int getUnread() {
        return type1 + type2;
    }

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }
}
