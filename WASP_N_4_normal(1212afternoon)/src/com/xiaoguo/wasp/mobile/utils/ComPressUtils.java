package com.xiaoguo.wasp.mobile.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ComPressUtils {
	/*  
     * 压缩图片，避免内存不足报错  
     */  
    public static Bitmap decodeFile(File f) {   
        Bitmap b = null;   
        try {   
            // Decode image size   
            BitmapFactory.Options o = new BitmapFactory.Options();   
            o.inJustDecodeBounds = true;   
            FileInputStream fis = new FileInputStream(f);   
            BitmapFactory.decodeStream(fis, null, o);   
            fis.close();   
            int scale = 1;   
            if (o.outHeight > 100 || o.outWidth > 100) {   
                scale = (int) Math.pow(   
                        2,   
                        (int) Math.round(Math.log(100 / (double) Math.max(   
                                o.outHeight, o.outWidth)) / Math.log(0.5)));   
            }   
            // Decode with inSampleSize   
            BitmapFactory.Options o2 = new BitmapFactory.Options();   
            o2.inSampleSize = scale;   
            fis = new FileInputStream(f);   
            b = BitmapFactory.decodeStream(fis, null, o2);   
            fis.close();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        return b;   
    }
    
    public static String getBitToStr(Bitmap pic) {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();  
		pic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		//将图片转为比特流
		byte[] bytes = stream.toByteArray(); 
		//用base64编码转换
        String imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return imageString;
	}
  //将语音文件编码后传输
  	public static String getAudoiToStr(String filePath) {
  		System.out.println("录制文件的路径" + filePath);
  		int audiolenth=0;
  		byte[] audiobyte=null;
  		String str2=null;
  		try {
  			File file = new File(filePath);
  			FileInputStream fileinput = new FileInputStream(file);
  			ByteArrayOutputStream output = new ByteArrayOutputStream();
  			byte[] b = new byte[1024];
  			int lenth = 0;
  			try {
  				while ((lenth = fileinput.read(b)) != -1) {
  					output.write(b, 0, lenth);
  				}
  				audiobyte = output.toByteArray();
  				audiolenth = audiobyte.length;// 音频数据的大小
  				output.close();
  				fileinput.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  				str2="异常！";
  			}

  		} catch (FileNotFoundException e) {
  			e.printStackTrace();
  			str2="异常！";
  		}

  		if (audiolenth > 0) {
  			// 对数据进行编码
  			str2 = Base64.encodeToString(audiobyte, Base64.DEFAULT);
  			System.out.println("str2="+str2);
  		}
  		return str2;
  	}
  	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(filePath, options);


		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 240, 300);
		//options.inSampleSize = calculateInSampleSize(options, options.outWidth/2, options.outHeight/2);


		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;


		return BitmapFactory.decodeFile(filePath, options);
	}
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);


			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}


		return inSampleSize;
	}
	public static String bitmapToString(String filePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		byte[] b = baos.toByteArray();
		recycle(bm);
		return Base64.encodeToString(b, Base64.DEFAULT);
	}
	
	public static String bitmapToString2(String filePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		byte[] b = baos.toByteArray();
		recycle(bm);
		return Base64.encodeToString(b, Base64.DEFAULT);
	}
	
	//回收比特流
	public static void recycle(Bitmap bitmap){
		if(bitmap != null && !bitmap.isRecycled()){   
	        bitmap.recycle();   
	        bitmap = null;   
		}   
		System.gc();  
	}
}
