package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaoguo.wasp.mobile.model.IMMessage;



public class MChatManager {
	/**
	 * 保存左右的聊天记录，退出后清除
	 */
	public static List<IMMessage> message_pool = new ArrayList<IMMessage>();

	/**
	 * 保存subscriber的threadId
	 */
	public static Map<String, String> chatThreads = new HashMap<String, String>();

	/**
	 * 获得当前这个subscriber的聊天记录
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
