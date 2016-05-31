package com.xiaoguo.wasp.mobile.communication;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class URLDrawable extends BitmapDrawable {

	protected Drawable drawable;

	@Override
	public void draw(Canvas canvas) {

		
		if (drawable != null) {
			drawable.draw(canvas);
		}
	}
}
