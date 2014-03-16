/*
 *Copyright (c) <2014> <Kevin Johnson>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package com.kevinj.floatlabelpattern;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class FloatLabelTextView extends LinearLayout {
	private static final String TAG = "FloatLabelTextView";

	private static long ANIMATION_DURATION = 400;
	private static int HINT_DEFAULT_COLOR = 0xFF006363;
	private static final int HINT_DEFAULT_COLOR_DISABLED = 0xFFCCCCCC;
	private static int HINT_DEFAULT_SIZE = 13;

	private TextView mHintView;
	private EditText mEditText;

	private int mHintColor;

	private AnimatorSet mEntranceAnimation = null;
	private AnimatorSet mExitAnimation = null;
	private ValueAnimator mAddColorAnimation = null;
	private ValueAnimator mRemoveColorAnimation = null;

	public FloatLabelTextView(Context context) {
		this(context, null);
	}

	public FloatLabelTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.float_label_textview, this, true);

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.FloatLabelTextView, 0, 0);
		String hint = array
				.getString(R.styleable.FloatLabelTextView_editText_hint);
		mHintColor = array.getColor(
				R.styleable.FloatLabelTextView_editText_color,
				HINT_DEFAULT_COLOR);
		float size = array
				.getDimension(R.styleable.FloatLabelTextView_editText_size,
						HINT_DEFAULT_SIZE);

		array.recycle();

		mHintView = (TextView) findViewById(R.id.textview_float);
		mEditText = (EditText) findViewById(R.id.textview_main);

		mHintView.setTextColor(HINT_DEFAULT_COLOR_DISABLED);

		mHintView.setText(hint);
		mEditText.setHint(hint);

		mHintView.setTextSize(size);

		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					getRemoveColorAnimation().start();
				} else {
					getAddColorAnimation().start();
				}
			}
		});

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					if (mHintView.getVisibility() == View.INVISIBLE) {
						getEntranceAnimation().start();
					}
				} else {
					getExitAnimation().start();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	public void addTextChangedListener(TextWatcher watcher) {
		mEditText.addTextChangedListener(watcher);
	}

	protected ValueAnimator getAddColorAnimation() {
		if (mAddColorAnimation == null) {
			mAddColorAnimation = ObjectAnimator.ofInt(mHintView, "textColor",
					HINT_DEFAULT_COLOR_DISABLED, mHintColor);
			mAddColorAnimation.setEvaluator(new ArgbEvaluator());
			mAddColorAnimation.setDuration(ANIMATION_DURATION);
		}
		return mAddColorAnimation;
	}

	protected ValueAnimator getRemoveColorAnimation() {
		if (mRemoveColorAnimation == null) {
			mRemoveColorAnimation = ObjectAnimator.ofInt(mHintView,
					"textColor", mHintColor, HINT_DEFAULT_COLOR_DISABLED);
			mRemoveColorAnimation.setEvaluator(new ArgbEvaluator());
			mRemoveColorAnimation.setDuration(ANIMATION_DURATION);
		}
		return mRemoveColorAnimation;
	}

	protected AnimatorSet getExitAnimation() {
		if (mExitAnimation == null) {
			mExitAnimation = new AnimatorSet();
			mExitAnimation.playTogether(
					ObjectAnimator.ofFloat(mHintView, "translationY", 0, 30),
					ObjectAnimator.ofFloat(mHintView, "alpha", 1, 0));
			mExitAnimation.setDuration(ANIMATION_DURATION);
			mExitAnimation.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mHintView.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
		}

		return mExitAnimation;
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		mEditText.setOnClickListener(l);
		super.setOnClickListener(l);
	}
	
	@Override
	public void setTag(Object tag) {
		mEditText.setTag(tag);
		super.setTag(tag);
	}
	
	@Override
	public void setTag(int key, Object tag) {
		mEditText.setTag(key, tag);
		super.setTag(key, tag);
	}

	protected AnimatorSet getEntranceAnimation() {
		if (mEntranceAnimation == null) {
			mEntranceAnimation = new AnimatorSet();
			mEntranceAnimation.playTogether(
					ObjectAnimator.ofFloat(mHintView, "translationY", 30, 0),
					ObjectAnimator.ofFloat(mHintView, "alpha", 0, 1));
			mEntranceAnimation.setDuration(ANIMATION_DURATION);
			mEntranceAnimation.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					mHintView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
		}

		return mEntranceAnimation;
	}

	public void setAnimationDuration(long duration) {
		ANIMATION_DURATION = duration;
	}

	public void setHint(String text) {
		mHintView.setText(text);
		mEditText.setHint(text);
	}

	public void setKeyListener(KeyListener input) {
		mEditText.setKeyListener(input);
	}

	public void setText(CharSequence text) {
		mEditText.setText(text);
	}

	public void setText(String text) {
		mEditText.setText(text);
	}

	public void setTextColor(ColorStateList colors) {
		mHintView.setTextColor(colors);
	}

	public void setTextColor(int color) {
		mHintView.setTextColor(color);
	}

	public void setTextSize(float size) {
		mHintView.setTextSize(size);
	}

	public void setTextSize(int unit, float size) {
		mHintView.setTextSize(unit, size);
	}

	public void setTypeface(Typeface tf, int style) {
		mHintView.setTypeface(tf, style);
	}

	public void setTypeface(Typeface tf) {
		mHintView.setTypeface(tf);
	}

}
