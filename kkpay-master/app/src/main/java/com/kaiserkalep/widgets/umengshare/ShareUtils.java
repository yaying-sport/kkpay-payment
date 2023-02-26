package com.kaiserkalep.widgets.umengshare;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.kaiserkalep.R;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.toast.ToastUtils;

import java.util.List;

/**
 * qq/微信分享
 *
 * @Auther: Administrator
 * @Date: 2019/3/23 0023 13:52
 * @Description:
 */
public class ShareUtils {


    /**
     * a、帖子分享信息：“您的好友给您分享了水心XXXX链接，赶紧去看看吧！【请复制链接用浏览器打开】【六合头条】【邀请码XXXX】”
     * 邀请好友分享：“您的好友邀请您加入六合头条XXXX链接，和朋友一起畅聊心水赢大奖！【请复制链接用浏览器打开】【六合头条】【邀请码XXXX】”
     */

    public static ShareUtils getInstance() {
        return Holder.INSTANCE;
    }


    private ShareUtils() {

    }

    private static class Holder {
        private static ShareUtils INSTANCE = new ShareUtils();
    }

    /**
     * 分享,复制口令
     *
     * @param activity
     * @param content
     */
    public void doInvitationShareText(final Activity activity, final String content,
                                      final ShareDialog uMShareDialog, final SucceedCallBackListener<Integer> listener) {
//        uMShareDialog.setOnListener(v -> {
//            int id = v.getId();
//            if (id == R.id.custom_share_wechat_view) { //微信
//                CommonUtils.copyText(activity.getApplicationContext(), content, false);
//                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CopyWordDialog,
//                        new CopyWordDialogData(content, 1, listener));
//            } else if (id == R.id.custom_share_qq_view) {//QQ好友
//                CommonUtils.copyText(activity.getApplicationContext(), content, false);
//                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CopyWordDialog,
//                        new CopyWordDialogData(content, 0, listener));
//            } else if (id == R.id.custom_share_copy_view) {//分享链接
//                CommonUtils.copyText(activity.getApplicationContext(), content);
//            } else if (id == R.id.custom_share_cancel_view) {
//                //取消关闭dialog
//            }
//            if (uMShareDialog.isShowing()) {
//                uMShareDialog.dismiss();
//            }
//            if ((id == R.id.custom_share_copy_view ||
//                    id == R.id.custom_share_cancel_view) && listener != null) {//分享链接立即回调
//                listener.succeedCallBack(0);
//            }
//        });
//        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.ShareDialog,
//                new UMShareDialogData(uMShareDialog));
    }

    /**
     * 分享文本
     *
     * @param activity
     * @param content
     */
    public void doCustomShareText(final Activity activity, final String content,
                                  final ShareDialog uMShareDialog, final SucceedCallBackListener<Integer> listener) {
        uMShareDialog.setOnListener(v -> {
            int id = v.getId();
            if (id == R.id.custom_share_wechat_view) { //微信
                CommonUtils.copyText(activity.getApplicationContext(), content, false);
                shareWechatFriend(activity.getApplicationContext(), content);
            } else if (id == R.id.custom_share_qq_view) {//QQ好友
                CommonUtils.copyText(activity.getApplicationContext(), content, false);
                shareQQ(activity.getApplicationContext(), content);
            } else if (id == R.id.custom_share_copy_view) {//分享链接
                CommonUtils.copyText(activity.getApplicationContext(), content);
            } else if (id == R.id.custom_share_cancel_view) {
                //取消关闭dialog
            }
            if (id == R.id.custom_share_cancel_view && listener != null) {
                listener.succeedCallBack(0);
            }
            if (uMShareDialog.isShowing()) {
                uMShareDialog.dismiss();
            }
        });
//        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.ShareDialog,
//                new UMShareDialogData(uMShareDialog));
    }

    /**
     * 直接分享纯文本内容至QQ好友
     *
     * @param mContext
     * @param content
     */
    public void shareQQ(Context mContext, String content) {
        if (PlatformUtil.isInstallApp(mContext, PlatformUtil.PACKAGE_MOBILE_QQ)) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            try {
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.show("您需要安装QQ客户端");
            }
        } else {
            ToastUtils.show("您需要安装QQ客户端");
        }
    }

    /**
     * 分享图片给QQ好友
     *
     * @param bitmap
     */
    public void shareImageToQQ(Context mContext, Bitmap bitmap) {
        if (PlatformUtil.isInstallApp(mContext, PlatformUtil.PACKAGE_MOBILE_QQ)) {
            try {
                Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
                        mContext.getContentResolver(), bitmap, null, null));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("image/*");
                // 遍历所有支持发送图片的应用。找到需要的应用
                ComponentName componentName = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");

                shareIntent.setComponent(componentName);
                // mContext.startActivity(shareIntent);
                mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
            } catch (Exception e) {
//            ContextUtil.getInstance().showToastMsg("分享图片到**失败");
            }
        }
    }

    /**
     * 直接分享文本到微信好友
     *
     * @param context 上下文
     */
    public void shareWechatFriend(Context context, String content) {
        if (PlatformUtil.isInstallApp(context, PlatformUtil.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
//            intent.putExtra("sms_body", content);
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.show("您需要安装微信客户端");
            }
        } else {
            ToastUtils.show("您需要安装微信客户端");
        }
    }


    static class PlatformUtil {
        public static final String PACKAGE_WECHAT = "com.tencent.mm";
        public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
        public static final String PACKAGE_QZONE = "com.qzone";
        public static final String PACKAGE_SINA = "com.sina.weibo";

        // 判断是否安装指定app
        public static boolean isInstallApp(Context context, String app_package) {
            final PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
            if (pInfo != null) {
                for (int i = 0; i < pInfo.size(); i++) {
                    String pn = pInfo.get(i).packageName;
                    if (app_package.equals(pn)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}

