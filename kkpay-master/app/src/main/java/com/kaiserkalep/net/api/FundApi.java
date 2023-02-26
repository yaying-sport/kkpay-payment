package com.kaiserkalep.net.api;

import com.kaiserkalep.base.BaseNetUtil;

import static com.kaiserkalep.net.url.HomeUrl.GET_ORDER_LIST_UNREADNOTICE;

import java.util.HashMap;

/**
 * 资金相关api
 *
 * @Auther: Jack
 * @Date: 2020/8/15 11:59
 * @Description:
 */
public interface FundApi {

    /**
     * 中心钱包信息
     */
    void walletInfo();

    /**
     * 银行卡列表
     */
    void getBankList();

    /**
     * 微信更新收款码
     *
     * @param file 文件路径
     */
//    void updateWxCode(String file, String pass);

    /**
     * 支付宝更新收款码
     *
     * @param zfbNo 支付宝号
     * @param file  支付宝二维码 路径
     */
//    void updateZFB(String zfbNo, String file, String pass);

    /**
     */
    void updateBank(HashMap<String, String> params);

    /**
     * @param payType wechat：微信 ali：支付宝 bank :银行卡
     */
    void deleteWallet(String payType);

    /**
     * 我的挂单
     *
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageSize      页面大小
     * @param pageNum       页码
     * @param status        状态 0未销售，1销售部分，2完成，-1取消，传空取所有
     */
    void mySell(String orderByColumn, String isAsc, String pageSize, String pageNum, String status);

    /**
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageSize      页面大小
     * @param pageNum       页码
     * @param status        状态 1进行中，4已完成，-1已取消，3暂停中，不传取全部
     */
    void myBuyOrder(String orderByColumn, String isAsc, String pageSize, String pageNum, String status);

    /**
     * 交易大厅卖单列表
     *
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageNum       页码
     * @param pageSize      页面大小
     */
    void OrderBuyList(String orderByColumn, String isAsc, String pageNum, String pageSize);

    /**
     * 买卖币获取表单
     */
    void getOrderFrom();

    /**
     * @param orderNo     订单号
     * @param amount      金额
     * @param receiveType 支持收款方式，bank银行卡，ali支付宝，wechat微信；多个逗号分割
     * @param canSplit    是否支持拆单，0不支持，1支持
     */
    void getOrderSell(String orderNo, String amount, String receiveType, String canSplit, String bankId);

    /**
     * @param orderNo 卖单号
     */
    void getOrderSellDetail(String orderNo);

    /**
     * @param orderNo     订单号
     * @param orderType   订单类型，卖单：sell，买单：buy
     * @param status      状态，对应买卖单状态   (需要改变成什么状态，就传什么状态) 0未销售，1销售部分，2完成，-1取消，-2部分取消
     * @param payProof    支付凭证，多个逗号分隔
     * @param payPassword 支付密码
     */
    void setChangeStatus(String orderNo, String orderType, String status, String payProof, String payPassword);

    /**
     * @param orderByColumn 排序字段，canSplit：是否拆分，amount：金额
     * @param isAsc         排序类型，asc升序，desc倒序
     * @param pageSize      页面大小
     * @param pageNum       页码
     */
    void myWalletBill(String orderByColumn, String isAsc, String pageSize, String pageNum);

    /**
     * @param sellOrderNo 卖币订单号
     * @param orderNo     买币订单号
     * @param amount      购买金额
     * @param payType     支付类型
     * @param isSplit     是否拆分
     */
    void myBuy(String sellOrderNo, String orderNo, String amount, String payType, String isSplit,String memberBankId);

    /**
     * @param orderNo 买单号
     */
    void getOrderBuyDetail(String orderNo);

    /**
     * 获取商户转账订单信息
     *
     * @param orderNo 订单号
     */
    void getPayOrder(String orderNo);

    /**
     * 会员支付商户转账订单
     *
     * @param orderNo 订单号
     * @param pass    支付密码
     */
    void getAgentMemberOrderPay(String orderNo, String pass);

    /**
     * 轮询订单未读消息通知
     */
    void getOrderListUnReadNotice();

    /**
     * 会员支付商户转账订单
     *
     * @param id 消息id
     */
    void setReadNotice(String id);

    /**
     * 用户主动推送消息
     *
     * @param orderno
     */
    void setAddNoticeToOther(String orderno);

    /**
     * 获取商户存款表单
     */
    void orderAgentPayForm();

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
    void orderAgentMemberPay(String orderNo, String amount, String payPassword, String agentAccount, String agentWalletAddress, String agentUsername);

    /**
     * 根据钱包地址查找代理商信息
     *
     * @param address 钱包地址
     */
    void agentMemberBindInfo(String address);

    /**
     * 根据商户id新增玩家账号
     *
     * @param agentId       代理商id
     * @param walletAddress 代理商地址
     * @param username      玩家名
     */
    void agentMemberAddUserName(String agentId, String walletAddress, String username);

    /**
     * 已绑定商户列表
     */
    void agentMemberBindHasBind();

    /**
     * 根据商户id删除玩家账号
     */
    void agentMemberDelUsername(String agentId, String username);

    /**
     * 根据商户id删除商户
     */
    void agentMemberDelSh(String agentId);

}
