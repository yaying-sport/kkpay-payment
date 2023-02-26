package com.kaiserkalep.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.DialogCommBase;
import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.toast.ToastUtils;
import com.kaiserkalep.utils.update.listener.UpdateDialogListener;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;


/**
 * 版本更新弹窗
 *
 * @Auther: Administrator
 * @Date: 2019/5/18 0018 19:36
 * @Description:
 */
public class UpdateDialog extends DialogCommBase implements View.OnClickListener {

    private TextView tvOk, tvCancel, tvVersion_name, updateMessage, tvProgressValue,tvIntent;
    private ProgressBar progressBar;
    private View progressContent;
    private boolean force_update = true;//默认强更
    private ShadowLayout slUpdate;
    //    private String down_url;
    private UpdateDate updateDate;


    public UpdateDialog(Context context) {
        super(context, R.style.SignInDialog);
    }


    @SuppressLint("WrongViewCast")
    @Override
    public void initView() {
        setView(R.layout.dialog_edition_update);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        slUpdate = findViewById(R.id.sl_update);
        slUpdate.setOnClickListener(this);
        slUpdate.setClickable(true);
        tvOk = findViewById(R.id.tv_confirm_text);
        tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        updateMessage = findViewById(R.id.update_message);
        tvVersion_name = findViewById(R.id.tv_version_name);
        progressContent = findViewById(R.id.rl_progress_content);
        tvProgressValue = findViewById(R.id.tv_progress_value);
        progressBar = findViewById(R.id.progressBar);
        tvIntent = findViewById(R.id.tv_intent);
        tvIntent.setOnClickListener(this);
    }


    /**
     * 设置数据
     *
     * @param updateDate
     * @param updateDialogListener
     */
    public void setData(UpdateDate updateDate, UpdateDialogListener updateDialogListener) {
        if (updateDate != null) {
            this.updateDate = updateDate;
//            down_url = updateDate.getUrl();
            SPUtil.setUpdateMd5(updateDate.getMd5());
            SPUtil.setDownBrowserUrl(updateDate.getDownBrowserUrl());
            this.updateDialogListener = updateDialogListener;
            String massageList = updateDate.getContent();
            updateMessage.setText(CommonUtils.StringNotNull(massageList) ? massageList : "");
            String version = updateDate.getVersion();
            tvVersion_name.setText("V" + version);
            force_update = updateDate.isForce_update();
            initStatus();
            super.show();
        }
    }

    /**
     * 设置初始按钮状态
     */
    public void initStatus() {
        if (force_update) {//强更
            setForceUpdate();
        } else {//非强更
            setNormalUpdate();
        }
    }

    int status = -1;//0,强更  1,非强更  2,下载中  3,下载暂停  4,下载失败

    /**
     * 设置强更
     */
    public void setForceUpdate() {
        if (status != 0) {
            status = 0;
            slUpdate.setVisibility(View.VISIBLE);
            tvOk.setText(MyApp.getLanguageString(getContext(), R.string.UpdateDialog_NowUpData));
            progressContent.setVisibility(View.GONE);
            setCancel("", false);
        }
    }


    /**
     * 设置普通更新
     */
    public void setNormalUpdate() {
        if (status != 1) {
            status = 1;
            slUpdate.setVisibility(View.VISIBLE);
            tvOk.setText(MyApp.getLanguageString(getContext(), R.string.UpdateDialog_NowUpData));
            progressContent.setVisibility(View.GONE);
            setCancel(MyApp.getLanguageString(getContext(), R.string.UpdateDialog_TellLater), true);
        }
    }

