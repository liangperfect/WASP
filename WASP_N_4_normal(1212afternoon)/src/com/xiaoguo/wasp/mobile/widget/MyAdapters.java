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
    mIcon1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.btn_back);  //���ظ�Ŀ¼
    mIcon2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.btn_back);  //������һ��
    mIcon3 = BitmapFactory.decodeResource(context.getResources(),R.drawable.folder);  //��һ��
    mIcon4 = BitmapFactory.decodeResource(context.getResources(),R.drawable.unknown);  //δ֪�ļ�
    mIcon5 = BitmapFactory.decodeResource(context.getResources(),R.drawable.music);  //�����ļ�
    mIcon6 = BitmapFactory.decodeResource(context.getResources(),R.drawable.txt);  //�ı��ļ�
    mIcon7 = BitmapFactory.decodeResource(context.getResources(),R.drawable.pdf);  //pdf
    mIcon8 = BitmapFactory.decodeResource(context.getResources(),R.drawable.mp4);  //mp4
    mIcon10 = BitmapFactory.decodeResource(context.getResources(),R.drawable.word);  //�ı��ļ�
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
      holder.text.setText("���ظ�Ŀ¼");  
      holder.icon.setImageBitmap(mIcon1);  
    }  
    else if(items.get(position).toString().equals("b2"))  
    {  
      holder.text.setText("������һ��");  
      holder.icon.setImageBitmap(mIcon2);  
    }  
    else  
    {  
      holder.text.setText(f.getName());  
      if(f.isDirectory())  
      {  
        holder.icon.setImageBitmap(mIcon3);  //�ļ���
      }  
      else  
      {  
    	 String fileName = f.getName();
    	 System.out.println("fileName="+fileName);
    	 if(fileName.endsWith(".mp3")){
    		 holder.icon.setImageBitmap(mIcon5);  //�����ļ�
    	 }else if(fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")||fileName.endsWith(".png")){
    		 Bitmap bitmap = getImageThumbnail(f.getPath(), 40, 40);
			holder.icon.setImageBitmap(bitmap);  //�ж��ļ��ĺ�׺�������ò�ͬ��ͼ��
    	 }else if(fileName.endsWith(".avi")||fileName.endsWith(".mp4")){
    		 Bitmap bitmap = getVideoThumbnail(f.getPath(), 40, 40, MediaStore.Images.Thumbnails.MICRO_KIND);
 			holder.icon.setImageBitmap(bitmap);  //�ж��ļ��ĺ�׺�������ò�ͬ��ͼ��
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
   * ����ָ����ͼ��·���ʹ�С����ȡ����ͼ 
     * �˷���������ô��� 
     *     1. ʹ�ý�С���ڴ�ռ䣬��һ�λ�ȡ��bitmapʵ����Ϊnull��ֻ��Ϊ�˶�ȡ��Ⱥ͸߶ȣ� 
     *        �ڶ��ζ�ȡ��bitmap�Ǹ��ݱ���ѹ������ͼ�񣬵����ζ�ȡ��bitmap����Ҫ������ͼ�� 
     *     2. ����ͼ����ԭͼ������û�����죬����ʹ����2.2�汾���¹���ThumbnailUtils��ʹ 
     *        ������������ɵ�ͼ�񲻻ᱻ���졣 
     * @param imagePath ͼ���·�� 
     * @param width ָ�����ͼ��Ŀ�� 
     * @param height ָ�����ͼ��ĸ߶� 
     * @return ���ɵ�����ͼ 
   * */
  private Bitmap getImageThumbnail(String imagePath, int width, int height) {  
      Bitmap bitmap = null;  
      BitmapFactory.Options options = new BitmapFactory.Options();  
      options.inJustDecodeBounds = true;  
      // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull   
      bitmap = BitmapFactory.decodeFile(imagePath, options);  
      options.inJustDecodeBounds = false; // ��Ϊ false   
      // �������ű�   
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
      // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false   
      bitmap = BitmapFactory.decodeFile(imagePath, options);  
      // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����   
      bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
              ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
      return bitmap;  
  } 
  /*
   ** ��ȡ��Ƶ������ͼ 
     * ��ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ�� 
     * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档 
     * @param videoPath ��Ƶ��·�� 
     * @param width ָ�������Ƶ����ͼ�Ŀ�� 
     * @param height ָ�������Ƶ����ͼ�ĸ߶ȶ� 
     * @param kind ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND�� 
     *            ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96 
     * @return ָ����С����Ƶ����ͼ 
   * */
  private Bitmap getVideoThumbnail(String videoPath, int width, int height,  
          int kind) {  
      Bitmap bitmap = null;  
      // ��ȡ��Ƶ������ͼ   
      bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
      System.out.println("w"+bitmap.getWidth());  
      System.out.println("h"+bitmap.getHeight());  
      bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
              ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
      return bitmap;  
  }  

}  
