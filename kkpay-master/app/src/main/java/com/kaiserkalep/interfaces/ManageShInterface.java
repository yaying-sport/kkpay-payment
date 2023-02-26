package com.kaiserkalep.interfaces;

import java.util.List;

/**
 * 钱包管理
 *
 * @Auther: Jack
 * @Date: 2021/1/1 18:25
 * @Description:
 */
public interface ManageShInterface {

    /**
     * 跳转浏览器
     *
     * @param url
     */
    void toView(String url);

    /**
     * 管理
     */
    void manage(String id);

    /**
     * 更多
     *
     */
    void more(String str,List<String> list);
}
