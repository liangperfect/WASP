package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaoguo.wasp.mobile.model.IMMessage;



public class MChatManager {
	/**
	 * �������ҵ������¼���˳������
	 */
	public static List<IMMessage> message_pool = new ArrayList<IMMessage>();

	/**
	 * ����subscriber��threadId
	 */
	public static Map<String, String> chatThreads = new HashMap<String, String>();

	/**
	 * ��õ�ǰ���subscriber�������¼
	 * 
	 * @param userJid
	 * @return
	 */
	public static List<IMMessage> getMessages(String subJid) {
		List<IMMessage> messages = new ArrayList<IMMessage>();

		if (subJid == null)
			return messages;

		for (IMMessage message : message_pool) {
			if (subJid.equals(message.getFromSubJid()))
				messages.add(message);
		}
		return messages;
	}

}
