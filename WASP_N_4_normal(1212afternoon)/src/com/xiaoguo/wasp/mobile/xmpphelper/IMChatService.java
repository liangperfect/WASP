package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.ArrayList;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;

/*
 * 即时通讯聊天服务
 * 能随时接收消息
 * */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class IMChatService extends Service {

	ChatManager chatManager = null;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		if (android.os.Build.VERSION.SDK_INT > 9) {    
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();   
			StrictMode.setThreadPolicy(policy); 
		}
		initChatManager();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public void onDestroy() {
		MChatManager.message_pool = null;
		super.onDestroy();
	}

	private void initChatManager() {
		chatManager = ConnectionUtils.getConnection(this).getChatManager();
		chatManager.addChatListener(chatListener);
		MChatManager.chatThreads = new HashMap<String, String>();
		MChatManager.message_pool = new ArrayList<IMMessage>();
	}

	private ChatManagerListener chatListener = new ChatManagerListener() {

		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			MChatManager.chatThreads.put(chat.getParticipant(),
					chat.getThreadID());
			// 创建消息的监听
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat chat, Message message) {
					IMMessage msg = new IMMessage();
					String time = (String) message.getProperty(IMMessage.KEY_TIME);
					msg.setTime(time == null ? ConnectionUtils.getStringTime()
							: time);
					msg.setContent(message.getBody());
					if (Message.Type.error == message.getType()) {
						msg.setType(IMMessage.ERROR);
					} else {
						msg.setType(IMMessage.SUCCESS);
					}
					msg.setFromSubJid(message.getFrom().split("/")[0]);

					MChatManager.message_pool.add(msg);

					Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
					intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
					sendBroadcast(intent);
				}
			});
		}
	};

}
