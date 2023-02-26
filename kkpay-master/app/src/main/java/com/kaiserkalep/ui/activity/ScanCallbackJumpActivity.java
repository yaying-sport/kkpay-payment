package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kaiserkalep.R;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.PushMessageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;

import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 后台调起App中转页面
 */
public class ScanCallbackJumpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_scancallbackjump);
        Intent intent = getIntent();
        if (null != intent && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String url = uri.toString();
                LogUtils.e("ScanCallbackJump  url = " + url);
                String scheme = PushMessageUtils.getHost(url, ScanCallbackJumpActivity.this);
                if (CommonUtils.StringNotNull(url, scheme) &&
                        url.contains(Config.SCHEME)
                ) {
                    Intent intentStr = null;
                    int activityStackSize = MyActivityManager.getActivityManager().getActivityStackSize();
                    if (activityStackSize > Config.ZERO && SPUtil.isLogin()) {
//                        if (scheme.equals(Config.SCHEME_HOST)) {
//                            //kkpay://pay?orderNo=696992517899419648
//                            intentStr = new Intent(this, ScanDetailActivity.class);
//                        } else if (scheme.equals(UIUtils.getString(R.string.scheme_bind))) {
//                            //商户绑定页面   kkpay://bind?a=KK_AGENT_21&u=95116&c=http://platformapi.tw00.cc/api/pay/kkpay/bindUser/notify
//                            intentStr = new Intent(this, ScanBindActivity.class);
//                        } else {
//                            intentStr = new Intent(this, StartActivity.class);
//                        }
                        if (IntentUtils.StartScanResultIntent(this, url)) {
                            finish();
                            return;
                        } else {
                            intentStr = new Intent(this, StartActivity.class);
                        }
                    } else {
                        intentStr = new Intent(this, StartActivity.class);
                    }
                    intentStr.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentStr.putExtra(StringConstants.DATA, url);
                    intentStr.putExtra(StringConstants.TYPE, ZERO_STRING);
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