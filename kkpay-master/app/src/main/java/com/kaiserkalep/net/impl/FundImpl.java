package com.kaiserkalep.net.impl;

import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.CallbackBase;
import com.kaiserkalep.base.NetManagerBase;
import com.kaiserkalep.net.api.FundApi;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import static com.kaiserkalep.constants.StringConstants.*;
import static com.kaiserkalep.net.url.HomeUrl.*;

/**
 * 资金相关api
 *
 * @Auther: Jack
 * @Date: 2020/8/15 12:00
 * @Description:
 */
public class FundImpl extends NetManagerBase implements FundApi {


    public FundImpl(CallbackBase callBack) {
        super(callBack);
    }

    /**
     * 中心钱包信息
     */
    @Override
    public void walletInfo() {
    }

    @Override
    public void getBankList() {
        BaseNetUtil.post(GET_BANKLIST, null, callBack);
    }


    /**
     * 微信更新收款码
     *
     * @param file 文件路径
     */
//    @Override
//    public void updateWxCode(String file, String pass) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put(QRCODE_FILE, file);
//        params.put(PAYPWD, pass);
//        BaseNetUtil.post(GET_WALLET_UPDATEWXCODE, params, callBack);
//    }

    /**
     * 支付宝更新收款码
     *
     * @param zfbNo 支付宝号
     * @param file  支付宝二维码 路径
     */
//    @Override
//    public void updateZFB(String zfbNo, String file, String pass) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put(ZFBNO, zfbNo);
//        params.put(QRCODE_FILE, file);
//        params.put(PAYPWD, pass);
//        BaseNetUtil.post(GET_WALLET_UPDATEZFB, params, callBack);
//    }

    /**
     * 添加支付方式  微信 支付宝 银行卡
     */
    @Override
    public void updateBank(HashMap<String, String> params) {
        BaseNetUtil.post(GET_WALLET_ADDBANK, params, callBack);
    }

