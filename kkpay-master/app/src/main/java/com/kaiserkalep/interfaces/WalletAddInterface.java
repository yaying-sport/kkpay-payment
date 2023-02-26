package com.kaiserkalep.interfaces;

import com.kaiserkalep.bean.WalletManageBean;

/**
 * 钱包管理
 *
 * @Auther: Jack
 * @Date: 2021/1/1 18:25
 * @Description:
 */
public interface WalletAddInterface {

    /**
     * 解绑
     *
     * @param position
     */
    void delete(int position, WalletManageBean item);
}
