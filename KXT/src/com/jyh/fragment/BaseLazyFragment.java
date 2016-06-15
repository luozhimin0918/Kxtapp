//package com.jyh.fragment;
//
//import android.support.v4.app.Fragment;
//
///**
// * Fragment懒加载，界面可见时才加载数据
// * 
// * @author zd
// *
// */
//public abstract class BaseLazyFragment extends Fragment {
//
//	public boolean isVisible;
//
//	/**
//	 * 该方法 在Fragment的onCreateView（）方法前就被调用了，如果在这个方法内部直接调用
//	 * lazyload()加载数据，就可能会造成空指针异常，因为视图还未创建
//	 */
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if (getUserVisibleHint()) {
//			isVisible = true;
//			onVisible();
//		} else {
//			isVisible = false;
//			onInVisible();
//		}
//	}
//
//	/**
//	 * Fragment可见时被调用
//	 */
//	protected void onVisible() {
//		lazyload();
//	}
//
//	/**
//	 * 加载数据
//	 */
//	protected abstract void lazyload();
//
//	/**
//	 * 界面不可见时被调用
//	 */
//	protected void onInVisible() {
////		Log.i("showfalse", "界面补课间");
//	};
//	
//
//}
