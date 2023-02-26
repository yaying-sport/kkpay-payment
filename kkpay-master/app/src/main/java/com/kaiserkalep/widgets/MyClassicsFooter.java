package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;

/**
 * 复写下拉刷新foot
 *
 * @Auther: Administrator
 * @Date: 2019/5/8 0008 17:20
 * @Description:
 */
public class MyClassicsFooter extends ClassicsFooter {
    private Context fContext ;

    public MyClassicsFooter(Context context) {
        this(context,null);
    }

    public MyClassicsFooter(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public MyClassicsFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.fContext = context;
    }

    public MyClassicsFooter setTextPulling(String text) {
        mTextPulling = text;
        return this;
    }

    public MyClassicsFooter setTextRefreshing(String text) {
        mTextRefreshing = text;
        return this;
    }

    public MyClassicsFooter setTextRelease(String text) {
        mTextRelease = text;
        return this;
    }

    public MyClassicsFooter setTextFinish(String text) {
        mTextFinish = text;
        return this;
    }

    public MyClassicsFooter setTextTextNothing(String text) {
        mTextNothing = text;
//        MyApp.postDelayed(() -> {
//            if (mTitleText != null) {
//                mTitleText.setText(mTextNothing);
//            }
//        },300);
        return this;
    }

    public MyClassicsFooter setHasData(boolean hasData) {
        if (hasData) {
            setTextTextNothing(MyApp.getLanguageString(fContext,R.string.load_all_data));
        } else {
            setTextTextNothing("");
        }
        return this;
    }


    public MyClassicsFooter setNoText() {
        mTextPulling = "";//"上拉加载更多";
        mTextRelease = "";//"释放立即加载";
        mTextLoading = "";//"正在加载...";
        mTextRefreshing = "";//"正在刷新...";
        mTextFinish = "";//"加载完成";
//        mTextFailed = "";//"加载失败";
        mTextNothing = MyApp.getLanguageString(fContext,R.string.load_all_data);//"没有更多数据了";
        return this;
    }

    public MyClassicsFooter setFootText(){
        mTextPulling = MyApp.getLanguageString(fContext,R.string.load_up_more_data);//"上拉加载更多";
        mTextRelease =MyApp.getLanguageString(fContext,R.string.load_dowm_more_data);//"释放立即加载";
        mTextLoading = "";//"正在加载...";
        mTextRefreshing = "";//"正在刷新...";
        mTextFinish = "";//"加载完成";
//        mTextFailed = "";//"加载失败";
        mTextNothing = MyApp.getLanguageString(fContext,R.string.load_all_data);//"没有更多数据了";
        return this;
    }
}
