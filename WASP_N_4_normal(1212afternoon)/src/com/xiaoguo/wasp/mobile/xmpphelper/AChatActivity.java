package com.xiaoguo.wasp.mobile.xmpphelper;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.utils.Byte2KB;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

/*
 * 自定义聊天界面抽象类
 * 需要实现
 * */
public abstract class AChatActivity extends BaseActivity {

	private Chat chat = null;
	private List<IMMessage> message_pool = null;
	Friends friends = null;
	UserSettingInfo userInfo = null;
	IMMessageDb messageDb = null;
	String temp = "";
	int offset = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInfo = new UserSettingInfo(this);
		Bundle b = getIntent().getBundleExtra("info");
		String style = b.getString("style");
		System.out.println("style=" + style);
		if (style.equals("crowd")) {

		} else {
			friends = b.getParcelable("friends");
			System.out.println("Achat--->friends:" + friends);
		}
		if (friends == null)
			return;
		String friendsid = friends.getJID();
		String host = CommandBase.instance().getHost();
		friendsid = friendsid + "@" + host.substring(0, host.indexOf(":"));
		String threadId = MChatManager.chatThreads.get(friendsid);
		chat = ConnectionUtils.getConnection(this).getChatManager()
				.getThreadChat(threadId);
		if (chat == null) {
			chat = ConnectionUtils.getConnection(this).getChatManager()
					.createChat(friendsid, null);
		}
		messageDb = new IMMessageDb(AChatActivity.this);
		System.out.println("friends的id:" + friendsid);
		temp = friends.getJID();
		// 获取所有的消息
		// message_pool = messageDb.getAllMessage(userInfo.getAccount(), temp,
		// null);
		// 获取5条消息
		message_pool = messageDb.getSomeMessage(userInfo.getAccount(), temp,
				null, offset, 10);
		offset = message_pool.size();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		registerReceiver(receiver, filter);
		super.onResume();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constant.NEW_MESSAGE_ACTION.equals(intent.getAction())) {
				System.out.println("hhahahahhahah");
				NotificationManager mgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification nt = new Notification();
				nt.defaults = Notification.DEFAULT_SOUND;
				int soundId = new Random(System.currentTimeMillis())
						.nextInt(Integer.MAX_VALUE);
				mgr.notify(soundId, nt);

				IMMessage message = intent
						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
				System.out.println("Achat---" + message.getChatMode());
				System.out.println("friends.getJID()=" + friends.getJID());
				String temp = friends.getJID();
				String tempFriend = message.getFromSubJid();
				temp = tempFriend.substring(0, tempFriend.indexOf("@"));
				System.out.println("temp="+temp);
				System.out.println("chat.getParticipant()="+chat.getParticipant());
				System.out.println("tempFriend="+tempFriend);
				if(chat.getParticipant().contains(temp)){
					message_pool.add(message);
				}else{
					message.setUnReadCount(0);
				}
				messageDb.saveMessage(message, temp, userInfo.getAccount());
				receiveNewMessage(message);
				refreshMessage(message_pool);
			}
		}
	};

	protected abstract void receiveNewMessage(IMMessage message);

	protected abstract void refreshMessage(List<IMMessage> messages);

	protected List<IMMessage> getMessages(String type) {
		if (type.equals("refresh")) {
			offset = message_pool.size();
			System.out.println("refresh offset:" + offset);
			List<IMMessage> localMsg = messageDb.getSomeMessage(
					userInfo.getAccount(), temp, null, offset, 5);
			for (int i = 0; i < localMsg.size(); i++) {
				message_pool.add(i, localMsg.get(i));
			}
		}
		return message_pool;
	}

	// 发送消息分文：文字（表情）消息，图片消息，语音消息，视频消息，分别对应0,1,2,3
	protected void sendMessage(String messageContent, String infoUrl, int type) {
		try {
			String time = ConnectionUtils.getStringTime();
			Message message = new Message();
			message.setProperty(IMMessage.KEY_TIME, time);
			message.setBody(messageContent);
			if (ConnectionUtils.getConnection(MContext).isConnected()) {
				chat.sendMessage(message);

				IMMessage newMessage = new IMMessage();
				newMessage.setMsgType(1);
				System.out.println("chat.getParticipant()="
						+ chat.getParticipant());
				newMessage.setToSubJid(chat.getParticipant());
				newMessage.setContent(messageContent);
				newMessage.setTime(time);
				newMessage.setChatMode(type);
				newMessage.setInfoUrl(infoUrl);
				// 只要是发出去的消息，都是未读消息
				newMessage.setUnReadCount(0);

				System.out
						.println("newMessageType=" + newMessage.getChatMode());
				System.out.println("infoUrl=" + infoUrl);
				System.out.println("发送给好有：" + friends.getJID());
				String jid = friends.getJID();
				System.out.println("处理后的好有：" + jid);
				messageDb.saveMessage(newMessage, jid, userInfo.getAccount());
				message_pool.add(newMessage);
				MChatManager.message_pool.add(newMessage);
				// 刷新视图
				refreshMessage(message_pool);
			} else {
				Toast.makeText(MContext, "您已经处于离线状态", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 发送文件
	 */
	protected void sendFile(String jid, File file1, XMPPConnection connection) {
		String filePath = file1.getPath();
		String kbs = Byte2KB.bytes2kb(file1.length());
		System.out.println("kbs=" + kbs);
		String messageContent = kbs + ";"
				+ filePath.substring(filePath.lastIndexOf("/") + 1);
		sendMessage(messageContent, filePath, 2);

		FileTransferManager manager = new FileTransferManager(connection);
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(jid
				+ "/Spark 2.6.3");
		long timeOut = 1000000;
		long sleepMin = 3000;
		long spTime = 0;
		int rs = 0;
		if (file1.exists()) {
			System.out.println("file_size=" + file1.length());
		}
		try {
			transfer.sendFile(file1, "send");
			rs = transfer.getStatus().compareTo(FileTransfer.Status.complete);
			spTime = spTime + sleepMin;
			if (spTime > timeOut) {
				return;
			}
			try {
				Thread.sleep(sleepMin);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
