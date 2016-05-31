package com.xiaoguo.wasp.mobile.widget;

import java.io.File;

import com.xiaoguo.wasp.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PalyVoiceLayoutRight extends LinearLayout{
	TextView timeView;
	ImageView imageView;
	MediaPlayer mediaPlayer;
	String filePath;
	
	public PalyVoiceLayoutRight(Context context, MediaPlayer mediaPlayer,
			String filePath, int j, Context context2) {
		super(context);
		this.mediaPlayer = mediaPlayer;
		this.filePath = filePath;
	}

	public PalyVoiceLayoutRight(Context context) {
		super(context);
	}
	
	public PalyVoiceLayoutRight(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.right_voice, this, true);
		timeView = (TextView)findViewById(R.id.voice_time);
		imageView = (ImageView)findViewById(R.id.voice_volumn);
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	
	public String getfilePath() {
		return filePath;
	}
	
	public void setfilaPath(String filePath) {
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
