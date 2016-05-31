package com.xiaoguo.wasp.mobile.utils;

import java.lang.reflect.Field;

import com.xiaoguo.wasp.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WeatherImgUtils {
	
	public static Bitmap getPicBmp(Context context,String picPath){
		Bitmap bit = null;
		System.out.println("picPath="+picPath);
		String tempStr=picPath.substring(0,picPath.indexOf("."));
		System.out.println("tempstr="+tempStr);
		int no = Integer.parseInt(tempStr);
		System.out.println("picNo="+no);
		
		int[] imageIds = new int[31];//表情ID
		for(int i=0;i<imageIds.length;i++){
			try {
				Field field = R.drawable.class.getDeclaredField("a"+i);
				int resourceId = Integer.parseInt(field.get(null).toString());
				imageIds[i] = resourceId;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		bit = BitmapFactory.decodeResource(context.getResources(), imageIds[no]);
		System.out.println("bit="+bit);
		return bit;
	}
	
	public static Bitmap getIconPicBmp(Context context,String picPath){
		Bitmap bit = null;
		System.out.println("picPath="+picPath);
		String tempStr=picPath.substring(0,picPath.indexOf("."));
		System.out.println("tempstr="+tempStr);
		int no = Integer.parseInt(tempStr);
		System.out.println("picNo="+no);
		
		int[] imageIds = new int[14];//表情ID
		for(int i=1;i<=imageIds.length;i++){
			try {
				Field field = R.drawable.class.getDeclaredField("i_"+i);
				int resourceId = Integer.parseInt(field.get(null).toString());
				imageIds[i] = resourceId;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		bit = BitmapFactory.decodeResource(context.getResources(), imageIds[no]);
		System.out.println("bit="+bit);
		return bit;
	}
}
