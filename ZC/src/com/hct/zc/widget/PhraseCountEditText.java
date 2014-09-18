package com.hct.zc.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.utils.StringUtils;

/**
 * 
 * 输入框中字符限定控件.
 * 
 * @time 2014年5月9日 下午4:23:39
 * @author jie.liu
 */
public class PhraseCountEditText extends FrameLayout {
	private EditText mFeedbackET;
	private TextView mPhraseCountTV;

	private static final int MAX_COUNT = 200;
	private static final String UNIT_OF_COUNT = "/";

	public static int commentCount = -1;

	public PhraseCountEditText(Context context) {
		super(context);
		init(context);
	}

	public PhraseCountEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PhraseCountEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View mRootView = inflater.inflate(R.layout.feedback_et, this);
		mFeedbackET = (EditText) mRootView.findViewById(R.id.content_et);
		mPhraseCountTV = (TextView) mRootView
				.findViewById(R.id.phrase_count_tv);
		mFeedbackET.addTextChangedListener(textWatcher);
	}

	private final TextWatcher textWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			editStart = mFeedbackET.getSelectionStart();
			editEnd = mFeedbackET.getSelectionEnd();

			mFeedbackET.removeTextChangedListener(textWatcher);

			while (calculateLength(s.toString()) > MAX_COUNT) {
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}

			mFeedbackET.setSelection(editStart);
			mFeedbackET.addTextChangedListener(textWatcher);

			setLeftCount();
		}
	};

	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 1;
			} else {
				len += 2;
			}
		}
		return Math.round(len);
	}

	private void setLeftCount() {
		mPhraseCountTV.setText(String.valueOf((MAX_COUNT - getInputCount()))
				+ UNIT_OF_COUNT + MAX_COUNT);
	}

	private long getInputCount() {
		return calculateLength(mFeedbackET.getText().toString());
	}

	public String getText() {
		return StringUtils.getText(mFeedbackET);
	}

}
