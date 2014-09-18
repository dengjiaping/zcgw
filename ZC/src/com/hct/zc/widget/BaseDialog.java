package com.hct.zc.widget;

import android.app.AlertDialog;
import android.content.Context;

/**
 * 
 * 自定义对话框.
 * 
 * @time 2014年5月9日 下午4:21:35
 * @author jie.liu
 */
public class BaseDialog extends AlertDialog {

	protected BaseDialog(Context context) {
		super(context);
		initial();
	}

	public BaseDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initial();
	}

	public BaseDialog(Context context, int theme) {
		super(context, theme);
		initial();
	}

	private void initial() {
		// setContentView(layoutResID);
	}

}
