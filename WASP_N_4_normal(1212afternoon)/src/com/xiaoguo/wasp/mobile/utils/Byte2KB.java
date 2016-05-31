package com.xiaoguo.wasp.mobile.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import android.graphics.Bitmap;

public class Byte2KB {
	public   static  String bytes2kb( long  bytes)   {
        BigDecimal filesize  =   new  BigDecimal(bytes);
        BigDecimal megabyte  =   new  BigDecimal( 1024 * 1024 );
         float  returnValue  =  filesize.divide(megabyte,  2 , BigDecimal.ROUND_UP).floatValue();
         if  (returnValue  >   1 )
             return (returnValue  +   "  MB " );
        BigDecimal kilobyte  =   new  BigDecimal( 1024 );
        returnValue  =  filesize.divide(kilobyte,  2 , BigDecimal.ROUND_UP).floatValue();
         return (returnValue  +   "  KB " );
	} 
  public static byte[] Bitmap2Bytes(Bitmap bm) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
         return baos.toByteArray();
   }	
}
