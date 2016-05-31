package com.xiaoguo.wasp.mobile.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.xiaoguo.wasp.mobile.model.IMMessage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * 聊天数据库
 * @author eva
 * */
public class IMMessageDb {
	private static final String db_name = "message_db";
	private SQLiteDatabase db;
	Context context;
	
	//有参构造函数
	public IMMessageDb(Context context){
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}
	
	/*
	 * 保存消息
	 * */
	public String saveMessage(IMMessage message, String friendsJid, String userJid){
		String results=null;
		try{
			db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
					"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
					"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
			if(message != null){
				db.execSQL("insert into _"
					+ userJid
					+ " (friendJid,content, time,title,fromSubjid,toSubjid,fromSubName," +
					"toSubName,infoUrl,unReadCount,msgType,type,acceptType,chatMode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[]{friendsJid,message.getContent(),message.getTime(),message.getTitle(),message.getFromSubJid(),message.getToSubJid(),
							message.getFromSubName(),message.getToSubName(),message.getInfoUrl(),message.getUnReadCount(),
							message.getMsgType(),message.getType(),message.getAcceptType(),message.getChatMode()});
			}
			results = "success";
		/*	Cursor c1 = db.rawQuery("SELECT * FROM _"+userJid,null);
			while(c1.moveToNext()){
				String xx = c1.getInt(c1.getColumnIndex("_id"))
						+","+c1.getString(c1.getColumnIndex("friendJid"))
						+","+c1.getString(c1.getColumnIndex("content"))
						+","+c1.getString(c1.getColumnIndex("time"))
						+","+c1.getString(c1.getColumnIndex("fromSubjid"))
						+","+c1.getString(c1.getColumnIndex("toSubjid"))
						+","+c1.getString(c1.getColumnIndex("fromSubName"))
						+","+c1.getString(c1.getColumnIndex("toSubName"))
						+","+c1.getString(c1.getColumnIndex("infoUrl"))
						+","+c1.getInt(c1.getColumnIndex("unReadCount"))
						+","+c1.getInt(c1.getColumnIndex("msgType"))
						+","+c1.getInt(c1.getColumnIndex("type"))
						+","+c1.getInt(c1.getColumnIndex("acceptType"))
						+","+c1.getInt(c1.getColumnIndex("chatMode"));
				System.out.println("数据库的消息：");
				System.out.println(xx);
			}*/
		}catch(Exception e){
			e.printStackTrace();
			results = "failed";
		}
		System.out.println("保存消息到数据库："+results);
		return results;
	}
	//获取消息记录
public List<IMMessage> getAllMessage(String userJid,String friendsJid,String time) {
	IMMessage message=null;
	List<IMMessage> list = new ArrayList<IMMessage>();
	db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
			"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
			"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
	/*Cursor c1 = db.rawQuery("SELECT * from _" + userJid, null);
	while(c1.moveToNext()){
		String xx = c1.getInt(c1.getColumnIndex("_id"))
				+","+c1.getString(c1.getColumnIndex("friendJid"))
				+","+c1.getString(c1.getColumnIndex("content"))
				+","+c1.getString(c1.getColumnIndex("time"))
				+","+c1.getString(c1.getColumnIndex("fromSubjid"))
				+","+c1.getString(c1.getColumnIndex("toSubjid"))
				+","+c1.getString(c1.getColumnIndex("fromSubName"))
				+","+c1.getString(c1.getColumnIndex("toSubName"))
				+","+c1.getString(c1.getColumnIndex("infoUrl"))
				+","+c1.getInt(c1.getColumnIndex("unReadCount"))
				+","+c1.getInt(c1.getColumnIndex("msgType"))
				+","+c1.getInt(c1.getColumnIndex("type"))
				+","+c1.getInt(c1.getColumnIndex("acceptType"))
				+","+c1.getInt(c1.getColumnIndex("chatMode"));
		System.out.println("数据库的消息：");
		System.out.println(xx);
	}*/
	Cursor c=null;
	c = db.rawQuery("SELECT * from _" + userJid +" WHERE friendJid='"+friendsJid+"'",null);
	System.out.println("friendsJid="+friendsJid);
	System.out.println("消息条数为:"+c.getCount());
	while (c.moveToNext()) {
		message = new IMMessage(c.getString(c.getColumnIndex("content")),
				c.getString(c.getColumnIndex("time")),
				c.getString(c.getColumnIndex("title")), 
				c.getString(c.getColumnIndex("fromSubjid")),
				c.getString(c.getColumnIndex("toSubjid")), 
				c.getString(c.getColumnIndex("fromSubName")),
				c.getString(c.getColumnIndex("toSubName")), 
				c.getString(c.getColumnIndex("infoUrl")), 
				c.getInt(c.getColumnIndex("unReadCount")), 
				c.getInt(c.getColumnIndex("msgType")),
				c.getInt(c.getColumnIndex("type")),
				c.getInt(c.getColumnIndex("acceptType")),
				c.getInt(c.getColumnIndex("chatMode")));	
				System.out.println("message="+message.toString());
				list.add(message);
		}
		c.close();
		return list;			
	}
