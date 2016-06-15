package com.jyh.kxt.customtool;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;

/**
 * Created by Administrator on 2016/4/1.
 */
public class TagLinearView extends View {
    private View view;
    private LinearLayout out_lin;
    private TextView in_text;
    private LayoutInflater inflater;

    public TagLinearView(Context context) {
        super(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.taglinearview, null);
        out_lin = (LinearLayout) view.findViewById(R.id.tag_out_ll);
        in_text = (TextView) view.findViewById(R.id.tag_in_text);
    }

    public View GetView() {
        return view;
    }

    public TagLinearView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagLinearView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOut_linColor(String color) {
        out_lin.setBackgroundColor(Color.parseColor(color));
    }

    public void setIn_Text(String text) {
        in_text.setText(text);
    }

    public void setIn_textColor(String color) {
        in_text.setTextColor(Color.parseColor(color));
    }
    public void setIn_textBgColor(String color) {
        in_text.setBackgroundColor(Color.parseColor(color));
    }

    public void setTextSize(float size) {
        in_text.setTextSize(size);
    }
}
