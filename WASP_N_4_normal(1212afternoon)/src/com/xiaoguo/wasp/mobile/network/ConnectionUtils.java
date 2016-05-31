package com.xiaoguo.wasp.mobile.network;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.content.Context;

/*
 * ��ʱͨѶģ��
 * xmpp���ӷ���
 * */
public class ConnectionUtils {
	private static XMPPConnection connection;

	// public static String host = "210.41.229.90";
	public static String tempHost = CommandBase.instance().getHost();
	public static String host = tempHost.substring(0, tempHost.indexOf(":"));
	// public static String host = "203.195.137.44";
	// public static String host = "222.18.162.131";
	// public static String host = "222.18.162.149";
	// public static String host = "222.18.162.130";//�Ƽ��ֵ���openfire ��������ַ
	// public static String host = "222.18.162.135";// �Ƴ������openfire��������ַ
	// public static String host = "222.18.162.141";//�������openfire��������ַ
	private static int port = 5222;
	private static ConnectionConfiguration connectionConfig;

	public ConnectionUtils(String host) {
		super();
		ConnectionUtils.host = host;

	}

	public static void setHost(String host) {
		ConnectionUtils.host = host;
		System.out.println("host1=" + host);
	}

	public static String getHost() {
		System.out.println("host2=" + ConnectionUtils.host);
		return ConnectionUtils.host;
	}

	// init
	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		// }
		// static {
		Connection.DEBUG_ENABLED = false;

		ProviderManager pm = ProviderManager.getInstance();
		configure(pm);

		SmackConfiguration.setKeepAliveInterval(60000 * 5); // 5 mins
		SmackConfiguration.setPacketReplyTimeout(5000); // 10 secs
		SmackConfiguration.setLocalSocks5ProxyEnabled(false);
		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);

		System.out.println("host_now=" + host);
		connectionConfig = new ConnectionConfiguration(host, port, "");
		connectionConfig.setTruststorePath("/system/etc/security/cacerts.bks");
		connectionConfig.setTruststorePassword("changeit");
		connectionConfig.setTruststoreType("bks");
		// �����Զ�����
		connectionConfig.setReconnectionAllowed(true);
		// �����½�ɹ����������״̬
		// connectionConfig.setSendPresence(true);//Ϊ�˽���������Ϣ�������ó�Ϊ���ߣ���ȡ����Ϣ֮��������Ϊ����
		connectionConfig.setSendPresence(false);
		connectionConfig.setCompressionEnabled(false);
		connectionConfig
				.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		connectionConfig.setSASLAuthenticationEnabled(false);
	}

	public static XMPPConnection getRegConnection() {
		init();
		return new XMPPConnection(connectionConfig);
	}

	public static XMPPConnection getConnection(Context context) {
		init();
		if (ConnectionUtils.connection == null) {
			ConnectionUtils.connection = new XMPPConnection(connectionConfig);
			connection.getRoster().setSubscriptionMode(
					Roster.SubscriptionMode.accept_all);
		}

		if (!ConnectionUtils.connection.isConnected()) {
			try {
				ConnectionUtils.connection.connect();
			} catch (XMPPException e) {
				// Toast.makeText(context, "���ӳ���,���Ժ����ԣ�",
				// Toast.LENGTH_SHORT).show();
				System.out.println("���ӳ���");
				e.printStackTrace();
			}
		}
		return ConnectionUtils.connection;
	}

	// ��ȡHttpClient
	public static HttpClient getHttpClient() {
		HttpClient client = new DefaultHttpClient();
		return client;
	}

	// ��ȡʱ���ַ�
	public static String getStringTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		String month = (c.get(Calendar.MONTH) + 1) + "";
		if (c.get(Calendar.MONTH) + 1 < 10) {
			month = "0" + month;
		}
		String day = c.get(Calendar.DAY_OF_MONTH) + "";
		if (c.get(Calendar.DAY_OF_MONTH) < 10) {
			day = "0" + day;
		}
		String hour = c.get(Calendar.HOUR_OF_DAY) + "";
		if (c.get(Calendar.HOUR_OF_DAY) < 10) {
			hour = "0" + hour;
		}
		String minute = c.get(Calendar.MINUTE) + "";
		if (c.get(Calendar.MINUTE) < 10) {
			minute = "0" + minute;
		}
		String second = c.get(Calendar.SECOND) + "";
		if (c.get(Calendar.SECOND) < 10) {
			second = "0" + second;
		}
		return c.get(Calendar.YEAR) + "-" + month + "-" + day + " " + hour
				+ ":" + minute + ":" + second;
	}

	// ����������
	public static MultiUserChat joinMultiChat(String n, String account) {
		init();
		MultiUserChat multiUserChat = new MultiUserChat(
				ConnectionUtils.connection, n);
		if (!multiUserChat.isJoined()) {
			try {
				// ���������ҵ�����
				multiUserChat.join(account);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}

		return multiUserChat;
	}

	public static void configure(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
		}

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
				new OpenIQProvider());
		pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
				new CloseIQProvider());
		pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
				new DataPacketProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

	}
}
