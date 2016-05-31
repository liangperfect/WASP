package com.xiaoguo.wasp.mobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ListButton extends Button{
	int id=0;
	int tagId=0;
	private ListTextView textView;
	
	public ListButton(Context context) {
		super(context);
	}
	
	public ListButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public ListTextView getTextView() {
		return textView;
	}

	public void setTextView(ListTextView textView) {
		this.textView = textView;
	}
	
	
}
