package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.SimpleDialogData;
import com.kaiserkalep.bean.SysNoticeListData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.DateUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;

import butterknife.BindView;

/**
 * 消息/通知/公告详情
 *
 * @Auther: Jack
 * @Date: 2020/12/19 19:48
 * @Description:
 */
public class MessageNotifyDetailActivity extends ZZActivity implements View.OnClickListener {


    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_content)
    TextView tvContent;
    private String msgID;
    private boolean needRefresh = false;//是否需要刷新,删除消息后
    private String title = "";

    @Override
    public int getViewId() {
        return R.layout.activity_message_notify_detail;
    }

    @Override
    public void afterViews() {
        commTitle.init("", "", "", R.drawable.icon_msg_more, this);
        TextView tvTitle = commTitle.getTvTitle();
        Bundle bundle = getBundleParams();
        if (bundle != null) {
            SysNoticeListData.RowsBean rowsBean = bundle.getParcelable(StringConstants.DATA);
            if (rowsBean != null) {
                msgID = rowsBean.getId();
                int noticeType = rowsBean.getNoticeType();//1,通知  2，公告
                if (noticeType == Config.ONE) {
                    title = MyApp.getLanguageString(this, R.string.message_notify_tz_Details);
                } else {
                    title = MyApp.getLanguageString(this, R.string.message_notify_gg_Details);
                }
                tvTitle.setText(title);
                tvContent.setText(CommonUtils.StringNotNull(rowsBean.getNoticeContent()) ? rowsBean.getNoticeContent() : "");
                tvTime.setText(rowsBean.getCreateTime());
            }
        }
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
    public void onClick(View v) {
        if (!CommonUtils.StringNotNull(msgID) || JudgeDoubleUtils.isDoubleClick()) {
            return;
        }
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.SimpleDialog,
                new SimpleDialogData("删除", MyApp.getMyColor(R.color.colorPrimary), "",
                        0, o -> deleteMsg()));
    }

    /**
     * 删除
     */
    private void deleteMsg() {
        if (!CommonUtils.StringNotNull(msgID)) {
            return;
        }
        new SysImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                needRefresh = true;
//                toast("删除成功");
                finish();
            }

            @Override
            public void onError(String msg, String code) {
                if (StringConstants.REQUEST_OTHER_ERROR.equals(code)) {//网络异常
                    super.onError(msg, code);
                } else {
                    toast("删除失败");
                }
            }
        }).sysNoticeDelete(msgID);
    }

    @Override
    public void finish() {
        if (needRefresh) {
            setResult(RESULT_OK, new Intent());
        }
        super.finish();
    }
}
