package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.content.Context;

import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.utils.SHA1Util;

public class MyConnectionListener implements ConnectionListener {
	Context context=null;
	private Timer tExit;  
    private String username;  
    private String password;  
    private int logintime = 2000; 
    
	public MyConnectionListener(Context context, String username,
			String password) {
		super();
		this.context = context;
		this.username = username;
		this.password = password;
	}

	@Override
	public void connectionClosed() {
		ConnectionUtils.getConnection(context).disconnect(); 
		tExit = new Timer();  
	    tExit.schedule(new timetask(), logintime);  
		
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		 boolean error = e.getMessage().equals("stream:error (conflict)");  
	        if (!error) {  
	            // 關閉連接   
	        	ConnectionUtils.getConnection(context).disconnect();  
	            // 重连服务器   
	            tExit = new Timer();  
	            tExit.schedule(new timetask(), logintime);  
	        }  
	}
	class timetask extends TimerTask {  
        @Override  
        public void run() {  
            if (username != null && password != null) {  
                // 连接服务器   
            	try {
            		//判断网络状态，如果网络状态良好则重连，否则提示用户网络状况不好
            		Activity activity = (Activity)context;
            		if(NetWorkDetect.detect(activity)){
            			ConnectionUtils.getConnection(context).login(username, SHA1Util.getSHA1EncString(password));
            		}else{
            		}
				} catch (XMPPException e) {
					e.printStackTrace();
				}
        	 if (ConnectionUtils.getConnection(context) 
						.isAuthenticated()) {  
                } else {  
                    tExit.schedule(new timetask(), logintime);  
                }  
            }  
        }  
    }
	
	@Override
	public void reconnectingIn(int arg0) {
		
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		
	}

	@Override
	public void reconnectionSuccessful() {
		
	}

}
