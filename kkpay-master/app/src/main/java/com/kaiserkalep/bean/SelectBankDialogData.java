package com.kaiserkalep.bean;

import com.kaiserkalep.R;
import com.kaiserkalep.interfaces.SelectBankInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

import java.util.List;

/**
 * 选择银行
 *
 * @Auther: Jack
 * @Date: 2020/12/29 14:54
 * @Description:
 */
public class SelectBankDialogData<T> {

    public List<T> list;
    public SelectBankInterface<T> listener;
    public String title;
    public int logoPlaceholder = R.drawable.icon_bank_logo;

    public SelectBankDialogData() {
    }

    public SelectBankDialogData(String title, int logoPlaceholder, List<T> list,
                                SelectBankInterface<T> listener) {
        this.list = list;
        this.logoPlaceholder = logoPlaceholder;
        this.listener = listener;
        this.title = title;
    }
}
