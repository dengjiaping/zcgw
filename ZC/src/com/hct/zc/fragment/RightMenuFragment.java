package com.hct.zc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hct.zc.R;

/**
 * 
 * @todo 首页的右侧菜单， 预留，暂时没用.
 * @time 2014年5月4日 下午2:18:16
 * @author jie.liu
 */
public class RightMenuFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.right_menu_fragment, null);
		return view;
	}

}
