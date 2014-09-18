package com.hct.zc.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.utils.StringUtils;

/**
 * @todo 自定义edittext
 * @time 2014-5-20 上午10:20:09
 * @author liuzenglong163@gmail.com
 */

public class CstEditText extends LinearLayout {
	

	public CstEditText(Context context) {
		super(context);
		initView(context);
	}
	
	public CstEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	@SuppressLint("NewApi")
	public CstEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private TextView tvName;
	private EditText etContent;
	
	public TextView getTvName() {
		return tvName;
	}


	public EditText getEtContent() {
		return etContent;
	}


	private void initView(Context context) {
		View cstView = LayoutInflater.from(context).inflate(R.layout.cst_edittext_layout,this);
		tvName = (TextView) cstView.findViewById(R.id.tv_name);
		etContent = (EditText) cstView.findViewById(R.id.et_content);
	}
	
	public void setTextChangListener(TextWatcher watch){
		etContent.addTextChangedListener(watch);
	}
	
	public void setInitName(String name,String hintConString){
		tvName.setText(name);
		etContent.setHint(hintConString);
	}
	
	public void setInitName(int name,int hintConString){
		tvName.setText(name);
		etContent.setHint(hintConString);
	}
	
	public String getText() {
		return StringUtils.getText(etContent);
	}

	public void setText(String content) {
		etContent.setText(content);
	}

	public void setInputType(int type) {
		etContent.setInputType(type);
	}

	public void setEditable(boolean enable) {
		etContent.setEnabled(enable);
	}
}
