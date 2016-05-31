package com.xiaoguo.wasp.mobile.model;

public class UploadItem
{
	private int m_id;
	private String m_url;
	private String m_content;
	private int m_type;
	private String m_filepath;
	
	public UploadItem(int id, String url, String content, int type, String filepath)
	{
		m_id = id;
		m_url = url;
		m_content = content;
		m_type = type;
		m_filepath = filepath;
	}

	public int getid() {
		return m_id;
	}

	public String geturl() {
		return m_url;
	}

	public String getcontent() {
		return m_content;
	}

	public int gettype() {
		return m_type;
	}

	public String getfilepath() {
		return m_filepath;
	}
}