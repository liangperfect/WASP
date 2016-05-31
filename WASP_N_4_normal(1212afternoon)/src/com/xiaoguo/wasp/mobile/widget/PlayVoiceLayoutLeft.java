package com.xiaoguo.wasp.mobile.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoguo.wasp.R;

public class PlayVoiceLayoutLeft extends LinearLayout{
	TextView timeView;
	ImageView imageView;
	MediaPlayer mediaPlayer;
	String filePath;
	
	public PlayVoiceLayoutLeft(Context context, MediaPlayer mediaPlayer,
			 String filePath, int j, Context context2) {
		super(context);
		this.mediaPlayer = mediaPlayer;
		this.filePath = filePath;
	}

	public PlayVoiceLayoutLeft(Context context) {
		super(context);
	}
	
	public PlayVoiceLayoutLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.left_voice, this, true);
		timeView = (TextView)findViewById(R.id.voice_time);
		imageView = (ImageView)findViewById(R.id.voice_volumn);
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setSaveFile(String filePath) {
		this.filePath = filePath;
	}

	public TextView getTimeView() {
		return timeView;
	}


	public void setTimeView(TextView timeView) {
		this.timeView = timeView;
	}


	public ImageView getImageView() {
		return imageView;
	}


	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
	
	
}
