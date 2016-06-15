package com.jyh.kxt.customtool;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2015/11/2.
 */
public class SelfGridView extends GridView {
    public SelfGridView(Context context) {
        super(context);
    }

    public SelfGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
