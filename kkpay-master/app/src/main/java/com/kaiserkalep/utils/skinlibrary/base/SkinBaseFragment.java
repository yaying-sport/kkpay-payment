package com.kaiserkalep.utils.skinlibrary.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaiserkalep.utils.skinlibrary.IDynamicNewView;
import com.kaiserkalep.utils.skinlibrary.attr.base.DynamicAttr;
import com.kaiserkalep.utils.skinlibrary.loader.SkinInflaterFactory;

import java.util.List;

import androidx.fragment.app.Fragment;


/**
 * Created by _SOLID
 * Date:2016/4/14
 * Time:10:35
 * Desc:
 */
public class SkinBaseFragment extends Fragment implements IDynamicNewView {

    private IDynamicNewView mIDynamicNewView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIDynamicNewView = (IDynamicNewView) context;
        } catch (ClassCastException e) {
            mIDynamicNewView = null;
        }
    }

    @Override
    public final void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
        if (mIDynamicNewView == null) {
            throw new RuntimeException("IDynamicNewView should be implements !");
        } else {
            mIDynamicNewView.dynamicAddView(view, pDAttrs);
        }
    }

    @Override
    public final void dynamicAddView(View view, String attrName, int attrValueResId) {
        mIDynamicNewView.dynamicAddView(view, attrName, attrValueResId);
    }

    @Override
    public final void dynamicAddFontView(TextView textView) {
        mIDynamicNewView.dynamicAddFontView(textView);
    }

    public final SkinInflaterFactory getSkinInflaterFactory() {
        if (getActivity() instanceof SkinBaseActivity) {
            return ((SkinBaseActivity) getActivity()).getInflaterFactory();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        removeAllView(getView());
        super.onDestroyView();
    }

    protected void removeAllView(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                removeAllView(viewGroup.getChildAt(i));
            }
            removeViewInSkinInflaterFactory(v);
        } else {
            removeViewInSkinInflaterFactory(v);
        }
    }

    private void removeViewInSkinInflaterFactory(View v) {
        if (getSkinInflaterFactory() != null) {
            //???????????????Activity???Fragment????????????????????????Fragment??????View
            getSkinInflaterFactory().removeSkinView(v);
        }
    }

    public void onThemeUpdate() {

    }
}
