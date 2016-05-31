package com.xiaoguo.wasp.mobile.ui.chat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.utils.ComPressUtils;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class PictureActivity extends Activity implements OnClickListener{
	private ImageView pictureView;
	private LinearLayout saveLayout;
	private ImageView saveView;
	private ImageView picUpView;
	private ImageView picDownView;
	private int isSave=0;//Ϊ0��ʾδ���棬Ϊ1��ʾ�Դ洢
	Bitmap bitmap=null;
	String style=null;
	String picUrl=null;
	MyBroadcastReceiver receiver = null;
	
	private int id=0;   
	private int displayWidth;   
	private int displayHeight;   
	private float scaleWidth=1;   
	private float scaleHeight=1;
	private LinearLayout layout1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);
		
		WASPApplication.getInstance().addActivity(this);
		
		Bundle bd = this.getIntent().getBundleExtra("pic");
		style = bd.getString("from");
		if(style.equals("from")){
			byte[] bis = bd.getByteArray("bit");
			bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
		}else{
			picUrl = bd.getString("url");
			bitmap=ComPressUtils.getSmallBitmap(picUrl);
		}

		 /* ȡ����Ļ�ֱ��ʴ�С */     
		DisplayMetrics dm=new DisplayMetrics();     
		 getWindowManager().getDefaultDisplay().getMetrics(dm);    
		 displayWidth=dm.widthPixels;    
		 /* ��Ļ�߶���۳��·�Button�߶� */     
		 displayHeight=dm.heightPixels; 
		init();
	}

	private void init() {
		pictureView  = (ImageView)findViewById(R.id.picture_display);
		
		if(bitmap!=null){
			pictureView.setImageBitmap(bitmap);
		}else{
			pictureView.setVisibility(View.GONE);
			saveLayout.setVisibility(View.GONE);
			Toast.makeText(this, "ͼƬ�ļ�Ϊ�գ�û��Ԥ����", Toast.LENGTH_SHORT).show();
		}
		pictureView.setOnClickListener(this);
		
		saveLayout = (LinearLayout)findViewById(R.id.save_layout);
		saveView = (ImageView)findViewById(R.id.save_pic);
		picDownView = (ImageView)findViewById(R.id.pic_down);
		picUpView = (ImageView)findViewById(R.id.pic_up);
		picDownView.setOnClickListener(this);
		picUpView.setOnClickListener(this);
		layout1 = (LinearLayout)findViewById(R.id.layout_pic);
		if(style.equals("from")){
			saveView.setOnClickListener(this);
		}else{
			saveView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.picture_display:
			ComPressUtils.recycle(bitmap);
			this.finish();
			break;
		case R.id.pic_down:
				small();
			break;
		case R.id.pic_up:
			big();
			break;
		case R.id.save_pic:
			System.out.println("isSave="+isSave);
			if(isSave==0){
				Toast.makeText(this, "�����У�", Toast.LENGTH_SHORT).show();
				int i = savePic();
				if(i==1){
					Toast.makeText(this, "����ɹ���", Toast.LENGTH_SHORT).show();
					isSave = 1;
					ComPressUtils.recycle(bitmap);
					this.finish();
				}else{
					Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "��ͼƬ�Ѿ����棡", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	private void big() {
		picDownView.setEnabled(true);
		int bmpWidth=bitmap.getWidth();     
		int bmpHeight=bitmap.getHeight();     
		/* ����ͼƬ�Ŵ�ı��� */     
		double scale=1.25;      
		/* �������Ҫ�Ŵ�ı��� */     
		scaleWidth=(float)(scaleWidth*scale);     
		scaleHeight=(float)(scaleHeight*scale);           
		/* ����reSize���Bitmap���� */     
		Matrix matrix = new Matrix();       
		matrix.postScale(scaleWidth, scaleHeight);      
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth,bmpHeight,matrix,true);             
		if(id==0) {       
			/* ����ǵ�һ�ΰ�����ɾ��ԭ�����õ�ImageView */      
			layout1.removeView(pictureView);    
		}else{       
			/* ������ǵ�һ�ΰ�����ɾ���ϴηŴ���С��������ImageView */      
			layout1.removeView((ImageView)findViewById(id));    
		}     
		/* �����µ�ImageView������reSize��Bitmap�����ٷ���Layout�� */    
		id++;     
		ImageView imageView = new ImageView(PictureActivity.this);  
		LinearLayout.LayoutParams params =new 	LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);			
		imageView.setLayoutParams(params);
		imageView.setId(id);     
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0){
				ComPressUtils.recycle(bitmap);
				PictureActivity.this.finish();
			}
		});
		imageView.setImageBitmap(resizeBmp);     
		layout1.addView(imageView);      
		setContentView(layout1);            
		/* ����ٷŴ�ᳬ����Ļ��С���Ͱ�Button disable */    
		if(scaleWidth*scale*bmpWidth>displayWidth||         
				scaleHeight*scale*bmpHeight>displayHeight){       
			picUpView.setEnabled(false);    
		} 
	}

	private void small() {
		 int bmpWidth=bitmap.getWidth();     
		 int bmpHeight=bitmap.getHeight();    
		 /* ����ͼƬ��С�ı��� */     
		 double scale=0.8;       
		 /* ��������Ҫ��С�ı��� */    
		 scaleWidth=(float) (scaleWidth*scale);     
		 scaleHeight=(float) (scaleHeight*scale);           
		 /* ����reSize���Bitmap���� */     
		 Matrix matrix = new Matrix();      
		 matrix.postScale(scaleWidth, scaleHeight);      
		 Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth, bmpHeight,matrix,true);            
		 if(id==0) {      
			 /* ����ǵ�һ�ΰ�����ɾ��ԭ��Ĭ�ϵ�ImageView */      
			 layout1.removeView(pictureView);    
		 } else {       
			 /* ������ǵ�һ�ΰ�����ɾ���ϴηŴ���С��������ImageView */       
			 layout1.removeView((ImageView)findViewById(id));   
		 }     
		 /* �����µ�ImageView������reSize��Bitmap�����ٷ���Layout�� */   
		 id++;     
		 ImageView imageView = new ImageView(PictureActivity.this);      
		 imageView.setId(id);    
		 imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0){
					ComPressUtils.recycle(bitmap);
					PictureActivity.this.finish();
				}
			});
		 imageView.setImageBitmap(resizeBmp); 
		 layout1.addView(imageView);      
		 setContentView(layout1);          
		 /* ��ΪͼƬ�ŵ����ʱ�Ŵ�ť��disable����������Сʱ��������Ϊenable */     
		 if(scaleWidth*scale*bmpWidth<displayWidth/3
				 ||scaleHeight*scale*bmpHeight>displayHeight/3){
			 picDownView.setEnabled(false); 
		 }
		 picUpView.setEnabled(true);
	}

	@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
	private int savePic() {
		int i=0;
		String sdStatus = Environment.getExternalStorageState();  
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����   
			File SAVE_PHOTO_DIR = new File("/sdcard/WASP/receive/pictures");
			if(!SAVE_PHOTO_DIR.exists()){
				SAVE_PHOTO_DIR.mkdirs();
			}
			System.out.println("path="+SAVE_PHOTO_DIR.getPath());
			Date date = new Date(System.currentTimeMillis());   
	        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");   
	        String fileName="/sdcard/WASP/receive/pictures/"+dateFormat.format(date) + ".jpg";
			try {   
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
				File file = new File(fileName);
				if(file.exists()){
					i=1;
				}
			} catch (IOException e) {   
				e.printStackTrace();  
				i = 0;
			}   
        }else{
        	Toast.makeText(this, "û��SD����", Toast.LENGTH_SHORT).show();
        }
		return i;
	}
//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(PictureActivity.this);
//		IntentFilter filter = new IntentFilter();
//		
//		filter.addAction(Constant.ROSTER_ADDED);
//		filter.addAction(Constant.ROSTER_DELETED);
//		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
//		filter.addAction(Constant.ROSTER_UPDATED);
//		// ��������
//		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
//		filter.addAction(Constant.NEW_MESSAGE_ACTION);
//		registerReceiver(receiver, filter);
//		super.onResume();
//	}	
}
