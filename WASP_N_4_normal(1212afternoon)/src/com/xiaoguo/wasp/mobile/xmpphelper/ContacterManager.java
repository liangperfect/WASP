package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.xiaoguo.wasp.mobile.model.Expert;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;


import android.content.Context;

public class ContacterManager {
	/**
	 * 保存着所有的联系人信息
	 */
	public static Map<String, Friends> contacters = null;

	public static void init(Connection connection) {
		contacters = new HashMap<String, Friends>();
		for (RosterEntry entry : connection.getRoster().getEntries()) {
			contacters.put(entry.getUser(),
					transEntryToUser(entry, connection.getRoster()));
		}
	}
	
	public static void destroy() {
		contacters = null;
	}
	
	/*
	 * 设置联系人列表
	 * 
	 * */
	public static void setContacters(Context context, List<Expert> expertsList){
		contacters = new HashMap<String, Friends>();
		Expert expert = null;
		Friends friends = null;
		String host = CommandBase.instance().getHost();
		host = host.substring(0,host.indexOf(":")); 
		for(int i=0;i<expertsList.size();i++){
			expert = new Expert();
			friends = new Friends();
			if (expert.getExpertName() == null) {
				friends.setName(expert.getExpertAccount());
			} else {
				friends.setName(expert.getExpertName());
			}
			friends.setJID(expert.getExpertAccount()+"@"+host);
			Roster roster = ConnectionUtils.getConnection(context).getRoster();
			Presence presence = roster.getPresence(expert.getExpertAccount()+"@"+host);
			friends.setFrom(presence.getFrom());
			String state = "离线";
			org.jivesoftware.smack.packet.Presence.Mode usMode = presence.getMode();
			if(usMode == org.jivesoftware.smack.packet.Presence.Mode.dnd){
				state = "忙碌";
			}else if(usMode == org.jivesoftware.smack.packet.Presence.Mode.away||
					usMode == org.jivesoftware.smack.packet.Presence.Mode.xa){
				state = "离开";
			}else if(presence.isAvailable()){
				state = "在线";
			}else{
				state = "离线";
			}
			friends.setStatus(state);
			friends.setAvailable(presence.isAvailable());
			contacters.put(expert.getExpertAccount()+"@"+host,
					friends);
		}
		
	}
	/**
	 * 获得所有的联系人列表
	 * 
	 * @return
	 */
	public static List<Friends> getContacterList(Connection connection) {
		init(connection);
		if (contacters == null){
			System.out.println("所有联系人列表为空！");
			throw new RuntimeException("contacters is null");
		}

		List<Friends> friendsList = new ArrayList<Friends>();

		for (String key : contacters.keySet())
			friendsList.add(contacters.get(key));

		return friendsList;
	}

	/**
	 * 获得所有未分组的联系人列表
	 * 
	 * @return
	 */
	public static List<Friends> getNoGroupUserList(Roster roster) {
		List<Friends> friendsList = new ArrayList<Friends>();

		// 服务器的用户信息改变后，不会通知到unfiledEntries
		for (RosterEntry entry : roster.getUnfiledEntries()) {
			friendsList.add(contacters.get(entry.getUser()).clone());
		}

		return friendsList;
	}

	/* 获取某个组里面的所有好友 
    *  
    * @param roster 
    * @param groupName 
    *            组名 
    * @return 
    */  
   public static List<RosterEntry> getEntriesByGroup(Roster roster,  
           String groupName) {  
       List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();  
       RosterGroup rosterGroup = roster.getGroup(groupName);  
       Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();  
       Iterator<RosterEntry> i = rosterEntry.iterator();  
       while (i.hasNext()) {  
           Entrieslist.add(i.next());  
       }  
       return Entrieslist;  
   } 
	/**
	 * 获得所有分组联系人
	 * 
	 * @return
	 */
	public static List<MRosterGroup> getGroups(Roster roster,Connection connection) {
		init(connection);
		if (contacters == null){
			System.out.println("所有分组联系人为空");
			throw new RuntimeException("contacters is null");
		}
		List<MRosterGroup> groups = new ArrayList<ContacterManager.MRosterGroup>();

		groups.add(new MRosterGroup("所有好友", getContacterList(connection)));
		groups.add(new MRosterGroup("未分组好友", getNoGroupUserList(roster)));

		for (RosterGroup group : roster.getGroups()) {
			List<Friends> groupUsers = new ArrayList<Friends>();
			for (RosterEntry entry : group.getEntries()) {
				groupUsers.add(contacters.get(entry.getUser()));
			}
			System.out.println("好有列表：");
			for(int i=0;i<groupUsers.size();i++){
				System.out.println(groupUsers.get(i).getName());
			}
			groups.add(new MRosterGroup(group.getName(), groupUsers));
		}

		return groups;
	}

