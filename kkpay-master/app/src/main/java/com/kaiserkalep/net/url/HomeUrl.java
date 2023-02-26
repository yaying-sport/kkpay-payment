package com.kaiserkalep.net.url;

/**
 * api接口
 *
 * @Auther: Jack
 * @Date: 2019/12/16 17:00
 * @Description:
 */
public interface HomeUrl {


    /**
     * 拼接路径
     */
    String JOINT = "/app";

/*****************************************************************************************************/
    /**
     * 启动页广告
     */
    String GET_WEBSITE_START_AD = JOINT + "/member/adList";
    /**
     * 获取用户须知
     */
    String GET_ABOUT = JOINT + "/member/about";
    /**
     * 收藏游戏
     */
    String GET_VERSION_UPDATE = JOINT + "/version/checkUpdate";
    /**
     * 获取当前的服务器时间
     */
    String GET_SERVICE_DATE = JOINT + "/center/getCurrentDate";
    /**
     * 获取滑块验证
     */
    String GET_MEMBER_CAPTCHA = JOINT + "/member/captcha/create";
    /**
     * 滑块验证
     */
    String GET_MEMBER_CAPTCHA_CHECK = JOINT + "/member/captcha/check";
    /**
     * 发送验证码
     */
    String SEND_AUTH_CODE = JOINT + "/member/captcha/sendPhoneCode";
    /**
     * 效验手机号登录
     */
    String SEND_CHECK_PHONE_CODE = JOINT + "/member/checkPhone";
    /**
     * 登录
     */
    String LOGIN = JOINT + "/member/login";
    /**
     * 注册
     */
    String REGISTER = JOINT + "/member/register";
    /**
     * 登录退出
     */
    String LOGIN_OUT = JOINT + "/member/logout";
    /**
     * 找回登录密码
     */
    String RETRIEVE_LOGIN_PASSWORD = JOINT + "/member/forgetPwd";
    /**
     * 修改登录密码
     */
    String UPDATE_LOGINPWD_URL = JOINT + "/center/modifyLoginPwd";
    /**
     * 修改支付密码
     */
    String UPDATE_PAYPWD_URL = JOINT + "/center/modifyPayPwd";
    /**
     * 获取客服链接
     */
    String GET_SERVICE_URL = JOINT + "/center/findCustomerLink";
    /**
     * 会员基础信息
     */
    String GET_USER_INFO = JOINT + "/center/information";
    /**
     * 会员余额信息
     */
    String GET_AMOUNT_INFO = JOINT + "/center/amountInfo";
    /**
     * 银行卡列表
     */
    String GET_BANKLIST = JOINT + "/center/findAllBank";
    /**
     * 文件上传公共接口
     */
    String WALLET_UPLOADFILE = JOINT + "/identify/uploadFile";
    /**
     * 修改头像和用户名
     */
    String UPLOAD_AVATAR_USERNAME = JOINT + "/center/modifyUserName";
    /**
     * 微信更新收款码
     */
//    String GET_WALLET_UPDATEWXCODE = JOINT + "/identify/updateWxCode";
    /**
     * 支付宝更新收款码
     */
//    String GET_WALLET_UPDATEZFB = JOINT + "/identify/updateZFB";
    /**
     * 添加银行卡
     */
    String GET_WALLET_ADDBANK = JOINT + "/memberBank/add";
    /**
     * 删除收款方式
     */
    String GET_WALLET_DELETE = JOINT + "/memberBank/del";
    /**
     * 消息中心（公告/通知）列表
     */
    String SYS_NOTICE_LIST = JOINT + "/notice/list";
    /**
     * 消息标记已读
     */
    String MSG_READ_TAG = JOINT + "/notice/msg";
    /**
     * 删除通知/公告
     */
    String SYS_NOTICE_DELETE = JOINT + "/notice/deleteOdd";
    /**
     * 获取未读消息数量
     */
    String SYS_NOTICE_UNREAD_NUM = JOINT + "/notice/getUnreadNum";
    /**
     * 获取意见反馈未读消息数量
     */
    String SYS_FEEDBACK_UNREAD_NUM = JOINT + "/feedback/countUnread";
    /**
     * 我的挂单
     */
    String GET_MY_SELL = JOINT + "/order/mySell";
    /**
     * 我的订单
     */
    String GET_MY_BUYORDER = JOINT + "/order/myBuy";
    /**
     * 交易大厅卖单列表
     */
    String GET_ORDER_BUY_LIST = JOINT + "/order/hall";
    /**
     * 买卖币获取表单
     */
    String GET_ORDER_FROM = JOINT + "/order/form";
    /**
     * 卖币
     */
    String GET_ORDER_SELL = JOINT + "/order/sell";
    /**
     * 卖单详情
     */
    String GET_ORDER_SELLDETAIL = JOINT + "/order/sellDetail";
    /**
     * 修改订单状态
     */
    String GET_ORDER_CHANGESTATUS = JOINT + "/order/changeStatus";
    /**
     * 钱包记录
     */
    String GET_MY_WALLET_BILL = JOINT + "/order/bill";
    /**
     * 买单
     */
    String GET_ORDER_BUY = JOINT + "/order/buy";
    /**
     * 买单详情
     */
    String GET_ORDER_BUYDETAIL = JOINT + "/order/buyDetail";
    /**
     * 获取商户转账订单信息
     */
    String GET_PAY_ORDER = JOINT + "/order/getpay";
    /**
     * 会员支付商户转账订单
     */
    String GET_ORDER_AGENTMEMBERORDERPAY = JOINT + "/order/agentMemberOrderPay";

