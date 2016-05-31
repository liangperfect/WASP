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
	private int isSave=0;//为0表示未保存，为1表示以存储
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

		 /* 取得屏幕分辨率大小 */     
		DisplayMetrics dm=new DisplayMetrics();     
		 getWindowManager().getDefaultDisplay().getMetrics(dm);    
		 displayWidth=dm.widthPixels;    
		 /* 屏幕高度须扣除下方Button高度 */     
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
			Toast.makeText(this, "图片文件为空，没有预览！", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "保存中！", Toast.LENGTH_SHORT).show();
				int i = savePic();
				if(i==1){
					Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
					isSave = 1;
					ComPressUtils.recycle(bitmap);
					this.finish();
				}else{
					Toast.makeText(this, "保存失败！", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "该图片已经保存！", Toast.LENGTH_SHORT).show();
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
		/* 设置图片放大的比例 */     
		double scale=1.25;      
		/* 计算这次要放大的比例 */     
		scaleWidth=(float)(scaleWidth*scale);     
		scaleHeight=(float)(scaleHeight*scale);           
		/* 产生reSize后的Bitmap对象 */     
		Matrix matrix = new Matrix();       
		matrix.postScale(scaleWidth, scaleHeight);      
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth,bmpHeight,matrix,true);             
		if(id==0) {       
			/* 如果是第一次按，就删除原来设置的ImageView */      
			layout1.removeView(pictureView);    
		}else{       
			/* 如果不是第一次按，就删除上次放大缩小所产生的ImageView */      
			layout1.removeView((ImageView)findViewById(id));    
		}     
		/* 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 */    
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
		/* 如果再放大会超过屏幕大小，就把Button disable */    
		if(scaleWidth*scale*bmpWidth>displayWidth||         
				scaleHeight*scale*bmpHeight>displayHeight){       
			picUpView.setEnabled(false);    
		} 
	}

	private void small() {
		 int bmpWidth=bitmap.getWidth();     
		 int bmpHeight=bitmap.getHeight();    
		 /* 设置图片缩小的比例 */     
		 double scale=0.8;       
		 /* 计算出这次要缩小的比例 */    
		 scaleWidth=(float) (scaleWidth*scale);     
		 scaleHeight=(float) (scaleHeight*scale);           
		 /* 产生reSize后的Bitmap对象 */     
		 Matrix matrix = new Matrix();      
		 matrix.postScale(scaleWidth, scaleHeight);      
		 Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth, bmpHeight,matrix,true);            
		 if(id==0) {      
			 /* 如果是第一次按，就删除原来默认的ImageView */      
			 layout1.removeView(pictureView);    
		 } else {       
			 /* 如果不是第一次按，就删除上次放大缩小所产生的ImageView */       
			 layout1.removeView((ImageView)findViewById(id));   
		 }     
		 /* 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 */   
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
		 /* 因为图片放到最大时放大按钮会disable，所以在缩小时把他重设为enable */     
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
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用   
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
        	Toast.makeText(this, "没有SD卡！", Toast.LENGTH_SHORT).show();
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
//		// 好友请求
//		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
//		filter.addAction(Constant.NEW_MESSAGE_ACTION);
//		registerReceiver(receiver, filter);
//		super.onResume();
//	}	
}
