package com.hct.zc.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.utils.StringUtils;

public class CustomEditText extends LinearLayout {

	private Context mContext;

	private LinearLayout mRootView;

	private TextView mPromptTV;

	private EditText mContentET;

	private TextView mHintTV;

	public CustomEditText(Context context) {
		super(context);
		init(context);
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mContext = context;

		LayoutInflater inflater = LayoutInflater.from(context);
		mRootView = (LinearLayout) inflater.inflate(R.layout.custom_edit_text,
				this);
		mPromptTV = (TextView) mRootView.findViewById(R.id.prompt_tv);
		mContentET = (EditText) mRootView.findViewById(R.id.content_et);
		mHintTV = (TextView) mRootView.findViewById(R.id.hint_tv);

		mContentET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				actDependContent(mContentET, mHintTV);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void actDependContent(EditText contentET, TextView promptTV) {
		String content = StringUtils.getText(contentET);
		if (TextUtils.isEmpty(content)) {
			promptTV.setVisibility(View.VISIBLE);
		} else {
			promptTV.setVisibility(View.GONE);
		}
	}

	public void init(int promptId, int hintId) {
		String prompt = mContext.getString(promptId);
		String hint = mContext.getString(hintId);
		init(prompt, hint);
	}

	public void init(String prompt, String hint) {
		mPromptTV.setText(prompt);
		mHintTV.setText(hint);
	}

	public String getText() {
		return StringUtils.getText(mContentET);
	}

	public void setText(String content) {
		mContentET.setText(content);
	}

	public void setInputType(int type) {
		mContentET.setInputType(type);
	}

	public void setBackGround(int resid) {
		mRootView.setBackgroundResource(resid);
	}

	public void setEditable(boolean enable) {
		mContentET.setEnabled(enable);
	}

}
