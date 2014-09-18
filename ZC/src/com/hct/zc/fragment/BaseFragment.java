package com.hct.zc.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hct.zc.R;
import com.hct.zc.http.HttpHelper.OnHttpResponse;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;

public class BaseFragment extends Fragment implements OnHttpResponse {

	public void pushToFragment(BaseFragment targetFragment) {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.content_frame, targetFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.setCustomAnimations(R.animator.slide_right_in,
				R.animator.slide_right_out);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void finish() {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		manager.popBackStack();
	}

	protected void clearBackStack() {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		while (manager.getBackStackEntryCount() > 0) {
			manager.popBackStackImmediate();
		}
	}

	protected void hideSystemKeyboard(EditText editText) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	public void onHttpNetworkNotFound(String path) {
		Toaster.showShort(getActivity(), "未检测到可用的网络");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpReturn(String path, int response, String result) {

	}

	@Override
	public void onHttpError(String path, Exception exception) {
		Toaster.showShort(getActivity(), "网络连接异常，请重试");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpError(String path, int response) {
		Toaster.showShort(getActivity(), "网络连接超时，请重试");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		StringBuilder sb = new StringBuilder();
		sb.append("请求路径：").append(path).append("\n").append(result);
		LogUtil.i(this, sb.toString());
		dismissDialogIfNeeded();
	}

	private void dismissDialogIfNeeded() {
		// 如果有启动进度条对话框将被关闭
		LoadingProgress.getInstance().dismiss();
	}
}
