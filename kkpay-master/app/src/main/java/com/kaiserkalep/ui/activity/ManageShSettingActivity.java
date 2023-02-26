package com.kaiserkalep.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.ManageShAccountAdapter;
import com.kaiserkalep.adapter.PhotoViewPopuWindow;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AddWjAccountDialogData;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.PlayerAddBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SelectBankInterface;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.widgets.MaxHeightRecyclerView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 商户管理（单一商户）
 */
public class ManageShSettingActivity extends ZZActivity implements SelectBankInterface<String> {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.rec_list)
    MaxHeightRecyclerView recList;
    @BindView(R.id.sl_setting_acount)
    ShadowLayout slSettingAcount;
    @BindView(R.id.sl_del)
    ShadowLayout slDel;

    private String title = "";
    private String agentId = "";
    private String logo = "";
    private String qrLogo = "";
    private List<String> list = new ArrayList<>();
    private ManageShAccountAdapter adapter;
    private PlayerAddBean playeraddbean;
    private PhotoViewPopuWindow photoviewpopuwindow;
    private Bitmap qrCodeBitmap;

    @Override
    public int getViewId() {
        return R.layout.activity_manageshsetting;
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    public void afterViews() {
        try {
            agentId = getStringParam(StringConstants.AGENTID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == agentId || TextUtils.isEmpty(agentId)) {
            toast(MyApp.getLanguageString(this, R.string.Share_Data_Error));
            finish();
        }
        title = MyApp.getLanguageString(getContext(), R.string.managesh_view_title);
        commTitle.init(title);
        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setOnRefreshListener(refreshLayout -> getAgentUserName(false));
        adapter = new ManageShAccountAdapter(getContext(), list, this);
        recList.setLayoutManager(new LinearLayoutManager(getContext()));
        recList.setAdapter(adapter);
        getAgentUserName(true);
    }

    @Override
    public void succeedCallBack(@Nullable String o, int position) {
        if (CommonUtils.StringNotNull(o)) {
            String content = MyApp.getLanguageString(getContext(), R.string.confirm_deletion_wj_dialog_content_Tip);
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                    new CommDialogData(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_ConfirmDelete),
                            content, MyApp.getLanguageString(getContext(), R.string.Share_cancel),
                            MyApp.getLanguageString(getContext(), R.string.Share_certain), null, v -> {
                        delWjAccount(o, position);
                    }));
        }
    }

    /**
     * 删除玩家
     *
     * @param username
     */
    private void delWjAccount(String username, int position) {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                //本地数据先移除
                if (null != list && list.size() > ZERO) {
                    list.remove(position);
                }
                if (null != adapter) {
                    adapter.changeDataUi(logo, list);
                }
                toast(MyApp.getLanguageString(ManageShSettingActivity.this, R.string.Share_delWj_Success));
                //请求
                getAgentUserName(true);
            }

        }.setNeedDialog(true).setNeedToast(true)).agentMemberDelUsername(agentId, username);
    }

    /**
     * 获取自定商家玩家账号
     */
    private void getAgentUserName(boolean needDialog) {
        new UserImpl(new ZZNetCallBack<PlayerAddBean>(this, PlayerAddBean.class) {
            @Override
            public void onSuccess(PlayerAddBean response) {
                if (null != response) {
                    if (list.size() > ZERO) {
                        list.clear();
                    }
                    playeraddbean = response;
                    logo = response.getLogo();
                    qrLogo = response.getQrCode();
                    GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(logo), ivLogo);
                    recycleBitmap();
                    PhotoViewPopuWindow.createQRCode(getContext(), qrLogo, o -> {
                        if (null != o) {
                            runOnUiThread(() -> {
                                qrCodeBitmap = o;
                                ivQrcode.setImageBitmap(qrCodeBitmap);
                            });
                        }
                    });
                    tvName.setText(response.getAgentName());
                    tvAddress.setText(response.getWalletAddress());
                    List<String> agentUsername = response.getAgentUsername();
                    if (null != agentUsername && agentUsername.size() > ZERO) {
                        list.addAll(agentUsername);
                    }
                    adapter.changeDataUi(ManageShSettingActivity.this.logo, list);
                } else {
                    toast(MyApp.getLanguageString(getContext(), R.string.Share_Data_Error));
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(needDialog).setNeedToast(true)).getAgentUserName(agentId);
    }


    @OnClick({R.id.sl_del, R.id.sl_setting_acount, R.id.iv_qrcode,R.id.tv_address,R.id.iv_address_tip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sl_del:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                        new CommDialogData(MyApp.getLanguageString(getContext(), R.string.confirm_deletion_dialog_title_Tip),
                                MyApp.getLanguageString(getContext(), R.string.confirm_deletion_sh_dialog_content_Tip),
                                MyApp.getLanguageString(getContext(), R.string.Share_cancel),
                                MyApp.getLanguageString(getContext(), R.string.Share_certain),
                                null, v -> {
                            delShAccount();
                        }));
                break;
            case R.id.sl_setting_acount:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.AddWjAccountDialog,
                        new AddWjAccountDialogData(agentId, o -> {
                            if (null != o && o) {
                                getAgentUserName(true);
                            }
                        }, null));
                break;
            case R.id.iv_qrcode:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                showPhotoViewDialog(qrLogo);
                break;
            case R.id.tv_address:
            case R.id.iv_address_tip:
                if (null != tvAddress) {
                    String string = tvAddress.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
        }
    }

    /**
     * 删除商户
     */
    private void delShAccount() {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(MyApp.getLanguageString(ManageShSettingActivity.this, R.string.Share_delSh_Success));
                ManageShSettingActivity.this.finish();
            }

        }.setNeedDialog(true).setNeedToast(true)).agentMemberDelSh(agentId);
    }

    private void reset() {
        logo = "";
        qrLogo = "";
        playeraddbean = null;
        if (list.size() > ZERO) {
            list.clear();
        }
    }

    /**
     * 释放二维码bitmap资源
     * 置空控件
     */
    private void recycleBitmap() {
        if (null != qrCodeBitmap) {
            qrCodeBitmap = null;

        }
        if (null != ivQrcode) {
            ivQrcode.setImageBitmap(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();
        release();
        recycleBitmap();
    }

    /**
     * 点击查看图片详情
     */
    private void showPhotoViewDialog(String imgUrl) {
        if (photoviewpopuwindow == null) {
            photoviewpopuwindow = new PhotoViewPopuWindow(getActivity());
        }
        if (photoviewpopuwindow.isShowing()) {
            photoviewpopuwindow.dismiss();
        } else {
            View parent = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
            photoviewpopuwindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        }
        photoviewpopuwindow.showImage(true, imgUrl);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (null != photoviewpopuwindow) {
            photoviewpopuwindow.release();
        }
    }
}
