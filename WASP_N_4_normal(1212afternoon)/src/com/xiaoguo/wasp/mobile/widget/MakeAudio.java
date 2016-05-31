package com.xiaoguo.wasp.mobile.widget;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Environment;

public class MakeAudio
{
	private static int SAMPLE_RATE_IN_HZ = 8000; 

	final MediaRecorder recorder = new MediaRecorder();
	public String path;

	public MakeAudio(String path)
	{
		this.path = sanitizePath(path);
	}

	private String sanitizePath(String path)
	{
		if (!path.startsWith("/"))
		{
			path = "/" + path+getfilmName();
		}
		if (!path.contains("."))
		{
			path += ".amr";
		}
		String sdStatus = Environment.getExternalStorageState();  
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����   
			File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/EHS/send/audio");
			if (!f.exists()) {//�����ڴ���
				 f.mkdirs();
			}
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/EHS/send/audio/" + path;
        }else{
        	return "û��sd��";
		}
	}

	@SuppressWarnings("deprecation")
	public void start() throws IOException
	{
		String state = android.os.Environment.getExternalStorageState();
		//��������״̬android.os.Environment.MEDIA_MOUNTED
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) { throw new IOException(
				"SD Card is not mounted,It is  " + state + "."); }
		File directory = new File(path).getParentFile();//��ȡpath�ĸ�Ŀ¼
		if (!directory.exists() && !directory.mkdirs()) { throw new IOException(
				"Path to file could not be created"); }
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);//��������ļ��ĸ�ʽ�ڼ�����ļ�¼��
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);//������Ƶ�����ʼ�¼
		recorder.setOutputFile(path);//ͨ�����ļ��������ļ��ı�д��
		recorder.prepare();
		recorder.start();
	}

	public void stop() throws IOException
	{
		recorder.stop();
		recorder.release();
	}
	/**
	 * ��ȡ��Ƶ���
	 * @return
	 */
	public double getAmplitude() {		
		if (recorder != null){			
			return  (recorder.getMaxAmplitude());		
			}		
		else			
			return 0;	
		}
	/**
	 * ����ʱ���
	 */
	@SuppressLint("SimpleDateFormat")
	public String getfilmName(){
		SimpleDateFormat  sim=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String film=sim.format(new Timestamp(System.currentTimeMillis()));
		return film;
		
	}
}