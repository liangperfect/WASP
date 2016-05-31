package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
/*
 * 即时通信联系人服务
 * 能获取联系人状态
 * */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class IMContactService extends Service {

	private Roster roster = null;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		if (android.os.Build.VERSION.SDK_INT > 9) {    
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();   
			StrictMode.setThreadPolicy(policy); 
		}
		
		addSubscriptionListener();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		init();
		return super.onStartCommand(intent, flags, startId);
	}

	private void init() {
		initRoster();
	}

	/**
	 * 添加一个监听，监听好友添加请求。
	 */
	private void addSubscriptionListener() {
		PacketFilter filter = new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				if (packet instanceof Presence) {
					Presence presence = (Presence) packet;
					if (presence.getType().equals(Presence.Type.subscribe)) {
						return true;
					}
				}
				return false;
			}
		};
		ConnectionUtils.getConnection(this).addPacketListener(
				subscriptionPacketListener, filter);
	}

	/**
	 * 初始化花名册 服务重启时，更新花名册
	 */
	private void initRoster() {
		roster = ConnectionUtils.getConnection(this).getRoster();
		roster.removeRosterListener(rosterListener);
		roster.addRosterListener(rosterListener);
		ContacterManager.init(ConnectionUtils.getConnection(this));
	}

	private PacketListener subscriptionPacketListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			// 如果是自动接收所有请求，则回复一个添加信息
			if (Roster.getDefaultSubscriptionMode().equals(
					SubscriptionMode.accept_all)) {
				Presence subscription = new Presence(Presence.Type.subscribe);
				subscription.setTo(packet.getFrom());
				ConnectionUtils.getConnection(IMContactService.this).sendPacket(subscription);
			} else {
				Intent intent = new Intent();
				intent.setAction(Constant.ROSTER_SUBSCRIPTION);
				intent.putExtra(Constant.ROSTER_SUB_FROM, packet.getFrom());
				sendBroadcast(intent);
			}
		}
	};

	@Override
	public void onDestroy() {
		// 释放资源
		ConnectionUtils.getConnection(this).removePacketListener(
				subscriptionPacketListener);
		ContacterManager.destroy();
		super.onDestroy();
	}

	private RosterListener rosterListener = new RosterListener() {

		@Override
		public void presenceChanged(Presence presence) {
			Intent intent = new Intent();
			intent.setAction(Constant.ROSTER_PRESENCE_CHANGED);
			String subscriber = presence.getFrom().substring(0,
					presence.getFrom().indexOf("/"));
			RosterEntry entry = roster.getEntry(subscriber);
				// 将状态改变之前的user广播出去
				intent.putExtra(Friends.userKey,
						ContacterManager.contacters.get(subscriber));
				ContacterManager.contacters.remove(subscriber);
				ContacterManager.contacters.put(subscriber,
						ContacterManager.transEntryToUser(entry, roster));
//			}
			sendBroadcast(intent);
		}

		@Override
		public void entriesUpdated(Collection<String> addresses) {
			for (String address : addresses) {
				Intent intent = new Intent();
				intent.setAction(Constant.ROSTER_UPDATED);
				// 获得状态改变的entry
				RosterEntry friendsEntry = roster.getEntry(address);
				Friends friends = ContacterManager
						.transEntryToUser(friendsEntry, roster);
				if (ContacterManager.contacters.get(address) != null) {
					// 这里发布的是更新前的user
					intent.putExtra(Friends.userKey, ContacterManager.contacters.get(address));
					// 将发生改变的用户更新到userManager
					ContacterManager.contacters.remove(address);
					ContacterManager.contacters.put(address, friends);
				}
				sendBroadcast(intent);
				// 用户更新，getEntries会更新
			}
		}

		@Override
		public void entriesDeleted(Collection<String> addresses) {
			for (String address : addresses) {
				Intent intent = new Intent();
				intent.setAction(Constant.ROSTER_DELETED);
				Friends friends = null;
				if (ContacterManager.contacters.containsKey(address)) {
					friends = ContacterManager.contacters.get(address);
					ContacterManager.contacters.remove(address);
				}
				intent.putExtra(Friends.userKey, friends);
				sendBroadcast(intent);
			}
		}

		@Override
		public void entriesAdded(Collection<String> addresses) {
			for (String address : addresses) {
				Intent intent = new Intent();
				intent.setAction(Constant.ROSTER_ADDED);
				RosterEntry userEntry = roster.getEntry(address);
				Friends friends = ContacterManager
						.transEntryToUser(userEntry, roster);
				ContacterManager.contacters.put(address, friends);
				intent.putExtra(Friends.userKey, friends);
				sendBroadcast(intent);
			}
		}
	};

}
