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
	 * ���������е���ϵ����Ϣ
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
	 * ������ϵ���б�
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
			String state = "����";
			org.jivesoftware.smack.packet.Presence.Mode usMode = presence.getMode();
			if(usMode == org.jivesoftware.smack.packet.Presence.Mode.dnd){
				state = "æµ";
			}else if(usMode == org.jivesoftware.smack.packet.Presence.Mode.away||
					usMode == org.jivesoftware.smack.packet.Presence.Mode.xa){
				state = "�뿪";
			}else if(presence.isAvailable()){
				state = "����";
			}else{
				state = "����";
			}
			friends.setStatus(state);
			friends.setAvailable(presence.isAvailable());
			contacters.put(expert.getExpertAccount()+"@"+host,
					friends);
		}
		
	}
	/**
	 * ������е���ϵ���б�
	 * 
	 * @return
	 */
	public static List<Friends> getContacterList(Connection connection) {
		init(connection);
		if (contacters == null){
			System.out.println("������ϵ���б�Ϊ�գ�");
			throw new RuntimeException("contacters is null");
		}

		List<Friends> friendsList = new ArrayList<Friends>();

		for (String key : contacters.keySet())
			friendsList.add(contacters.get(key));

		return friendsList;
	}

	/**
	 * �������δ�������ϵ���б�
	 * 
	 * @return
	 */
	public static List<Friends> getNoGroupUserList(Roster roster) {
		List<Friends> friendsList = new ArrayList<Friends>();

		// ���������û���Ϣ�ı�󣬲���֪ͨ��unfiledEntries
		for (RosterEntry entry : roster.getUnfiledEntries()) {
			friendsList.add(contacters.get(entry.getUser()).clone());
		}

		return friendsList;
	}

	/* ��ȡĳ������������к��� 
    *  
    * @param roster 
    * @param groupName 
    *            ���� 
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
	 * ������з�����ϵ��
	 * 
	 * @return
	 */
	public static List<MRosterGroup> getGroups(Roster roster,Connection connection) {
		init(connection);
		if (contacters == null){
			System.out.println("���з�����ϵ��Ϊ��");
			throw new RuntimeException("contacters is null");
		}
		List<MRosterGroup> groups = new ArrayList<ContacterManager.MRosterGroup>();

		groups.add(new MRosterGroup("���к���", getContacterList(connection)));
		groups.add(new MRosterGroup("δ�������", getNoGroupUserList(roster)));

		for (RosterGroup group : roster.getGroups()) {
			List<Friends> groupUsers = new ArrayList<Friends>();
			for (RosterEntry entry : group.getEntries()) {
				groupUsers.add(contacters.get(entry.getUser()));
			}
			System.out.println("�����б�");
			for(int i=0;i<groupUsers.size();i++){
				System.out.println(groupUsers.get(i).getName());
			}
			groups.add(new MRosterGroup(group.getName(), groupUsers));
		}

		return groups;
	}

	/**
	 * ����RosterEntry����һ��User
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
		String state = "����";
		org.jivesoftware.smack.packet.Presence.Mode usMode = presence.getMode();
		if(usMode == org.jivesoftware.smack.packet.Presence.Mode.dnd){
			state = "æµ";
		}else if(usMode == org.jivesoftware.smack.packet.Presence.Mode.away||
				usMode == org.jivesoftware.smack.packet.Presence.Mode.xa){
			state = "�뿪";
		}else if(presence.isAvailable()){
			state = "����";
		}else{
			state = "����";
		}
//		friends.setStatus(presence.getStatus());
		friends.setStatus(state);
		friends.setSize(entry.getGroups().size());
		friends.setAvailable(presence.isAvailable());
		friends.setType(entry.getType());
		return friends;
	}

	/**
	 * �޸�������ѵ��ǳ�
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
	 * ��һ��������ӵ�һ������
	 * 
	 * @param user
	 * @param groupName
	 */
	public static void addUserToGroup(final Friends friends, final String groupName,
			final XMPPConnection connection) {
		if (groupName == null || friends == null)
			return;
		// ��һ��rosterEntry��ӵ�group����PacketCollector���������߳�
		new Thread() {
			public void run() {
				RosterGroup group = connection.getRoster().getGroup(groupName);
				// ������Ѿ����ھ���ӵ�����飬�����ڴ���һ����
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
	 * ��һ�����Ѵ�����ɾ��
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
