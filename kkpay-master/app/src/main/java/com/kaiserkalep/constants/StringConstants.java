package com.kaiserkalep.constants;


/**
 * String常量
 *
 * @Auther: Administrator
 * @Date: 2019/5/9 0009 13:50
 * @Description:
 */
public interface StringConstants {

    String matches = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
    /**
     * 请求状态码   成功
     */
    String REQUEST_SUCCEED = "200";
    /**
     * 登录更换设备时，跳转效验手机号页面
     */
    String REQUEST_SUCCEED_TO_VERIFYNEW = "306";
    /**
     * 请求状态码   统一失败
     */
    String REQUEST_FAILURE = "500";
    /**
     * 请求状态码   登录失效跳转登录
     */
    String REQUEST_LOGIN_OUT = "401";
    /**
     * 请求状态码   地区限制
     */
    String REQUEST_TOKEN_OVERDUE = "403";
    /**
     * 请求状态码    其他异常
     */
    String REQUEST_OTHER_ERROR = "41414";

    /**
     * 请求状态码    更新维护
     */
    String REQUEST_UPDATE = "333";
    /**
     * 手机型号(vivo 1818)
     */
    String PHONE_MODEL = "phonemodel";
    /**
     * 系统版本(Android 8.0)
     */
    String OS_VER = "osver";
    /**
     * 设备号
     */
    String IMEI = "imei";
    /**
     * token
     */
    String X_AUTH_TOKEN = "x-auth-token";
    /**
     * 当前软件版本(1.0.0)
     */
    String VER = "ver";
    /**
     * 验证码
     */

    String TITLE = "title";
    String TITLE_SECOND = "titleSecond";
    String PARAMS = "params";
    /**
     * 设备标识
     */
    String DEVICE = "x-terminal-type";

    /**
     * url
     */
    String URL = "url";
    /**
     * 跳转
     */
    String CALLBACK = "callback";
    /**
     * 数据
     */
    String DATA = "data";
    String MSG = "msg";
    String CODE = "code";
    String TRANCEID = "tranceId";
    /**
     * app版本
     */
    String VERSION = "version";
    /**
     * 索引
     */
    String INDEX = "index";
    String TYPE = "type";
    String NAV = "nav";
    /**
     * 推送后台启动
     */
    String IS_PUSH_MESSAGE = "is_push_message";
    /**
     * 拦截接口标识
     */
    String IS_INTERCEPTOR = "INTERCEPTOR";
    String SALE_DATA = "sale_data";
    String ONLY_FINISH = "only_finish";
    String NEED_SERVICE = "need_service";
    String SALE_INDEX_ID = "saleIndexId";
    String COMM_PAGE_SIZE_2 = "18";
    String PHONE = "phone";

    String OFFSET = "offset";
    String PASSWORD = "password";
    String CONFIRMPASSWORD = "confirmPassword";
    String VALIDCODE = "validCode";
    String PAYPWD = "payPwd";
    String PWD = "pwd";
    String PHONECODE = "phoneCode";

    String IDS = "ids";
    String ID = "id";
    String CONTENT = "content";

    String ASC = "asc";//升序
    String DESC = "desc";//倒序
    String BUY = "buy";
    String SELL = "sell";
    String CREATETIME = "createTime";
    String TRADETYPE = "tradeType";
    String UPDATETIME = "updateTime";
    String ORDER_BY_COLUMN = "orderByColumn";
    String ISASC = "isAsc";
    String PAGE_SIZE = "pageSize";
    String PAGE_NUM = "pageNum";
    String NOTICE_TYPE = "noticeType";
    String STATUS = "status";
    String QRCODE_FILE = "file";
    String QRCODE = "qrcode";
    String ZFBNO = "zfbNo";
    String FILE_TYPE = "fileType";
    String WALLET_WECHAT = "Wallet_Wechat";
    String WALLET_ALIPAY = "Wallet_Alipay";
    String WALLET_BANK = "Wallet_Bank";
    String BANK_ID = "bankId";
    String BANK_NO = "bankNo";
    String PAY_TYPE = "payType";
    String PAYTYPE_BANK = "bank";
    String PAYTYPE_ALI = "ali";
    String PAYTYPE_WECHAT = "wechat";

    String AVATAR_FILE = "avatarFile";
    String AVATAR = "Avatar";
    String ACCOUNT = "account";
    String ADDRESS = "address";
    String USER_NAME = "username";

    String SELLORDERNO = "sellOrderNo";
    String ORDERNO = "orderNo";
    String BILLNO = "billNo";
    String BILLTYPE = "billType";
    String ORDERTYPE = "orderType";
    String AMOUNTTYPE = "amountType";
    String AMOUNT = "amount";
    String MONEY = "Money";
    String RECEIVETYPE = "receiveType";
    String CANSPLIT = "canSplit";
    String ISSPLIT = "isSplit";
    String PAYPROOF = "payProof";
    String PAYPASSWORD = "payPassword";
    String AMOUNT_DEAL = "amountDeal";
    String AMOUNT_CANCEL = "amountCancel";
    String AMOUNT_FREEZE = "amountFreeze";
    String AGENTACCOUNT = "agentAccount";
    String AGENTWALLETADDRESS = "agentWalletAddress";
    String AGENTUSERNAME = "agentUsername";

    String TITLETYPE = "titleType";
    String REALNAME = "realName";
    String IDENTIFYID = "identifyId";
    String FILEFRONT = "fileFront";
    String FILEREV = "fileRev";
    String NUMID = "numId";
    String VIDEO = "video";

    String LAST = "last";
    String ALL = "all";
    String AGENTID = "agentId";
    String WALLETADDRESS = "walletAddress";

    String MEMBER_BANK_ID = "memberBankId";


    String A = "a";
    String U = "u";
    String C = "c";


    String comma = ",";

    String DELETE = "delete";

}
