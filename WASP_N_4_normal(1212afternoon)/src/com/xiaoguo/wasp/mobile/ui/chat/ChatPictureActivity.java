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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChatPictureActivity extends Activity implements OnClickListener,OnTouchListener{
	private long firstClick;
	private long lastClick;
	private int count;
	ImageView imageView;
	Bitmap bitmap=null;
	
	String style=null;
	String picUrl=null;
	private Matrix matrix = new Matrix();
	DisplayMetrics dm;
	
    Matrix savedMatrix = new Matrix();

    float minScaleR;// ��С���ű���
    static final float MAX_SCALE = 4f;// ������ű���

    static final int NONE = 0;// ��ʼ״̬
    static final int DRAG = 1;// �϶�
    static final int ZOOM = 2;// ����
    int mode = NONE;

    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    private int isSave=0;//Ϊ0��ʾδ���棬Ϊ1��ʾ�Դ洢
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_pic);
        
        WASPApplication.getInstance().addActivity(this);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
        
		Bundle bd = this.getIntent().getBundleExtra("pic");
		style = bd.getString("from");
		if(style.equals("from")){
			byte[] bis = bd.getByteArray("bit");
			bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
		}else{
			picUrl = bd.getString("url");
			bitmap=ComPressUtils.getSmallBitmap(picUrl);
		}
		
        imageView = (ImageView) findViewById(R.id.chat_img);
        if(bitmap!=null){
        	imageView.setImageBitmap(bitmap);
		}else{
			imageView.setVisibility(View.GONE);
			Toast.makeText(this, "ͼƬ�ļ�Ϊ�գ�û��Ԥ����", Toast.LENGTH_SHORT).show();
		}
        imageView.setScaleType(ScaleType.MATRIX);
        imageView.setOnTouchListener(this);
        imageView.setOnClickListener(this);
        minZoom();
        center();
        imageView.setImageMatrix(matrix);
    }
    /**
     * ��������
     */
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        // ���㰴��
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            prev.set(event.getX(), event.getY());
            mode = DRAG;
            break;
        // ���㰴��
        case MotionEvent.ACTION_POINTER_DOWN:
            dist = spacing(event);
            // �����������������10�����ж�Ϊ���ģʽ
            if (spacing(event) > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - prev.x, event.getY()
                        - prev.y);
            } else if (mode == ZOOM) {
                float newDist = spacing(event);
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    float tScale = newDist / dist;
                    matrix.postScale(tScale, tScale, mid.x, mid.y);
                }
            }
            break;
        }
        imageView.setImageMatrix(matrix);
        CheckView();
        return true;
    }

    /**
     * ���������С���ű������Զ�����
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * ��С���ű��������Ϊ100%
     */
    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }
    /**
     * ����ľ���
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * ������е�
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    /*private class TounchListener implements OnTouchListener{
    	
    	private PointF startPoint = new PointF();
    	private Matrix currentMaritx = new Matrix();
    	
    	private int mode = 0;//���ڱ��ģʽ
    	private static final int DRAG = 1;//�϶�
    	private static final int ZOOM = 2;//�Ŵ�
    	private float startDis = 0;
    	private PointF midPoint;//���ĵ�

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
					count = 0;
				}
				count++;
				if (count == 1) {
					firstClick = System.currentTimeMillis();
				} else if (count == 2) {
					lastClick = System.currentTimeMillis();
					// ���ε��С��300ms Ҳ�����������
					if (lastClick - firstClick < 300) {// �ж��Ƿ���ִ����˫���¼�
						System.out.println(">>>>>>>>ִ����˫���¼�");
						float endDis = distance(event);//��������
						if(endDis > 10f){
							float scale = endDis / startDis;//�Ŵ���
							matrix.set(currentMaritx);
							matrix.postScale(scale, scale, midPoint.x, midPoint.y);
						}
					}
				}

				mode = DRAG;
				currentMaritx.set(imageView.getImageMatrix());//��¼ImageView���ڵ��ƶ�λ��
				startPoint.set(event.getX(),event.getY());//��ʼ��
				break;

			case MotionEvent.ACTION_MOVE://�ƶ��¼�
				if (mode == DRAG) {//ͼƬ�϶��¼�
					float dx = event.getX() - startPoint.x;//x���ƶ�����
					float dy = event.getY() - startPoint.y;
					matrix.set(currentMaritx);//�ڵ�ǰ��λ�û������ƶ�
					matrix.postTranslate(dx, dy);
					
				} else if(mode == ZOOM){//ͼƬ�Ŵ��¼�
					float endDis = distance(event);//��������
					if(endDis > 10f){
						float scale = endDis / startDis;//�Ŵ���
						matrix.set(currentMaritx);
						matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					}
					
					
				}

				break;
				
			case MotionEvent.ACTION_UP:
				mode = 0;
				break;
			//����ָ�뿪��Ļ������Ļ���д���(��ָ)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			//����Ļ���Ѿ��д��㣨��ָ��,����һ����ָѹ����Ļ
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = ZOOM;
				startDis = distance(event);
				
				if(startDis > 10f){//������ָ����������
					midPoint = mid(event);
					currentMaritx.set(imageView.getImageMatrix());//��¼��ǰ�����ű���
				}
				
				break;


			}
			imageView.setImageMatrix(matrix);
			return true;
		}
    	
    }*/

    /**
     * ����֮��ľ���
     * @param event
     * @return
     */
    private static float distance(MotionEvent event){
    	//�����ߵľ���
    	float dx = event.getX(1) - event.getX(0);
    	float dy = event.getY(1) - event.getY(0);
    	return FloatMath.sqrt(dx*dx + dy*dy);
    }
    /**
     * ��������֮�����ĵ�ľ���
     * @param event
     * @return
     */
    private static PointF mid(MotionEvent event){
    	float midx = event.getX(1) + event.getX(0);
    	float midy = event.getY(1) - event.getY(0);
    	
    	return new PointF(midx/2, midy/2);
    }
    private void center() {
        center(true, true);
    }

    /**
     * �����������
     */
    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imageView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat_img:
//			Toast.makeText(ChatPictureActivity.this, "�����", Toast.LENGTH_SHORT).show();
			ComPressUtils.recycle(bitmap);
			this.finish();
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if(style.equals("from")){
			menu.add(0, 0, 0, "����");
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if(style.equals("from")){
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
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(style.equals("from")){
			menu.add(0, 0, 0, "����");
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if(style.equals("from")){
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
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}*/
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
}
