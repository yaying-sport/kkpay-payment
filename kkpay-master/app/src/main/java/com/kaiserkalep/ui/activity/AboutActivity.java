package com.kaiserkalep.ui.activity;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;

public class AboutActivity extends ZZActivity {

    private String title = "";

    @Override
    public int getViewId() {
        return R.layout.activity_about;
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
        title = MyApp.getLanguageString(getContext(), R.string.about_activity_title);
        commTitle.init(title);
    }
}
