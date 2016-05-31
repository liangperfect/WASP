package com.xiaoguo.wasp.mobile.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

import com.swetake.util.Qrcode;

public class QRcodeUtils {
	
	public static Bitmap androidQRCode(String str2) {
		Qrcode qrEncoding = new Qrcode();
		qrEncoding.setQrcodeEncodeMode('M');
		qrEncoding.setQrcodeErrorCorrect('B');
		qrEncoding.setQrcodeVersion(7);
		Bitmap bt = null;
		try {
			byte[] strByte = str2.getBytes("utf-8");
			if(strByte.length > 0){
				boolean[][] bEncoding = qrEncoding.calQrcode(strByte);
				int w = 320;
				int h = 240;
				bt = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				Canvas cn = new Canvas(bt);
//				onDraw(cn);
				int padding = 50;
				cn.drawColor(android.graphics.Color.WHITE);
				Paint myPaint = new Paint();
				myPaint.setColor(android.graphics.Color.BLACK);
				myPaint.setStyle(Paint.Style.FILL);
				myPaint.setStrokeWidth(1.0f);
				for(int i=0; i<bEncoding.length; i++){
					for(int j=0; j<bEncoding.length; j++){
						if(bEncoding[j][i]){
							cn.drawRect(new Rect(padding+3*j+2,
									padding+3*i+2,
									padding+3*j+2+3,
									padding+3*i+2+3), myPaint);
						}
					}
				}
				cn.save(Canvas.ALL_SAVE_FLAG);
				cn.restore();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bt;
		
	}
	/*private void onDraw(Canvas cn) {
		// TODO Auto-generated method stub
		int padding = 50;
		cn.drawColor(android.graphics.Color.WHITE);
		Paint myPaint = new Paint();
		myPaint.setColor(android.graphics.Color.BLACK);
		myPaint.setStyle(Paint.Style.FILL);
		myPaint.setStrokeWidth(1.0f);
		for(int i=0; i<bEncoding.length; i++){
			for(int j=0; j<bEncoding.length; j++){
				if(bEncoding[j][i]){
					cn.drawRect(new Rect(padding+3*j+2,
							padding+3*i+2,
							padding+3*j+2+3,
							padding+3*i+2+3), myPaint);
				}
			}
		}
	}*/
	public static void saveEWM(Bitmap bitmap, String filePath, int count) throws IOException {
		FileOutputStream fos=null;
		String newFile = "/sdcard/WASP/qrcode";
		File file = new File(newFile);
		if(!file.exists()){
			file.mkdirs();
		}
		File fileName01 = new File(newFile+"/"+filePath+".png");
		if(!fileName01.exists()){
			fileName01.createNewFile();
			try{
				fos = new FileOutputStream(fileName01);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}finally{
				fos.close();
				fos.flush();
			}
		}
	}
}
