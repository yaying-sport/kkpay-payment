package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kaiserkalep.R;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.SPUtil;

import static com.kaiserkalep.constants.Config.ONE_STRING;

/**
 * 商户存款
 * 后台调起App中转页面
 */
public class ScanShCallbackJumpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_scancallbackjump);
        Intent intent = getIntent();
        if (null != intent && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                Intent intentStr;
                String scheme = uri.toString();
                ////kkpay://agent?address=xcixo8q4
                if (CommonUtils.StringNotNull(scheme) &&
                        scheme.contains(Config.SCHEME) &&
                        scheme.contains(Config.SCHEME_SH_HOST) &&
                        scheme.contains(Config.SCHEME_SH_PARAM)) {
                    int activityStackSize = MyActivityManager.getActivityManager().getActivityStackSize();
                    if (activityStackSize > Config.ZERO && SPUtil.isLogin()) {
                        intentStr = new Intent(this, DepositShActivity.class);
                    } else {
                        intentStr = new Intent(this, StartActivity.class);
                    }
                    intentStr.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentStr.putExtra(StringConstants.DATA, scheme);
                    intentStr.putExtra(StringConstants.TYPE, ONE_STRING);
                    startActivity(intentStr);
                }
            }
        }

        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}