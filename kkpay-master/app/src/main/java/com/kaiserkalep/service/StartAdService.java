package com.kaiserkalep.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.bigimageViewpage.tool.utility.image.ImageUtil;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 开屏广告下载
 *
 * @Auther: Jack
 * @Date: 2019/10/14 10:43
 * @Description:
 */
public class StartAdService extends IntentService {

    public StartAdService() {
        super("StartAdService");
    }

    public static void startMyService(Context context, List<String> loadImageUrls) {
        Intent intent = new Intent(context, StartAdService.class);
        intent.putStringArrayListExtra("loadImageUrls", (ArrayList<String>) loadImageUrls);
        try {
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ArrayList<String> loadImageUrls = intent.getStringArrayListExtra("loadImageUrls");
            if (CommonUtils.ListNotNull(loadImageUrls)) {
                for (String url : loadImageUrls) {
                    File bitmap = GlideUtil.loadFile(getApplicationContext(), BaseNetUtil.jointUrl(url));
                    if (bitmap != null) {
                        ImageUtil.saveAdGuideFile(getApplicationContext(), bitmap, url);
                    }
                }
            }
        }
    }
}
