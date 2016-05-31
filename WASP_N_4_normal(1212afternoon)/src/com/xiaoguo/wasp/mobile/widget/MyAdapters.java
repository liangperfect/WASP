package com.xiaoguo.wasp.mobile.widget;

import java.io.File;
import java.util.List;

import com.xiaoguo.wasp.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapters extends BaseAdapter  
{  
  private LayoutInflater mInflater;  
  private Bitmap mIcon1;  
  private Bitmap mIcon2;  
  private Bitmap mIcon3;  
  private Bitmap mIcon4;  
  private Bitmap mIcon5; 
  private Bitmap mIcon6;
  private Bitmap mIcon7;  
  private Bitmap mIcon8; 
  private Bitmap mIcon10;
  private List<String> items;  
  private List<String> paths;  
  private Context context=null;
  public MyAdapters(Context context,List<String> it,List<String> pa)  
  {  
	  this.context = context;
    mInflater = LayoutInflater.from(context);  
    items = it;  
    paths = pa;  
    mIcon1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.btn_back);  //返回根目录
    mIcon2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.btn_back);  //返回上一级
    mIcon3 = BitmapFactory.decodeResource(context.getResources(),R.drawable.folder);  //下一级
    mIcon4 = BitmapFactory.decodeResource(context.getResources(),R.drawable.unknown);  //未知文件
    mIcon5 = BitmapFactory.decodeResource(context.getResources(),R.drawable.music);  //音乐文件
    mIcon6 = BitmapFactory.decodeResource(context.getResources(),R.drawable.txt);  //文本文件
    mIcon7 = BitmapFactory.decodeResource(context.getResources(),R.drawable.pdf);  //pdf
    mIcon8 = BitmapFactory.decodeResource(context.getResources(),R.drawable.mp4);  //mp4
    mIcon10 = BitmapFactory.decodeResource(context.getResources(),R.drawable.word);  //文本文件
  }  
    
  public int getCount()  
  {  
    return items.size();  
  }  
  public Object getItem(int position)  
  {  
    return items.get(position);  
  }  
    
  public long getItemId(int position)  
  {  
    return position;  
  }  
    
  public View getView(int position,View convertView,ViewGroup parent)  
  {  
    ViewHolder holder;  
      
    if(convertView == null)  
    {  
      convertView = mInflater.inflate(R.layout.file_row, null);  
      holder = new ViewHolder();  
      holder.text = (TextView) convertView.findViewById(R.id.text);  
      holder.icon = (ImageView) convertView.findViewById(R.id.icon);  
        
      convertView.setTag(holder);  
    }  
    else  
    {  
      holder = (ViewHolder) convertView.getTag();  
    }  
    File f=new File(paths.get(position).toString());  
    if(items.get(position).toString().equals("b1"))  
    {  
      holder.text.setText("返回根目录");  
      holder.icon.setImageBitmap(mIcon1);  
    }  
    else if(items.get(position).toString().equals("b2"))  
    {  
      holder.text.setText("返回上一层");  
      holder.icon.setImageBitmap(mIcon2);  
    }  
    else  
    {  
      holder.text.setText(f.getName());  
      if(f.isDirectory())  
      {  
        holder.icon.setImageBitmap(mIcon3);  //文件夹
      }  
      else  
      {  
    	 String fileName = f.getName();
    	 System.out.println("fileName="+fileName);
    	 if(fileName.endsWith(".mp3")){
    		 holder.icon.setImageBitmap(mIcon5);  //音乐文件
    	 }else if(fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")||fileName.endsWith(".png")){
    		 Bitmap bitmap = getImageThumbnail(f.getPath(), 40, 40);
			holder.icon.setImageBitmap(bitmap);  //判断文件的后缀名，设置不同的图标
    	 }else if(fileName.endsWith(".avi")||fileName.endsWith(".mp4")){
    		 Bitmap bitmap = getVideoThumbnail(f.getPath(), 40, 40, MediaStore.Images.Thumbnails.MICRO_KIND);
 			holder.icon.setImageBitmap(bitmap);  //判断文件的后缀名，设置不同的图标
    	 }else if(fileName.endsWith(".pdf")){
    		 holder.icon.setImageBitmap(mIcon7);  //pdf
    	 }else if(fileName.endsWith("doc") || fileName.endsWith(".docx")){
    		 holder.icon.setImageBitmap(mIcon10);  //word
    	 }else if(fileName.endsWith(".txt")||fileName.endsWith(".lrc")||fileName.endsWith(".log")||fileName.endsWith(".xml")){
    		 holder.icon.setImageBitmap(mIcon6);  //word
    	 }else{
    		 holder.icon.setImageBitmap(mIcon4);  //word
    	 }
      }  
    }  
    return convertView;  
  }  
  private class ViewHolder  
  {  
    TextView text;  
    ImageView icon;  
  }  
 /*
   * 根据指定的图像路径和大小来获取缩略图 
     * 此方法有两点好处： 
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
     *        用这个工具生成的图像不会被拉伸。 
     * @param imagePath 图像的路径 
     * @param width 指定输出图像的宽度 
     * @param height 指定输出图像的高度 
     * @return 生成的缩略图 
   * */
  private Bitmap getImageThumbnail(String imagePath, int width, int height) {  
      Bitmap bitmap = null;  
      BitmapFactory.Options options = new BitmapFactory.Options();  
      options.inJustDecodeBounds = true;  
      // 获取这个图片的宽和高，注意此处的bitmap为null   
      bitmap = BitmapFactory.decodeFile(imagePath, options);  
      options.inJustDecodeBounds = false; // 设为 false   
      // 计算缩放比   
      int h = options.outHeight;  
      int w = options.outWidth;  
      int beWidth = w / width;  
      int beHeight = h / height;  
      int be = 1;  
      if (beWidth < beHeight) {  
          be = beWidth;  
      } else {  
          be = beHeight;  
      }  
      if (be <= 0) {  
          be = 1;  
      }  
      options.inSampleSize = be;  
      // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false   
      bitmap = BitmapFactory.decodeFile(imagePath, options);  
      // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象   
      bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
              ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
      return bitmap;  
  } 
  /*
   ** 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
   * */
  private Bitmap getVideoThumbnail(String videoPath, int width, int height,  
          int kind) {  
      Bitmap bitmap = null;  
      // 获取视频的缩略图   
      bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
      System.out.println("w"+bitmap.getWidth());  
      System.out.println("h"+bitmap.getHeight());  
      bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
              ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
      return bitmap;  
  }  

}  
