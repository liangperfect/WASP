package com.xiaoguo.wasp.mobile.widget;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Base64;

public class AudioEncoder {

	static {
		System.loadLibrary("audiowrapper");
	}

	byte[] audiobyte;// �����Ƶ����
	int audiolenth;// ��Ƶ���ݵĴ�С
	int encodeSize = 0;// ���������ݴ�С
	byte[] encodedAudio = new byte[256];// ��ű���������
	DatagramPacket dataPacket;
	InetAddress ip;
	int port;
	DatagramSocket socket;

	/**
	 * �����ݽ��б���
	 */
	public void startEncoder(String path) {
		System.out.println("¼���ļ���·��" + path);

		try {
			File file = new File(path);
			FileInputStream fileinput = new FileInputStream(file);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int lenth = 0;
			try {
				while ((lenth = fileinput.read(b)) != -1) {
					output.write(b, 0, lenth);
				}
				audiobyte = output.toByteArray();
				audiolenth = audiobyte.length;// ��Ƶ���ݵĴ�С
				output.close();
				fileinput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (audiolenth > 0) {
			encodeAudio();// �����ݽ��б���
		}

	}

	/**
	 * ����Ƶ���ݽ��б���
	 */
	public void encodeAudio() {
	

		AudioCodec.audio_codec_init(30);
		encodedAudio = new byte[audiolenth];
		// �������
		encodeSize = AudioCodec.audio_encode(audiobyte, 0, audiolenth,
				encodedAudio, 0);
		if (encodeSize > 0) {
			// clear data
			System.out.println("�����Ĵ�С��"+encodedAudio.length);
			String messageStr = Base64.encodeToString(encodedAudio, Base64.DEFAULT);
			System.out.println("messageStr="+messageStr);

		}
	}
}
