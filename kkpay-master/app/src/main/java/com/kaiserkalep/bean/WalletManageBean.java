package com.kaiserkalep.bean;

import android.content.Context;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.kaiserkalep.constants.Config.EGATIVE_ONE;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

public class WalletManageBean implements Serializable {
    private int type; //0:微信;1:支付宝;2,银行卡
//    private String title;//微信 支付宝 银行卡
//    private String account;//账号
//    private String address;//无用

    private String id;
//    private String memberId;
    private String bankId;
    private String bankName;
    private String bankNo;
    private String qrcode;
    private String icon;

    public WalletManageBean() {
    }

//    public WalletManageBean(int type, String title, String account, String address) {
//        this.title = title;
//        this.account = account;
//        this.type = type;
//        this.address = address;
//    }

//    public WalletManageBean(int type, String title, String account, String address) {
//        this.bankName = title;
//        this.bankNo = account;
//        this.type = type;
//        this.qrcode = address;
//    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public String getMemberId() {
//        return memberId;
//    }
//
//    public void setMemberId(String memberId) {
//        this.memberId = memberId;
//    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    //    public int getIcon() {
//        switch (type) {
//            case ZERO:
//                return R.drawable.icon_wechat_logo;
//            case ONE:
//                return R.drawable.icon_aliba_logo;
//            case TWO:
//                return R.drawable.icon_bank_logo;
//        }
//        return ZERO;
//    }

    public int getCircleIcon() {
        switch (type) {
            case ZERO:
                return R.drawable.icon_circle_wechat;
            case ONE:
                return R.drawable.icon_circle_alipay;

            case TWO:
                return R.drawable.icon_circle_bank;
        }
        return ZERO;
    }


    public String getDelField() {
        switch (type) {
            case ZERO:
                return StringConstants.PAYTYPE_WECHAT;
            case ONE:
                return StringConstants.PAYTYPE_ALI;
            case TWO:
                return StringConstants.PAYTYPE_BANK;
        }
        return "";
    }

    public static int getPayStrIcon(String str) {
        switch (str) {
            case StringConstants.PAYTYPE_WECHAT:
                return R.drawable.icon_wechat_logo;
            case StringConstants.PAYTYPE_ALI:
                return R.drawable.icon_aliba_logo;
            case StringConstants.PAYTYPE_BANK:
                return R.drawable.icon_bank_logo;
        }
        return EGATIVE_ONE;
    }

    /**
     * 设置支付集合
     *
     * @param response
     */
//    public static List<WalletManageBean> setWalletList(Context content, UserInfoBean response) {
//        List<WalletManageBean> listBank = new ArrayList<>();
//        if (null != content && null != response) {
//            String wechatQrcode = response.getWechatQrcode();
//            String realName = response.getRealName();
//            if (CommonUtils.StringNotNull(wechatQrcode)) {
//                String languageString = MyApp.getLanguageString(content, R.string.Wechat_name);
//                listBank.add(new WalletManageBean(ZERO, languageString, realName, wechatQrcode));
//            }
//            String aliAccount = response.getAliAccount();
//            String aliQrcode = response.getAliQrcode();
//            if (CommonUtils.StringNotNull(aliAccount) && CommonUtils.StringNotNull(aliQrcode)) {
//                String languageString = MyApp.getLanguageString(content, R.string.Alipay_name);
//                listBank.add(new WalletManageBean(ONE, languageString, aliAccount, aliQrcode));
//            }
//            String bankId = response.getBankId();
//            String bankNo = response.getBankNo();
//            if (CommonUtils.StringNotNull(bankId) && CommonUtils.StringNotNull(bankNo)) {
//                String languageString = MyApp.getLanguageString(content, R.string.Bank_name);
//                listBank.add(new WalletManageBean(TWO, languageString, bankNo, bankId));
//            }
//        }
//        return listBank;
//    }

}
