package com.xiaoguo.wasp.mobile.communication;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.widget.TextView;

public class URLImageParser implements ImageGetter {

	Context c;
	TextView tv_image;

	public URLImageParser(TextView t, Context c) {
		this.tv_image = t;
		this.c = c;
	}

	@Override
	public Drawable getDrawable(String source) {
		URLDrawable urlDrawable = new URLDrawable();
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);
		// 图片要拼接一下
		String detailUrl = "http://218.65.88.29:10080" + source;
		asyncTask.execute(detailUrl);
		return urlDrawable;
	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(URLDrawable d) {
			this.urlDrawable = d;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(),
						0 + result.getIntrinsicHeight());
				urlDrawable.drawable = result;
				URLImageParser.this.tv_image.invalidate();
				URLImageParser.this.tv_image
						.setHeight(URLImageParser.this.tv_image.getHeight()
								+ result.getIntrinsicHeight());
				URLImageParser.this.tv_image.setEllipsize(null);

				// super.onPostExecute(result);
				// if (result != null) {
				// mDrawable.setDrawable(result);
				// Log.i(getClass().getSimpleName(), "更新界面：");
				// mText.invalidate();
				// mText.setHeight(mText.getHeight()+result.getIntrinsicHeight());
				// mText.setEllipsize(null);

			}
		}

		@Override
		protected Drawable doInBackground(String... params) {
			// TODO Auto-generated method stub
			String source = params[0];// 图片URL
			return fetchDrawable(source);
		}

		// 获取URL的Drawable对象
		public Drawable fetchDrawable(String urlString) {
			try {
				InputStream is = fetch(urlString);
				Drawable drawable = Drawable.createFromStream(is, "src");
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				return drawable;
			} catch (Exception e) {
				return null;
			}
		}

		private InputStream fetch(String urlString)
				throws MalformedURLException, IOException {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlString);
			HttpResponse response = httpClient.execute(request);
			return response.getEntity().getContent();
		}

	}

}