//获取某些消息记录
/*
* 从“begin”开始查询offset条与friendsJid的聊天记录
* */
public List<IMMessage> getSomeMessage(String userJid,String friendsJid,String time,int begin,int offset) {
	IMMessage message=null;
	List<IMMessage> list = new ArrayList<IMMessage>();
	db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
			"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
			"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
	Cursor c=null;
	c = db.rawQuery("SELECT * from _" + userJid +" WHERE friendJid='"+friendsJid+"'"+" ORDER BY time DESC"+" LIMIT "+begin+","+offset,null);
//	c = db.rawQuery("SELECT * from _" + userJid +" WHERE friendJid='"+friendsJid+"'",null);
	System.out.println("friendsJid="+friendsJid);
	System.out.println("消息条数为:"+c.getCount());
	while (c.moveToNext()) {
		message = new IMMessage(c.getString(c.getColumnIndex("content")),
				c.getString(c.getColumnIndex("time")),
				c.getString(c.getColumnIndex("title")), 
				c.getString(c.getColumnIndex("fromSubjid")),
				c.getString(c.getColumnIndex("toSubjid")), 
				c.getString(c.getColumnIndex("fromSubName")),
				c.getString(c.getColumnIndex("toSubName")), 
				c.getString(c.getColumnIndex("infoUrl")), 
				c.getInt(c.getColumnIndex("unReadCount")), 
				c.getInt(c.getColumnIndex("msgType")),
				c.getInt(c.getColumnIndex("type")),
				c.getInt(c.getColumnIndex("acceptType")),
				c.getInt(c.getColumnIndex("chatMode")));	
				System.out.println("message="+message.toString());
				list.add(message);
		}
		c.close();
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).getTime());
		}
		Collections.reverse(list);
		System.out.println("-----------------------------------");
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).getTime());
		}
		return list;			
}
//判断消息是否已经保存
public boolean isMessageSaved(String userJid,String friendsJid, IMMessage message){
	boolean result=false;
	try{
		db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
				"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
				"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
		Cursor c1 = db.rawQuery("SELECT * FROM _"+userJid+" WHERE friendJid='"+friendsJid +"' AND time='"+message.getTime()+"'",null);
		if(c1.getCount() > 0){	
			result=true;
		}else{
			result = false;
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	return result;
}
//更新某一条消息
public String updateMessageReadOrNot(String userJid,String friendsJid, IMMessage message, int readState){
	String result = null;
	try{
		db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
				"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
				"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
		Cursor c1 = db.rawQuery("SELECT * FROM _"+userJid,null);
		/*while(c1.moveToNext()){
			String xx = c1.getInt(c1.getColumnIndex("_id"))
					+","+c1.getString(c1.getColumnIndex("friendJid"))
					+","+c1.getString(c1.getColumnIndex("content"))
					+","+c1.getString(c1.getColumnIndex("time"))
					+","+c1.getString(c1.getColumnIndex("fromSubjid"))
					+","+c1.getString(c1.getColumnIndex("toSubjid"))
					+","+c1.getString(c1.getColumnIndex("fromSubName"))
					+","+c1.getString(c1.getColumnIndex("toSubName"))
					+","+c1.getString(c1.getColumnIndex("infoUrl"))
					+","+c1.getInt(c1.getColumnIndex("unReadCount"))
					+","+c1.getInt(c1.getColumnIndex("msgType"))
					+","+c1.getInt(c1.getColumnIndex("type"))
					+","+c1.getInt(c1.getColumnIndex("acceptType"))
					+","+c1.getInt(c1.getColumnIndex("chatMode"));
			System.out.println("数据库的消息：");
			System.out.println(xx);
		}*/
		System.out.println("count22="+c1.getCount());
		if(c1.getCount() > 0){	
			System.out.println("friendJid="+friendsJid);
			db.execSQL("UPDATE _"+userJid+" SET "+"unReadCount="+readState+" WHERE friendJid='"+friendsJid +"' AND time='"+message.getTime()+"'");
			Cursor c = db.rawQuery("SELECT * FROM _"+userJid+" WHERE friendJid='"+friendsJid+"'", null);
			if(c.getCount()==0){
				result = saveMessage(message, friendsJid, userJid);
			}
			result="success";
		}else{
			result = saveMessage(message, friendsJid, userJid);
		}
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("更新消息出错了");
		result = "fialed";
	}
	
	return result;
}

//更新与某好友的所有条消息
public String updateMessageWithSB(String userJid,String friendsJid, IMMessage message, int readState){
	String result = null;
	try{
		db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
				"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
				"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
		Cursor c1 = db.rawQuery("SELECT * FROM _"+userJid,null);
		System.out.println("count22="+c1.getCount());
		if(c1.getCount() > 0){	
			System.out.println("friendJid="+friendsJid);
			db.execSQL("UPDATE _"+userJid+" SET "+"unReadCount="+readState+" WHERE friendJid='"+friendsJid+" OR toSubjid='"+friendsJid);
			Cursor c = db.rawQuery("SELECT * FROM _"+userJid+" WHERE friendJid='"+friendsJid+"'", null);
			if(c.getCount()==0){
				result = saveMessage(message, friendsJid, userJid);
			}
			result="success";
		}else{
			result = saveMessage(message, friendsJid, userJid);
		}
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("更新通知出错了");
		result = "fialed";
	}
	
	return result;
}

//更新某一条消息
public String updateMessage(String userJid,String friendsJid, IMMessage message, int readState,String content){
	String result = null;
	try{
		db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
				"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
				"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
		Cursor c1 = db.rawQuery("SELECT * FROM _"+userJid,null);
		/*while(c1.moveToNext()){
			String xx = c1.getInt(c1.getColumnIndex("_id"))
					+","+c1.getString(c1.getColumnIndex("friendJid"))
					+","+c1.getString(c1.getColumnIndex("content"))
					+","+c1.getString(c1.getColumnIndex("time"))
					+","+c1.getString(c1.getColumnIndex("fromSubjid"))
					+","+c1.getString(c1.getColumnIndex("toSubjid"))
					+","+c1.getString(c1.getColumnIndex("fromSubName"))
					+","+c1.getString(c1.getColumnIndex("toSubName"))
					+","+c1.getString(c1.getColumnIndex("infoUrl"))
					+","+c1.getInt(c1.getColumnIndex("unReadCount"))
					+","+c1.getInt(c1.getColumnIndex("msgType"))
					+","+c1.getInt(c1.getColumnIndex("type"))
					+","+c1.getInt(c1.getColumnIndex("acceptType"))
					+","+c1.getInt(c1.getColumnIndex("chatMode"));
			System.out.println("数据库的消息：");
			System.out.println(xx);
		}*/
		System.out.println("count22="+c1.getCount());
		if(c1.getCount() > 0){	
			System.out.println("friendJid="+friendsJid);
			db.execSQL("UPDATE _"+userJid+" SET "+"unReadCount="+readState+" , content='"+content+"' WHERE friendJid='"+friendsJid+"'"/* +"' AND time='"+message.getTime()+"'"*/);
			Cursor c = db.rawQuery("SELECT * FROM _"+userJid+" WHERE friendJid='"+friendsJid+"'", null);
			if(c.getCount()==0){
				result = saveMessage(message, friendsJid, userJid);
			}
			result="success";
		}else{
			result = saveMessage(message, friendsJid, userJid);
		}
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("更新通知出错了");
		result = "fialed";
	}
	
	return result;
}

//获取某一个好友消息中未读的消息数
public int getUnreadMessageNum(String userJid,String friendsJid,int readState){
	int num=0;
	db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
			"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
			"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
	System.out.println("friendjid="+friendsJid);
	System.out.println("readState="+readState);
	Cursor cursor=null;
	if(friendsJid==null || friendsJid.equals("")){
		cursor = db.rawQuery("SELECT * FROM _"+userJid+" WHERE unReadCount="+readState,null);
	}else{
		cursor = db.rawQuery("SELECT * FROM _"+userJid+" WHERE friendJid='"+friendsJid+"' AND unReadCount="+readState,null);
	}
	num = cursor.getCount();
	System.out.println("未读条数："+num);
	return num;
}

//获取最近聊天的所有好友jid
public List<String> getAllFriends(String userJid,String user) {
	List<String> list = new ArrayList<String>();
	String tempjid=null;
	String tojid=null;
	String fromjid=null;
	db.execSQL("CREATE table IF NOT EXISTS _" + userJid+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"friendJid TEXT,content TEXT,time TEXT,title TEXT,fromSubjid TEXT," +
			"toSubjid TEXT,fromSubName TEXT,toSubName TEXT," +
			"infoUrl TEXT,unReadCount INTEGER,msgType INTEGER,type INTEGER,acceptType INTEGER,chatMode INTEGER)");
	Cursor c = db.rawQuery("SELECT * from _" + userJid,null);
	System.out.println("我的总的消息条数："+c.getCount());
	System.out.println("userjid="+userJid);
	System.out.println("user="+user);
	while (c.moveToNext()) {
		String xx = c.getInt(c.getColumnIndex("_id"))
				+","+c.getString(c.getColumnIndex("friendJid"))
				+","+c.getString(c.getColumnIndex("content"))
				+","+c.getString(c.getColumnIndex("time"))
				+","+c.getString(c.getColumnIndex("fromSubjid"))
				+","+c.getString(c.getColumnIndex("toSubjid"))
				+","+c.getString(c.getColumnIndex("fromSubName"))
				+","+c.getString(c.getColumnIndex("toSubName"))
				+","+c.getString(c.getColumnIndex("infoUrl"))
				+","+c.getInt(c.getColumnIndex("unReadCount"))
				+","+c.getInt(c.getColumnIndex("msgType"))
				+","+c.getInt(c.getColumnIndex("type"))
				+","+c.getInt(c.getColumnIndex("acceptType"))
				+","+c.getInt(c.getColumnIndex("chatMode"));
		System.out.println(xx);
			tojid = c.getString(c.getColumnIndex("toSubjid"));
			fromjid = c.getString(c.getColumnIndex("fromSubjid"));
			System.out.println("fromjid="+fromjid);
			System.out.println("tojid="+tojid);
			//我发出去的
				if(tojid!=null && !tojid.equals(user)){
					int i=0;
					for(i=0;i<list.size();i++){
						tempjid = list.get(i);
						System.out.println("temp="+tempjid);
						if(tempjid.equals(tojid)){
							break;
						}
					}
					if(i>=list.size()){
						list.add(tojid);
					}
				}
			//发给我的
			if (fromjid!=null && !fromjid.equals(user)) {
				int i=0;
				for(i=0;i<list.size();i++){
					tempjid = list.get(i);
					if(tempjid.equals(fromjid)){
						break;
					}
				}
				if(i>=list.size()){
					list.add(fromjid);
				}
			}
		}
		c.close();
		return list;			
	}
}
