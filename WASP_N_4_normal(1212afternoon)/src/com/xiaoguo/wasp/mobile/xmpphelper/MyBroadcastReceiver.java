package com.xiaoguo.wasp.mobile.xmpphelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.ui.chat.ChatLayoutActivity;

public class MyBroadcastReceiver extends BroadcastReceiver {
	private static int i=0;
	private Context context;

	public MyBroadcastReceiver() {
		super();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constant.NEW_MESSAGE_ACTION.equals(intent.getAction())) {
			i=i++;
//			Toast.makeText(context, "֪ͨ������"+i, Toast.LENGTH_SHORT).show();
			NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			//������Ϣ
			IMMessage message = intent
					.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
			IMMessageDb messageDb = new IMMessageDb(context);
			UserSettingInfo userInfo = new UserSettingInfo(context);
			String friendJid = message.getFromSubJid();
			friendJid = friendJid.substring(0,friendJid.indexOf("@"));
			if(!messageDb.isMessageSaved(userInfo.getAccount(),friendJid,message)){
				saveMessageReceive(message,context);
				
				String str = message.getContent();
				String fromJid = message.getFromSubJid();
				if(fromJid.contains("@")&&fromJid.contains(".")){
					fromJid = fromJid.substring(0, fromJid.indexOf("@"));
				}
				int chatMode = 0;
				String text = fromJid +":"+message.getContent();
				if(str.startsWith(";")){
					chatMode = Integer.parseInt(str.substring(
							str.indexOf(";") + 1, 2));
					if ((chatMode == 0) || (chatMode == 4)) {
						text = fromJid+":"+str.substring(2);
					}else if(chatMode == 1){
						text = fromJid+":"+"[ͼƬ]";
					}else if(chatMode == 2){
						text = fromJid+":"+"[�ļ�]";
					}else if(chatMode == 3){
						text = fromJid+":"+"[����]";
					}
				}
				long time = System.currentTimeMillis();
				Intent i = new Intent(context,ChatLayoutActivity.class);
				Friends friend = new Friends();
				String jid = message.getFromSubJid();
				jid = jid.substring(0, jid.indexOf("@"));
				friend.setJID(jid);
				String userName = message.getFromSubName();
				friend.setName(userName);
				System.out.println("friends="+friend);
				Bundle bd = new Bundle();
				bd.putString("style","from");
				bd.putParcelable("friends", friend);
				System.out.println("10");
				i.putExtra("info", bd);
				PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
				Notification nt = new Notification.Builder(context)
				.setContentTitle("��������Ϣ")
				.setContentText(text)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(time).setDefaults(Notification.DEFAULT_SOUND)
				.setContentIntent(pIntent)
				.build();
				nt.flags = Notification.FLAG_AUTO_CANCEL; 
				mgr.notify(1, nt);
			}
		}
	}
	/**
	 * roster�����һ��subcriber
	 * 
	 * @param user
	 */
	protected void addUserReceive(Friends friends){
		System.out.println("��Ӻ���");
		//Toast.makeText(context, "��Ӻ���", Toast.LENGTH_SHORT).show();
	};
	/**
	 * rosterɾ����һ��subscriber
	 * 
	 * @param user
	 */
	protected  void deleteUserReceive(Friends friends){
		System.out.println("ɾ������");
	//	Toast.makeText(context, "ɾ������", Toast.LENGTH_SHORT).show();
	};
	
	/**
	 * roster�е�һ��subscriber��״̬��Ϣ��Ϣ�����˸ı�
	 * 
	 * @param user
	 */
	protected  void changePresenceReceive(Friends friends){
		System.out.println("�ı��¼״̬");
	//	Toast.makeText(context, "�ı��¼״̬", Toast.LENGTH_SHORT).show();
	};
	
	
	protected  void updateUserReceive(Friends friends){
		System.out.println("�����û�");
		//Toast.makeText(context, "�����û�", Toast.LENGTH_SHORT).show();
	};

	/**
	 * �յ�һ�������������
	 * 
	 * @param subFrom
	 */
	protected  void subscripUserReceive(String subFrom){
		System.out.println("��������");
		//Toast.makeText(context, "��������", Toast.LENGTH_SHORT).show();
		IMMessage message = new IMMessage();
		message.setUnReadCount(0);
		IMMessageDb messageDb = new IMMessageDb(context);
		UserSettingInfo userInfo = new UserSettingInfo(context);
		String friendJid = subFrom;
		System.out.println("friendJid="+friendJid);
		message.setChatMode(5);
		message.setContent(";5������Ӻ���");
		message.setFromSubJid(subFrom);
		System.out.println("subFrom="+subFrom);
		String host=CommandBase.instance().getHost();
		message.setToSubJid(userInfo.getAccount()+"@"+host.substring(0,host.indexOf(":")));
		String time = ConnectionUtils.getStringTime();
		message.setTime(time);
		friendJid = friendJid.substring(0,friendJid.indexOf("@"));
		messageDb.saveMessage(message, friendJid, userInfo.getAccount());
	};
	
	/**
	 * �յ�����Ϣ
	 * 
	 * @param subFrom
	 */
	protected  void saveMessageReceive(IMMessage message,Context context){
		message.setUnReadCount(0);
		IMMessageDb messageDb = new IMMessageDb(context);
		UserSettingInfo userInfo = new UserSettingInfo(context);
		String friendJid = message.getFromSubJid();
		friendJid = friendJid.substring(0,friendJid.indexOf("@"));
		messageDb.saveMessage(message, friendJid, userInfo.getAccount());
	};
}