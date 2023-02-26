package com.kaiserkalep.utils;

import com.kaiserkalep.base.IBaseView;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

import java.util.List;

/**
 * @Auther: Jack
 * @Date: 2020/6/24 15:31
 * @Description:
 */
public class VideoCollectUtils {


    /**
     * 收藏接口
     *
     * @param needDialog   加载框
     * @param ui           Context
     * @param collect_type 收藏状态 , 1,收藏   0,取消
     * @param ids          视频ids 视频id数组json
     * @param listener     回调
     */
    public static void collectVideo(boolean needDialog, IBaseView ui, String collect_type, List<String> ids,
                                    SucceedCallBackListener<Boolean> listener) {
//        new UserImpl(new ZZNetCallBack<String>(ui, String.class) {
//            @Override
//            public void onSuccess(String response) {
//                if (listener != null) {
//                    listener.succeedCallBack(true);
//                }
//            }
//
//            @Override
//            public void onError(String msg, String code) {
//                super.onError(msg, code);
//                if (listener != null) {
//                    listener.succeedCallBack(false);
//                }
//            }
//        }.setNeedDialog(needDialog)).collectVideo(collect_type, ids);
    }


}
