package com.kaiserkalep.bean;

import java.util.List;

/**
 * 更多网址数据
 *
 * @Auther: Jack
 * @Date: 2020/12/22 17:37
 * @Description:
 */
public class ToMoreDialogData {

    public List<String> dialogList;
    public String title = "";

    public ToMoreDialogData() {
    }

    public ToMoreDialogData(String title, List<String> dialogList) {
        this.title = title;
        this.dialogList = dialogList;
    }

}
