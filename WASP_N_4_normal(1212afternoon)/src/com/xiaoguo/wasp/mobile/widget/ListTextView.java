package com.xiaoguo.wasp.mobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ListTextView extends TextView{
	int id=0;
	public ListTextView(Context context) {
		super(context);
	}

	public ListTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ListTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
