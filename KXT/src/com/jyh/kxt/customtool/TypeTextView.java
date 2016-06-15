package com.jyh.kxt.customtool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

/** 
 * @author  beginner 
 * @date 创建时间：2015年11月16日 下午1:27:39 
 * @version 1.0  
 */
public class TypeTextView extends TextView{

	private Context context;
	private Paint paint;
	private FontMetrics fontMetrics;
	private int start;
	private int end;
	@SuppressWarnings("unused")
	private int baseLine;
	
	public TypeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		this.context=context;
		init();
	}

	public TypeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
		// TODO Auto-generated constructor stub
	}

	public TypeTextView(Context context) {
		super(context);
		this.context=context;
		init();
		// TODO Auto-generated constructor stub
	}
	
	private void init(){
		paint = new Paint();
		paint.setColor(Color.rgb(0X16, 0X77, 0Xe0));
		paint.setStrokeWidth(8*getDpi(context));
		fontMetrics = paint.getFontMetrics();
		float dpi=getDpi(context);
		start = (int) (10*dpi);
		baseLine = (int) fontMetrics.bottom;
		end = start+ (int)((Math.ceil(fontMetrics.descent-fontMetrics.ascent)+2)*dpi);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0, start, 0, end, paint);
	}
	
	public static float getDpi(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }
	
}
