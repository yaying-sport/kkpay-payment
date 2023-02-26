package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.kaiserkalep.R;
import com.kaiserkalep.base.ViewBase;


/**
 * 通用webview功能tab
 *
 * @Auther: Jack
 * @Date: 2019/11/11 10:26
 * @Description:
 */
public class WebViewTabLayout extends ViewBase implements View.OnClickListener {

    ImageView ivBack;
    ImageView ivAdvance;

    public WebViewTabLayout(Context context) {
        super(context);
    }

    public WebViewTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getViewId() {
        return R.layout.view_web_view_tab;
    }

    @Override
    public void afterViews(View v) {
        ivBack = v.findViewById(R.id.iv_back);
        ivAdvance = v.findViewById(R.id.iv_advance);
        ivBack.setOnClickListener(this);
        ivAdvance.setOnClickListener(this);
        v.findViewById(R.id.iv_refresh).setOnClickListener(this);
        setBackStatus(true);
        setAdvanceStatus(true);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (listener != null) {
                    listener.back();
                }
                break;
            case R.id.iv_advance:
                if (listener != null) {
                    listener.advance();
                }
                break;
            case R.id.iv_refresh:
                if (listener != null) {
                    listener.refresh();
                }
                break;
        }
    }

    public void setBackStatus(boolean select) {
        ivBack.setSelected(select);
    }

    public void setAdvanceStatus(boolean select) {
        ivAdvance.setSelected(select);
    }

    public interface WebViewTabListener {
        void back();

        void advance();

        void refresh();
    }

    private WebViewTabListener listener;

    public void setListener(WebViewTabListener listener) {
        this.listener = listener;
    }
}
