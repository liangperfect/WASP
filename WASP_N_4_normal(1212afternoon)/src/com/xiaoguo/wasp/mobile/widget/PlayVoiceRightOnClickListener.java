package com.xiaoguo.wasp.mobile.widget;

import java.io.File;
import java.io.IOException;

import com.xiaoguo.wasp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


public class PlayVoiceRightOnClickListener implements OnClickListener{
	private static PlayVoiceRightOnClickListener instance = null;
	private PalyVoiceLayoutRight btn;
	MediaPlayer mediaPlayer;
	ImageView voice_volumn;
	String filaPath;
	private Thread recordThread;
	int i = 1;
	Boolean isPlaying=false;
	Context context;
	
	public static PlayVoiceRightOnClickListener instance() {
		if (instance == null) {

			instance = new PlayVoiceRightOnClickListener();
		}
		return instance;
	}
	@Override
	public void onClick(View v) {
		btn = (PalyVoiceLayoutRight) v;
		mediaPlayer = btn.getMediaPlayer();
		filaPath = btn.getfilePath();
		voice_volumn = btn.getImageView();
		context = btn.getContext();
		isPlaying = false;
		if (!isPlaying) {
			if (mediaPlayer != null
					&& mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer = new MediaPlayer();
			try {
				System.out.println("path=" + filaPath);
				mediaPlayer.setDataSource(filaPath);
				mediaPlayer.prepare();
				mediaPlayer.start();
				isPlaying = true;
				mediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(
									MediaPlayer mp) {
								if (isPlaying) {
									isPlaying = false;
								}

							}
						});

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				isPlaying = false;
			} else {
				isPlaying = false;
			}
		}
		mythread1();
	}
	
	// 录音计时线程
		void mythread1() {
			recordThread = new Thread(ImgThread1);
			recordThread.start();
		}

		// 录音线程
		private Runnable ImgThread1 = new Runnable() {
			@Override
			public void run() {
				while (isPlaying) {
					try {
						Thread.sleep(200);
						if (i < 3) {
							i = i + 1;
						} else {
							i = 1;
						}
						imgHandle1.sendEmptyMessage(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				i=3;
				imgHandle1.sendEmptyMessage(i);
			}

			@SuppressLint("HandlerLeak")
			Handler imgHandle1 = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						voice_volumn.setImageResource(R.drawable.velumn_right_1);
						break;
					case 2:
						voice_volumn.setImageResource(R.drawable.velumn_right_2);
						break;
					case 3:
						voice_volumn.setImageResource(R.drawable.velumn_right_3);
						break;
					default:
						break;
					}

				}
			};
		};
}

