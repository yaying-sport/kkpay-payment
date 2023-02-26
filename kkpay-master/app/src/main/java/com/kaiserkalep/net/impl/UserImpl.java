package com.kaiserkalep.net.impl;

import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.CallbackBase;
import com.kaiserkalep.base.NetManagerBase;
import com.kaiserkalep.net.api.UserApi;

import java.util.HashMap;

import static com.kaiserkalep.constants.StringConstants.*;
import static com.kaiserkalep.net.url.HomeUrl.*;

/**
 * 用户相关api
 *
 * @Auther: Jack
 * @Date: 2019/12/16 15:42
 * @Description:
 */
public class UserImpl extends NetManagerBase implements UserApi {


    public UserImpl(CallbackBase callBack) {
        super(callBack);
    }

    /**
     * 滑块验证码
     *
     * @param phone 用户账号
     */
    @Override
    public void captcha(String phone) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phone);
        BaseNetUtil.post(GET_MEMBER_CAPTCHA, params, callBack);
    }

    /**
     * 校验滑块验证
     *
     * @param phone  用户账户
     * @param offset 滑动距离
     */
    @Override
    public void captchaCheck(String phone, String offset) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phone);
        BaseNetUtil.putStringParams(params, OFFSET, offset);
        BaseNetUtil.post(GET_MEMBER_CAPTCHA_CHECK, params, callBack);
    }

    /**
     * 登录
     *
     * @param phone     账号
     * @param password  密码
     * @param validCode 验证码
     */
    @Override
    public void login(String phone, String password, String validCode) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phone);
        BaseNetUtil.putStringParams(params, PASSWORD, password);
        BaseNetUtil.putStringParams(params, VALIDCODE, validCode);
        BaseNetUtil.post(LOGIN, params, callBack);
    }

    /**
     * 注册
     *
     * @param phone           账号
     * @param password        密码
     * @param confirmPassword 确认密码
     * @param validCode       验证码
     * @param payPwd          支付密码
     * @param phoneCode       手机验证码
     */
    @Override
    public void register(String phone, String password, String confirmPassword, String validCode, String payPwd, String phoneCode) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phone);
        BaseNetUtil.putStringParams(params, PASSWORD, password);
        BaseNetUtil.putStringParams(params, CONFIRMPASSWORD, confirmPassword);
        BaseNetUtil.putStringParams(params, VALIDCODE, validCode);
        BaseNetUtil.putStringParams(params, PAYPWD, payPwd);
        BaseNetUtil.putStringParams(params, PHONECODE, phoneCode);
        BaseNetUtil.post(REGISTER, params, callBack);
    }

    /**
     * 找回登录密码
     *
     * @param phone           账户名
     * @param password        新密码
     * @param confirmPassword 确认新密码
     * @param validCode       滑动验证码
     * @param phoneCode       手机验证码
     */
    @Override
    public void retrieveLoginPassword(String phone, String password, String confirmPassword, String validCode, String phoneCode) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phone);
        BaseNetUtil.putStringParams(params, PASSWORD, password);
        BaseNetUtil.putStringParams(params, CONFIRMPASSWORD, confirmPassword);
        BaseNetUtil.putStringParams(params, VALIDCODE, validCode);
        BaseNetUtil.putStringParams(params, PHONECODE, phoneCode);
        BaseNetUtil.post(RETRIEVE_LOGIN_PASSWORD, params, callBack);
    }

    /**
     * 修改登录密码
     *
     * @param pwd             当前密码
     * @param password        新密码
     * @param confirmPassword 确认密码
     */
    @Override
    public void updateMemberUpdateLoginPwd(String pwd, String password, String confirmPassword) {
        HashMap<String, String> postParams = new HashMap<>();
        BaseNetUtil.putStringParams(postParams, PWD, pwd);
        BaseNetUtil.putStringParams(postParams, PASSWORD, password);
        BaseNetUtil.putStringParams(postParams, CONFIRMPASSWORD, confirmPassword);
        BaseNetUtil.post(UPDATE_LOGINPWD_URL, postParams, callBack);
    }

    /**
     * 修改支付密码
     *
     * @param pwd             当前密码
     * @param password        新密码
     * @param confirmPassword 确认密码
     */
    @Override
    public void updateMemberUpdatePayPwd(String pwd, String password, String confirmPassword) {
        HashMap<String, String> postParams = new HashMap<>();
        BaseNetUtil.putStringParams(postParams, PWD, pwd);
        BaseNetUtil.putStringParams(postParams, PASSWORD, password);
        BaseNetUtil.putStringParams(postParams, CONFIRMPASSWORD, confirmPassword);
        BaseNetUtil.post(UPDATE_PAYPWD_URL, postParams, callBack);
    }

    /**
     * 退出登录
     */
    @Override
    public void loginOut() {
        BaseNetUtil.get(LOGIN_OUT, null, callBack);
    }

    /**
     * 上传头像
     *
     * @param filePath
     */
    @Override
    public void uploadAvatar(String filePath) {

    }

    /**
     * 发送验证码
     *
     * @param phoneNo 手机号
     */
    @Override
    public void sendAuthCode(String phoneNo) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phoneNo);
        BaseNetUtil.post(SEND_AUTH_CODE, params, callBack);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    @Override
    public void sendCheckPhone(String phone, String code) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, PHONE, phone);
        BaseNetUtil.putStringParams(params, CODE, code);
        BaseNetUtil.post(SEND_CHECK_PHONE_CODE, params, callBack);
    }

    /**
     * 会员基础信息
     */
    @Override
    public void getUserInfo() {
        BaseNetUtil.post(GET_USER_INFO, null, callBack);
    }

    /**
     * 会员基础信息
     */
    @Override
    public void getAmountInfo() {
        BaseNetUtil.post(GET_AMOUNT_INFO, null, callBack);
    }

    /**
     * 修改头像和用户名
     *
     * @param filePath 头像地址
     * @param nickname 昵称
     */
    @Override
    public void setAvatarNickName(String filePath, String nickname) {
        HashMap<String, String> params = new HashMap<>();
        params.put(AVATAR_FILE, filePath);
        params.put(USER_NAME, nickname);
        BaseNetUtil.post(UPLOAD_AVATAR_USERNAME, params, callBack);
    }

    /**
     * 上传二维码
     *
     * @param filePath 文件路径
     */
    @Override
    public void uploadQrCode(String type, String filePath) {
        HashMap<String, String> params = new HashMap<>();
        params.put(TYPE, type);
        params.put(QRCODE_FILE, filePath);
        BaseNetUtil.postUpLoadFile(WALLET_UPLOADFILE, params, callBack);
    }

    /**
     * 实名认证 第一步
     *
     * @param realName   真实姓名
     * @param identifyId 身份认证
     * @param fileFront  身份证正面 （File 类型）
     * @param fileRev    身份证反面 （File 类型）
     */
    @Override
    public void uploadidentifyid(String realName, String identifyId, String fileFront, String fileRev) {
        HashMap<String, String> params = new HashMap<>();
        params.put(REALNAME, realName);
        params.put(IDENTIFYID, identifyId);
        params.put(FILEFRONT, fileFront);
        params.put(FILEREV, fileRev);
        BaseNetUtil.post(GET_UPLOADIDENTIFYID, params, callBack);
    }

    /**
     * 获取视频阅读数字
     */
    @Override
    public void getFindReadNum() {
        BaseNetUtil.get(GET_FINDREADNUM, null, callBack);
    }

    /**
     * 视频上传保存接口
     */
    @Override
    public void setUpdateVideo(String numid, String video) {
        HashMap<String, String> params = new HashMap<>();
        params.put(NUMID, numid);
        params.put(VIDEO, video);
        BaseNetUtil.post(VERFIY_UPDATEVIDEO, params, callBack);
    }

    /**
     * 获取商家通讯录
     * 类型，last最近，all全部
     *
     * @param type
     */
    @Override
    public void getAgentbook(String type) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, TYPE, type);
        BaseNetUtil.get(AGENTMEMBERBIND_BOOK, params, callBack);
    }

    /**
     * 商户id
     *
     * @param agentId
     */
    @Override
    public void getAgentUserName(String agentId) {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.putStringParams(params, AGENTID, agentId);
        BaseNetUtil.get(AGENTMEMBERBIND_AGENTUSERNAME, params, callBack);
    }


    //
    public void getPayModeList() {
        BaseNetUtil.get(MEMBER_BANK_LIST, null, callBack);
    }


    public void getIdCardInfo(String frontUrl, String revUrl) {
        HashMap<String, String> params = new HashMap<>();
        params.put(FILEFRONT, frontUrl);
        params.put(FILEREV, revUrl);
        BaseNetUtil.post(CHECK_IDCARD_INFO, params, callBack);
    }

    public void getFaceParam(String realName, String identifyId, String frontUrl,String reverseUrl) {
        HashMap<String, String> params = new HashMap<>();
        params.put(REALNAME, realName);
        params.put(IDENTIFYID, identifyId);
        params.put(FILEFRONT, frontUrl);
        params.put(FILEREV, reverseUrl);
        BaseNetUtil.post(GET_FACE_ID, params, callBack);
    }

    public void getSignParam() {
        HashMap<String, String> params = new HashMap<>();
        BaseNetUtil.get(GET_SIGN_ID, params, callBack);
    }

    public void queryFaceRecord(String orderNo) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ORDERNO, orderNo);
        BaseNetUtil.get(QUERY_FACE_RECORD, params, callBack);
    }

}