    /**
     * @param memberId
     */
    @Override
    public void deleteWallet(String memberId) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ID, memberId);
        BaseNetUtil.get(GET_WALLET_DELETE, params, callBack);
    }

    /**
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageSize      页面大小
     * @param pageNum       页码
     * @param status        状态 0未销售，1销售部分，2完成，-1取消，传空取所有
     */
    @Override
    public void mySell(String orderByColumn, String isAsc, String pageSize, String pageNum, String status) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDER_BY_COLUMN, orderByColumn);
        BaseNetUtil.putStringParams(params, ISASC, isAsc);
        BaseNetUtil.putStringParams(params, PAGE_SIZE, pageSize);
        BaseNetUtil.putStringParams(params, PAGE_NUM, pageNum);
        BaseNetUtil.putStringParams(params, STATUS, status);
        BaseNetUtil.get(GET_MY_SELL, params, callBack);

    }

    /**
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageSize      页面大小
     * @param pageNum       页码
     * @param status        状态 1进行中，4已完成，-1已取消，3暂停中，不传取全部
     */
    @Override
    public void myBuyOrder(String orderByColumn, String isAsc, String pageSize, String pageNum, String status) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDER_BY_COLUMN, orderByColumn);
        BaseNetUtil.putStringParams(params, ISASC, isAsc);
        BaseNetUtil.putStringParams(params, PAGE_SIZE, pageSize);
        BaseNetUtil.putStringParams(params, PAGE_NUM, pageNum);
        BaseNetUtil.putStringParams(params, STATUS, status);
        BaseNetUtil.get(GET_MY_BUYORDER, params, callBack);
    }

    /**
     * 交易大厅卖单列表
     *
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageNum       页码
     * @param pageSize      页面大小
     */
    @Override
    public void OrderBuyList(String orderByColumn, String isAsc, String pageSize, String pageNum) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDER_BY_COLUMN, orderByColumn);
        BaseNetUtil.putStringParams(params, ISASC, isAsc);
        BaseNetUtil.putStringParams(params, PAGE_NUM, pageNum);
        BaseNetUtil.putStringParams(params, PAGE_SIZE, pageSize);
        BaseNetUtil.get(GET_ORDER_BUY_LIST, params, callBack);
    }

    /**
     * 买卖币获取表单
     */
    @Override
    public void getOrderFrom() {
        BaseNetUtil.get(GET_ORDER_FROM, null, callBack);
    }

    /**
     * @param orderNo     订单号
     * @param amount      金额
     * @param receiveType 支持收款方式，bank银行卡，ali支付宝，wechat微信；多个逗号分割
     * @param canSplit    是否支持拆单，0不支持，1支持
     */
    @Override
    public void getOrderSell(String orderNo, String amount, String receiveType, String canSplit, String memberBankId) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.putStringParams(params, AMOUNT, amount);
        BaseNetUtil.putStringParams(params, RECEIVETYPE, receiveType);
        BaseNetUtil.putStringParams(params, CANSPLIT, canSplit);
        BaseNetUtil.putStringParams(params, MEMBER_BANK_ID, memberBankId);
        BaseNetUtil.post(GET_ORDER_SELL, params, callBack);
    }

    /**
     * @param orderNo 订单号
     */
    @Override
    public void getOrderSellDetail(String orderNo) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.get(GET_ORDER_SELLDETAIL, params, callBack);
    }

    /**
     * @param orderNo     订单号
     * @param orderType   订单类型，卖单：sell，买单：buy
     * @param status      状态，对应买卖单状态   (需要改变成什么状态，就传什么状态) 0未销售，1销售部分，2完成，-1取消，-2部分取消
     * @param payProof    支付凭证，多个逗号分隔
     * @param payPassword 支付密码
     */
    public void setChangeStatus(String orderNo, String orderType, String status, String payProof, String payPassword) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.putStringParams(params, ORDERTYPE, orderType);
        BaseNetUtil.putStringParams(params, STATUS, status);
        BaseNetUtil.putStringParams(params, PAYPROOF, payProof);
        BaseNetUtil.putStringParams(params, PAYPASSWORD, payPassword);
        BaseNetUtil.post(GET_ORDER_CHANGESTATUS, params, callBack);
    }

    /**
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageSize      页面大小
     * @param pageNum       页码
     */
    @Override
    public void myWalletBill(String orderByColumn, String isAsc, String pageSize, String pageNum) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDER_BY_COLUMN, orderByColumn);
        BaseNetUtil.putStringParams(params, ISASC, isAsc);
        BaseNetUtil.putStringParams(params, PAGE_SIZE, pageSize);
        BaseNetUtil.putStringParams(params, PAGE_NUM, pageNum);
        BaseNetUtil.get(GET_MY_WALLET_BILL, params, callBack);

    }

    /**
     * @param sellOrderNo 卖币订单号
     * @param orderNo     买币订单号
     * @param amount      购买金额
     * @param payType     支付类型
     * @param isSplit     是否拆分
     */
    @Override
    public void myBuy(String sellOrderNo, String orderNo, String amount, String payType, String isSplit, String memberBankId) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, SELLORDERNO, sellOrderNo);
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.putStringParams(params, AMOUNT, amount);
        BaseNetUtil.putStringParams(params, PAY_TYPE, payType);
        BaseNetUtil.putStringParams(params, ISSPLIT, isSplit);
        BaseNetUtil.putStringParams(params, MEMBER_BANK_ID, memberBankId);
        BaseNetUtil.post(GET_ORDER_BUY, params, callBack);
    }

    /**
     * @param orderNo 买单号
     */
    @Override
    public void getOrderBuyDetail(String orderNo) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.get(GET_ORDER_BUYDETAIL, params, callBack);
    }

    /**
     * 获取商户转账订单信息
     *
     * @param orderNo 订单号
     */
    @Override
    public void getPayOrder(String orderNo) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.get(GET_PAY_ORDER, params, callBack);
    }

    /**
     * 会员支付商户转账订单
     *
     * @param orderNo 订单号
     * @param pass    支付密码
     */
    @Override
    public void getAgentMemberOrderPay(String orderNo, String pass) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.putStringParams(params, PAYPASSWORD, pass);
        BaseNetUtil.post(GET_ORDER_AGENTMEMBERORDERPAY, params, callBack);
    }

    /**
     * 轮询订单未读消息通知
     */
    @Override
    public void getOrderListUnReadNotice() {
        BaseNetUtil.get(GET_ORDER_LIST_UNREADNOTICE, null, callBack);
    }

    /**
     * 会员支付商户转账订单
     *
     * @param id 消息id
     */
    @Override
    public void setReadNotice(String id) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, id);
        BaseNetUtil.get(GET_ORDER_READNOTICE, params, callBack);
    }

    /**
     * 用户主动推送消息
     *
     * @param orderno
     */
    @Override
    public void setAddNoticeToOther(String orderno) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderno);
        BaseNetUtil.get(GET_ORDER_ADDNOTICE_OTHER, params, callBack);
    }

    /**
     * 获取商户存款表单
     */
    @Override
    public void orderAgentPayForm() {
        BaseNetUtil.get(ORDER_AGENTPAYFORM, null, callBack);
    }

    /**
     * 商户存款支付
     *
     * @param orderNo            订单号
     * @param amount             金额
     * @param payPassword        支付密码
     * @param agentAccount       商户账户
     * @param agentWalletAddress 商户钱包地址，跟商户账户两者必须传一个
     * @param agentUsername      商户玩家账户
     */
    @Override
    public void orderAgentMemberPay(String orderNo, String amount, String payPassword, String agentAccount, String agentWalletAddress, String agentUsername) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ORDERNO, orderNo);
        BaseNetUtil.putStringParams(params, AMOUNT, amount);
        BaseNetUtil.putStringParams(params, PAYPASSWORD, payPassword);
        BaseNetUtil.putStringParams(params, AGENTACCOUNT, agentAccount);
        BaseNetUtil.putStringParams(params, AGENTWALLETADDRESS, agentWalletAddress);
        BaseNetUtil.putStringParams(params, AGENTUSERNAME, agentUsername);
        BaseNetUtil.post(ORDER_AGENTMEMBERPAY, params, callBack);
    }

    /**
     * 根据钱包地址查找代理商信息
     *
     * @param address 钱包地址
     */
    @Override
    public void agentMemberBindInfo(String address) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ADDRESS, address);
        BaseNetUtil.get(AGENTMEMBERBIND_AGENTINFO, params, callBack);
    }

    /**
     * 根据商户id新增玩家账号
     *
     * @param agentId
     * @param walletAddress
     * @param username
     */
    @Override
    public void agentMemberAddUserName(String agentId, String walletAddress, String username) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, AGENTID, agentId);
        BaseNetUtil.putStringParams(params, WALLETADDRESS, walletAddress);
        BaseNetUtil.putStringParams(params, USER_NAME, username);
        BaseNetUtil.post(AGENTMEMBERBIND_ADDUSERNAME, params, callBack);
    }

    /**
     * 已绑定商户列表
     */
    @Override
    public void agentMemberBindHasBind() {
        BaseNetUtil.get(AGENTMEMBERBIN_HASBIND, null, callBack);
    }

    /**
     * 根据商户id删除玩家账号
     */
    @Override
    public void agentMemberDelUsername(String agentId, String username) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, AGENTID, agentId);
        BaseNetUtil.putStringParams(params, USER_NAME, username);
        BaseNetUtil.post(AGENTMEMBERBIND_DELUSERNAME, params, callBack);
    }

    /**
     * 根据商户id删除商户
     */
    @Override
    public void agentMemberDelSh(String agentId) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, AGENTID, agentId);
        BaseNetUtil.get(AGENTMEMBERBIND_DELAGENT, params, callBack);
    }

    public void getAgentMemberInfo(String account) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, ACCOUNT, account);
        BaseNetUtil.get(GET_AGENT_MEMBER_INFO, params, callBack);
    }

    public void bindAgentMember(String a, String u, String c) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, A, a);
        BaseNetUtil.putStringParams(params, U, u);
        BaseNetUtil.putStringParams(params, C, c);
        BaseNetUtil.post(BIND_AGENT_MEMBER, params, callBack);
    }

}