    private void setCancel(String text, boolean isVisible) {
        tvCancel.setText(text);
        ViewGroup.LayoutParams layoutParams = tvCancel.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = UIUtils.dip2px(isVisible ? 37 : 20);
            tvCancel.setLayoutParams(layoutParams);
        }
        tvCancel.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 更新下载的进度
     *
     * @param currentProgress 当前进度
     */
    public void setProgress(int currentProgress) {
        if (status != 2) {
            status = 2;
            progressContent.setVisibility(View.VISIBLE);
            slUpdate.setVisibility(View.GONE);
            setCancel(force_update ? "" : MyApp.getLanguageString(getContext(), R.string.UpdateDialog_BackgroundUpdate), !force_update);
        }
        if (currentProgress >= 0) {
            tvProgressValue.setText(currentProgress + "%");
            if (progressBar != null) {
                progressBar.setProgress(currentProgress);
            }
        }
    }

    /**
     * 更新暂停,包括手动暂停和网络错误
     */
    public void setReUpdate() {
        if (status != 3) {
            status = 3;
            setReUpdateTwo();
//            setReUpdateOne();
        }
    }

    private void setReUpdateTwo() {
        tvProgressValue.setText(MyApp.getLanguageString(getContext(), R.string.UpdateDialog_DownloadPaused));
        progressContent.setVisibility(View.VISIBLE);
        slUpdate.setVisibility(View.GONE);
        setCancel(force_update ? "" : MyApp.getLanguageString(getContext(), R.string.UpdateDialog_BackgroundUpdate), !force_update);
    }

//    private void setReUpdateOne() {
//        tvOk.setVisibility(View.VISIBLE);
//        Context context = getContext();
//        if (NetWorkUtils.isNetworkConnected()) {//有网暂停
//            tvOk.setText("继续更新");
//            tvOk.setOnClickListener(v -> {//跳转下载管理器
//                Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//                viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(viewDownloadIntent);
//            });
//        } else {//无网设置
//            tvOk.setText("前往设置");
//            ToastUtils.show("当前网络异常,请检查网络后重试");
//            tvOk.setOnClickListener(v -> {
//                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
//            });
//        }
//        progressContent.setVisibility(View.GONE);
//        if (force_update) {
//            tvCancel.setVisibility(View.GONE);
//        } else {
//            tvCancel.setVisibility(View.VISIBLE);
//            tvCancel.setText("以后再说");
//        }
//    }

    /**
     * 更新错误,去浏览器
     */
    public void setErrorUpdate() {
        if (status != 4) {
            status = 4;
            slUpdate.setVisibility(View.VISIBLE);
            tvOk.setText(MyApp.getLanguageString(getContext(), R.string.UpdateDialog_BrowserDownload));
            ToastUtils.show(MyApp.getLanguageString(getContext(), R.string.UpdateDialog_BrowserDownload_Fails));
            slUpdate.setOnClickListener(v -> {
                if (updateDialogListener != null) {
                    updateDialogListener.downFromBrowser();
                }
            });
            progressContent.setVisibility(View.GONE);
            setCancel(force_update ? "" : MyApp.getLanguageString(getContext(), R.string.UpdateDialog_TellLater), !force_update);
        }
    }

    /**
     * 监听接口
     */
    private UpdateDialogListener updateDialogListener;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sl_update:
                if (updateDialogListener != null) {
                    updateDialogListener.updateDownLoad();
                }
//                if (!force_update && isShowing()) {//非强更关闭弹框
//                    dismiss();
//                }
//                IntentUtils.gotoDefaultWeb(getInterface(), down_url);
                break;
            case R.id.tv_cancel:
                if (isShowing()) {
                    dismiss();
                }
                break;
            case R.id.tv_intent://跳转网页
                if (updateDialogListener != null) {
                    updateDialogListener.downFromBrowser();
                }
                break;
        }
    }

    public void clickUpdate() {
        if (isShowing() && slUpdate != null) {
            slUpdate.performClick();
        }
    }

    public void cancelUpdate() {
        if (updateDialogListener != null) {
            updateDialogListener.cancelUpdate();
        }
    }

    public UpdateDate getUpdateDate() {
        return updateDate;
    }
}
