package com.xiaoguo.wasp.mobile.xmpphelper;


import com.xiaoguo.wasp.mobile.network.ConnectionUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;


/**
 * @author zhanghaitao
 * @date 2011-7-7
 * @version 1.0
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class fileListenerService extends Service {
	private static XmppFileManager _xmppFileMgr;

	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		if (android.os.Build.VERSION.SDK_INT > 9) {    
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();   
			StrictMode.setThreadPolicy(policy); 
		}
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("info","Service Bind Success");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		_xmppFileMgr = new XmppFileManager(getBaseContext());
		_xmppFileMgr.initialize(ConnectionUtils.getConnection(this));
		System.out.println("-----fileservice start");
		return START_STICKY;
	}
	

}
