package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.kaiserkalep.R;
import com.kaiserkalep.base.ViewBase;
import com.kaiserkalep.widgets.indicatorseekbar.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 订单进度
 */
public class OrderProgressStatus extends ViewBase {

    @BindView(R.id.tv_top_one)
    TextView tvTopOne;
    @BindView(R.id.tv_top_two)
    TextView tvTopTwo;
    @BindView(R.id.tv_top_thr)
    TextView tvTopThr;
    @BindView(R.id.tv_top_four)
    TextView tvTopFour;
    @BindView(R.id.isb_seekbar)
    IndicatorSeekBar isbSeekbar;
    @BindView(R.id.tv_bottom_date_one)
    TextView tvBottomDateOne;
    @BindView(R.id.tv_bottom_time_one)
    TextView tvBottomTimeOne;
    @BindView(R.id.tv_bottom_date_two)
    TextView tvBottomDateTwo;
    @BindView(R.id.tv_bottom_time_two)
    TextView tvBottomTimeTwo;
    @BindView(R.id.tv_bottom_date_thr)
    TextView tvBottomDateThr;
    @BindView(R.id.tv_bottom_time_thr)
    TextView tvBottomTimeThr;
    @BindView(R.id.tv_bottom_date_four)
    TextView tvBottomDateFour;
    @BindView(R.id.tv_bottom_time_four)
    TextView tvBottomTimeFour;

    private List<TextView> listTvTop;
    private List<TextView> listTvBottomDate;
    private List<TextView> listTvBottomTime;

    public OrderProgressStatus(Context context) {
        super(context);
    }

    public OrderProgressStatus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderProgressStatus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getViewId() {
        return R.layout.view_progress_status_layout;
    }

    @Override
    public void afterViews(View v) {
        listTvTop = new ArrayList<>();
        listTvTop.add(tvTopOne);
        listTvTop.add(tvTopTwo);
        listTvTop.add(tvTopThr);
        listTvTop.add(tvTopFour);

        listTvBottomDate = new ArrayList<>();
        listTvBottomDate.add(tvBottomDateOne);
        listTvBottomDate.add(tvBottomDateTwo);
        listTvBottomDate.add(tvBottomDateThr);
        listTvBottomDate.add(tvBottomDateFour);

        listTvBottomTime = new ArrayList<>();
        listTvBottomTime.add(tvBottomTimeOne);
        listTvBottomTime.add(tvBottomTimeTwo);
        listTvBottomTime.add(tvBottomTimeThr);
        listTvBottomTime.add(tvBottomTimeFour);

        isbSeekbar.setOnTouchListener((v1, event) -> true);
    }

    public OrderProgressStatus setTopTitle(String... title) {
        for (int i = 0; i < title.length; i++) {
            listTvTop.get(i).setText(title[i]);
        }
        return this;
    }

    public OrderProgressStatus setBottomDateTitle(String... title) {
        for (int i = 0; i < title.length; i++) {
            listTvBottomDate.get(i).setText(title[i]);
        }
        return this;
    }

    public OrderProgressStatus setBottomDateTitle(int index, String title) {
        listTvBottomDate.get(index).setText(title);
        return this;
    }

    public OrderProgressStatus setBottomTimeTitle(String... title) {
        for (int i = 0; i < title.length; i++) {
            listTvBottomTime.get(i).setText(title[i]);
        }
        return this;
    }

    public OrderProgressStatus setBottomTimeTitle(int index, String title) {
        listTvBottomTime.get(index).setText(title);
        return this;
    }

    public void setOrderProgress(int progress) {
        if (null != isbSeekbar) {
            isbSeekbar.setProgress(progress);
        }
    }
}
