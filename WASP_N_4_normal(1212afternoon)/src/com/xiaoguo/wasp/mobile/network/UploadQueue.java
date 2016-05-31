package com.xiaoguo.wasp.mobile.network;
import java.util.List;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.xiaoguo.wasp.mobile.database.ApplicationDb;
import com.xiaoguo.wasp.mobile.model.UploadItem;

public class UploadQueue {
	
	private AsyncHttpClient m_httpClient = new AsyncHttpClient();
	private boolean m_networkAvaliable = false;
	private ApplicationDb m_db = null;
	
	
    UploadQueue(Context context) {
		m_httpClient.addHeader("Content-Type", "application/json");
		m_httpClient.addHeader("Request-Client", "mobile/1.0.0");
    	
		m_db = new ApplicationDb(context);
		
	}

	public boolean isEmpty() 
	{
		return m_db.isEmpty();
	}
		
	public void startUpload()
	{
		while (!isEmpty() && m_networkAvaliable)
		{
			//TODO: get the latest item form sqlite db
			
			List<UploadItem> queue = m_db.getUploadQueue();
			
			for (int i=0; i<queue.size(); i++)
			{
				RequestParams postParam = new RequestParams();
				postParam.put("req", queue.get(i).getcontent());
				m_httpClient.post(queue.get(i).geturl(), postParam, null);
			}
			
			//TODO: execute network request
			
		}
	}

	
	public void addItemToQueue(String url, String type, String content, String filepath)
	{
		m_db.addUploadItem(new UploadItem(0, url, content, (type == "text" ? 0 : 1), filepath));
	}
	
	public void addItemToQueue(UploadItem item)
	{
		m_db.addUploadItem(item);
		
		System.out.print("Upload Queue isEmpty " + m_db.isEmpty());
	}
	
	
	@SuppressWarnings("unused")
	private void deleteUploadedItem(int id)
	{
		// TODO: if the current uploading item has been uploaded successful, just delete it from sqlite db
	}
	
}
