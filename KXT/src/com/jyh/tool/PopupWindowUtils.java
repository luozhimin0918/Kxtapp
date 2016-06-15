package com.jyh.tool;


import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 *PopupWindow工具类
 */
public class PopupWindowUtils {
	
    private PopupWindow popupWindow;
  
    /**
     * 创建自定义的popupwindow
     * @param anchor  根据显示的view
     * @param convertView  要显示的view
     * @param styleId  显示动画的样式id
     * @param gravity 显示的位置
     * @param x x轴的偏移量
     * @param y y轴的偏移量
     */
	public void initPopupWindwo(View anchor,View convertView,int w,int h,int color,int style,int x,int y) {

		popupWindow = new PopupWindow(convertView,w,h);
		ColorDrawable dw = new ColorDrawable(color);
		popupWindow.setFocusable(true); // 可以聚焦
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setAnimationStyle(style);
		popupWindow.showAtLocation(anchor, Gravity.CENTER, x, y);
	}


    //取消popupWindow()
    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) { //不为null,并且正在显示中
            popupWindow.dismiss();
        }
    }

    //显示的状态(true代表显示了，false没有显示)
    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing() ? true : false;
    }

}
