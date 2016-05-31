package com.xiaoguo.wasp.mobile.widget;


import com.xiaoguo.wasp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ImageTxSpinner extends LinearLayout{
	private ImageView imageView;
	private TextView text;
	private Spinner spinner;
	private TextView tvs;
	private TextView display;
	public ImageTxSpinner(Context context) {
		super(context,null);
	}
	public ImageTxSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.account_info, this, true);
		imageView = (ImageView)findViewById(R.id.my_img);
		text = (TextView)findViewById(R.id.my_nickname);
		spinner = (Spinner)findViewById(R.id.my_state);
		tvs = (TextView)findViewById(R.id.tvs);
		display = (TextView)findViewById(R.id.my_display);
	}
	public ImageView getImageView(){
		return imageView;
	}
	public TextView getTextView(){
		return text;
	}
	public Spinner getSpinner(){
		return spinner;
	}
	public void setImageViewImg(int id){
		imageView.setImageResource(id);
	}
	public void setTextViewTx(String textStr){
		text.setText(textStr);
	}
	
	public TextView getTextView2(){
		return tvs;
	}
	public void setTextView2(String text){
		tvs.setText(text);
	}
	
	public TextView getTextView3(){
		return display;
	}
	public void setTextView3(String text){
		display.setText(text);
	}
}
