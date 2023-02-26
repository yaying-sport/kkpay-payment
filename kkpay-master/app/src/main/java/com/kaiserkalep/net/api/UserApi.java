package com.kaiserkalep.net.api;

import com.kaiserkalep.base.BaseNetUtil;

import static com.kaiserkalep.net.url.HomeUrl.GET_FINDREADNUM;

/**
 * 用户相关api
 *
 * @Auther: Jack
 * @Date: 2019/12/16 15:41
 * @Description:
 */
public interface UserApi {

    /**
     * 滑块验证码
     *
     * @param phone 用户账号
     */
    void captcha(String phone);


    /**
     * 校验滑块验证
     *
     * @param codeNonce 随机数
     * @param offset    滑动距离
     */
    void captchaCheck(String codeNonce, String offset);

    /**
     * 登录
     *
     * @param phone     账号
     * @param password  密码
     * @param validCode 验证码
     */
    void login(String phone, String password, String validCode);

    /**
     * 注册
     *
     * @param username        账号
     * @param password        密码
     * @param confirmPassword 确认密码
     * @param phone           手机号
     * @param validCode       验证码
     * @param codeNonce       随机数
     */
    void register(String username, String password, String confirmPassword,
                  String phone, String validCode, String codeNonce);

    /**
     * 找回登录密码
     *
     * @param phone           账户名
     * @param password        新密码
     * @param confirmPassword 确认新密码
     * @param validCode       滑动验证码
     * @param phoneCode       手机验证码
     */
    void retrieveLoginPassword(String phone, String password, String confirmPassword, String validCode, String phoneCode);

    /**
     * 修改登录密码
     *
     * @param pwd             当前密码
     * @param password        新密码
     * @param confirmPassword 确认密码
     */
    void updateMemberUpdateLoginPwd(String pwd, String password, String confirmPassword);

    /**
     * 修改支付密码
     *
     * @param pwd             当前密码
     * @param password        新密码
     * @param confirmPassword 确认密码
     */
    void updateMemberUpdatePayPwd(String pwd, String password, String confirmPassword);

    /**
     * 退出登录
     */
    void loginOut();

    /**
     * 上传头像
     *
     * @param filePath 文件路径
     */
    void uploadAvatar(String filePath);

    /**
     * 发送验证码
     *
     * @param phoneNo 手机号
     */
    void sendAuthCode(String phoneNo);

    /**
     * 效验手机号等级
     *
     * @param phone 手机号
     * @param code  验证码
     */
    void sendCheckPhone(String phone, String code);

    /**
     * 会员基础信息
     */
    void getUserInfo();

    /**
     * 会员余额信息
     */
    void getAmountInfo();

    /**
     * 修改头像和用户名
     *
     * @param filePath 头像地址
     * @param nickname 昵称
     */
    void setAvatarNickName(String filePath, String nickname);

    /**
     * 文件上传公共接口
     *
     * @param filePath
     */
    void uploadQrCode(String type, String filePath);

    /**
     * 实名认证 第一步
     *
     * @param realName   真实姓名
     * @param identifyId 身份认证
     * @param fileFront  身份证正面 （File 类型）
     * @param fileRev    身份证反面 （File 类型）
     */
    void uploadidentifyid(String realName, String identifyId, String fileFront, String fileRev);

    /**
     * 获取视频阅读数字
     */
    void getFindReadNum();

    /**
     * 视频上传保存接口
     */
    void setUpdateVideo(String numid, String video);

    /**
     * 获取商家通讯录
     * 类型，last最近，all全部
     *
     * @param type
     */
    void getAgentbook(String type);

    /**
     * 商户id
     *
     * @param agentId
     */
    void getAgentUserName(String agentId);

}