	/**
	 * 根据RosterEntry创建一个User
	 * 
	 * @param entry
	 * @return
	 */
	public static Friends transEntryToUser(RosterEntry entry, Roster roster) {
		Friends friends = new Friends();
		if (entry.getName() == null) {
			friends.setName(entry.getUser());
		} else {
			friends.setName(entry.getName());
		}
		friends.setJID(entry.getUser());
		Presence presence = roster.getPresence(entry.getUser());
		friends.setFrom(presence.getFrom());
		String state = "离线";
		org.jivesoftware.smack.packet.Presence.Mode usMode = presence.getMode();
		if(usMode == org.jivesoftware.smack.packet.Presence.Mode.dnd){
			state = "忙碌";
		}else if(usMode == org.jivesoftware.smack.packet.Presence.Mode.away||
				usMode == org.jivesoftware.smack.packet.Presence.Mode.xa){
			state = "离开";
		}else if(presence.isAvailable()){
			state = "在线";
		}else{
			state = "离线";
		}
//		friends.setStatus(presence.getStatus());
		friends.setStatus(state);
		friends.setSize(entry.getGroups().size());
		friends.setAvailable(presence.isAvailable());
		friends.setType(entry.getType());
		return friends;
	}

	/**
	 * 修改这个好友的昵称
	 * 
	 * @param user
	 * @param nickname
	 */
	public static void setNickname(Friends friends, String nickname,
			XMPPConnection connection) {
		RosterEntry entry = connection.getRoster().getEntry(friends.getJID());
		entry.setName(nickname);
	}

	/**
	 * 把一个好友添加到一个组中
	 * 
	 * @param user
	 * @param groupName
	 */
	public static void addUserToGroup(final Friends friends, final String groupName,
			final XMPPConnection connection) {
		if (groupName == null || friends == null)
			return;
		// 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
		new Thread() {
			public void run() {
				RosterGroup group = connection.getRoster().getGroup(groupName);
				// 这个组已经存在就添加到这个组，不存在创建一个组
				RosterEntry entry = connection.getRoster().getEntry(
						friends.getJID());
				try {
					if (group != null) {
						if (entry != null)
							group.addEntry(entry);
					} else {
						RosterGroup newGroup = connection.getRoster()
								.createGroup(groupName);
						if (entry != null)
							newGroup.addEntry(entry);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 把一个好友从组中删除
	 * 
	 * @param user
	 * @param groupName
	 */
	public static void removeUserFromGroup(final Friends friends,
			final String groupName, final XMPPConnection connection) {
		if (groupName == null || friends == null)
			return;
		new Thread() {
			public void run() {
				RosterGroup group = connection.getRoster().getGroup(groupName);
				if (group != null) {
					try {
						RosterEntry entry = connection.getRoster().getEntry(
								friends.getJID());
						if (entry != null)
							group.removeEntry(entry);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public static class MRosterGroup {
		private String name;
		private List<Friends> friends;

		public MRosterGroup(String name, List<Friends> friends) {
			this.name = name;
			this.friends = friends;
		}

		public int getCount() {
			if (friends != null)
				return friends.size();
			return 0;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Friends> getFriends() {
			return friends;
		}

		public void setFriends(List<Friends> friends) {
			this.friends = friends;
		}

	}

}