    /**
     * 轮询订单未读消息通知
     */
    String GET_ORDER_LIST_UNREADNOTICE = JOINT + "/order/unReadNotice";

    /**
     * 订单消息设置已读
     */
    String GET_ORDER_READNOTICE = JOINT + "/order/readNotice";

    /**
     * 用户主动推送消息
     */
    String GET_ORDER_ADDNOTICE_OTHER = JOINT + "/order/addNotice";

    /**
     * 意见反馈标题
     */
    String GET_FEEDTITLE = JOINT + "/feedback/feedTitle";

    /**
     * 新增意见反馈
     */
    String GET_FEEDBACK_ADD = JOINT + "/feedback/add";

    /**
     * 意见反馈列表
     */
    String GET_MY_FEEDBACK_LIST = JOINT + "/feedback/list";

    /**
     * 意见反馈列表详情
     */
    String GET_FEEDBACK_DETAIL = JOINT + "/feedback/searchId";

    /**
     * 会员回复反馈列表
     */
    String GET_FEEDBACK_REPLY = JOINT + "/feedback/reply";
    /**
     * 弹窗公告列表
     */
    String GET_ALERT_NOTICE_LIST = JOINT + "/notice/getAlertList";

    /**
     * 实名认证 第一步
     */
    String GET_UPLOADIDENTIFYID = JOINT + "/identify/uploadIdentifyID";

    /**
     * 获取视频阅读数字
     */
    String GET_FINDREADNUM = JOINT + "/identify/findReadNum";

    /**
     * 视频上传保存接口
     */
    String VERFIY_UPDATEVIDEO = JOINT + "/identify/updateVideo";

    /**
     * 商家通讯录
     */
    String AGENTMEMBERBIND_BOOK = JOINT + "/agentMemberBind/book";

    /**
     * 查询商户信息以及已添加玩家账号
     */
    String AGENTMEMBERBIND_AGENTUSERNAME = JOINT + "/agentMemberBind/agentUsername";

    /**
     * 获取商户存款表单
     */
    String ORDER_AGENTPAYFORM = JOINT + "/order/agentPayForm";

    /**
     * 获取商户存款支付
     */
    String ORDER_AGENTMEMBERPAY = JOINT + "/order/agentMemberPay";

    /**
     * 根据钱包地址查找代理商信息
     */
    String AGENTMEMBERBIND_AGENTINFO = JOINT + "/agentMemberBind/agentInfo";

    /**
     * 根据商户id新增玩家账号
     */
    String AGENTMEMBERBIND_ADDUSERNAME = JOINT + "/agentMemberBind/addUsername";

    /**
     * 已绑定商户列表
     */
    String AGENTMEMBERBIN_HASBIND = JOINT + "/agentMemberBind/hasBind";

    /**
     * 根据商户id删除玩家账号
     */
    String AGENTMEMBERBIND_DELUSERNAME = JOINT + "/agentMemberBind/delUsername";

    /**
     * 根据商户id删除商户
     */
    String AGENTMEMBERBIND_DELAGENT = JOINT + "/agentMemberBind/delAgent";


    /**
     * 帮助页教程
     */
    String HELP_PAGE_TUTORIAL = "/?nav=1";

    /**
     * 获取已绑定银行卡列表
     */
    String MEMBER_BANK_LIST = JOINT + "/memberBank/list";
    //获取商户信息
    String GET_AGENT_MEMBER_INFO = JOINT + "/agentMemberBind/agentInfoByAccount";
    //绑定商户信息
    String BIND_AGENT_MEMBER = JOINT + "/agentMemberBind/bindWallet";
    //绑定商户信息
    String CHECK_IDCARD_INFO = JOINT + "/identify/checkIdCardInformation";
    //上送身份信息
    String GET_FACE_ID = JOINT + "/identify/getfaceid";
    String GET_SIGN_ID = JOINT + "/identify/geteNonceSign";
    String QUERY_FACE_RECORD = JOINT + "/identify/queryfacerecord";
}
