package com.xiaoguo.wasp.mobile.ui.chat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.utils.Byte2KB;
import com.xiaoguo.wasp.mobile.utils.ComPressUtils;
import com.xiaoguo.wasp.mobile.utils.ExpressionUtil;
import com.xiaoguo.wasp.mobile.utils.GridviewUtils;
import com.xiaoguo.wasp.mobile.widget.AudioEncoder;
import com.xiaoguo.wasp.mobile.widget.MakeAudio;
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;
import com.xiaoguo.wasp.mobile.xmpphelper.AChatActivity;


public class ChatLayoutActivity extends AChatActivity implements OnClickListener {

	private PullDownView mPullDownView;
	ListView listView = null;
	List<Map<String, Object>> list;
	IMMessage message;
	String style = null;
	String name = null;
	Friends friends = null;

	private TextView titleView;// ����
	private Button messageView;// ������Ϣ����
	private Button imgView;// ������Ϣ

	private ImageView bqView;// ����
	private ImageView addView;// ���ͷ�ʽ
	private EditText chatInputView;// ��������
	private Button speakView;// ��ס˵��
	private Button chatSendView;// ���������̰�ť

	private MessageListAdapter adapter = null;

	private int[] imageIds = new int[105];// ����ID
	private Dialog builder; // ���鵯����

	private int input_type = 0;// ���뷽ʽ INPUT_TYPE:0---�������룬1---��������
	/* ������ */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int FILE_RESULT_CODE = 2;

	ByteArrayOutputStream stream = null;
	String filename = null;
	int i1 = 0;// δ��������0������������1��
	String record_call = null;

	private static int MAX_TIME = 60; // �¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static int MIX_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ�����ƣ�������Ϊ1

	private static int RECORD_NO = 0; // ����¼��
	private static int RECORD_ING = 1; // ����¼��
	private static int RECODE_ED = 2; // ���¼��

	private static int RECODE_STATE = 0; // ¼����״̬

	private static float recodeTime = 0.0f; // ¼����ʱ��
	private static double voiceValue = 0.0; // ��˷��ȡ������ֵ
	MakeAudio mr;// ¼����Ƶ�����ļ�
	private Dialog dialog;
	private Thread recordThread;
	private ImageView dialog_img;
	AudioEncoder encoder = new AudioEncoder();// �������
	boolean isPlay = false;
	MediaPlayer mediaPlayer;
	int i = 1;
	int j = 0;// 0--��ʾ���ͣ�1--��ʾ����
	ImageView voice_volumn = null;

	// Ⱥ��
	String crowd_jid = null;
	String crowd_name = null;

	private ProgressBar pb = null;
	private IMMessageDb messageDb = null;
	private UserSettingInfo userInfo = null;
	List<IMMessage> msgList = null;
	TextView receiveState = null;
	
	String dispalyName=null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {    
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();   
			StrictMode.setThreadPolicy(policy); 
		}

		WASPApplication.getInstance().addActivity(this);
		
		messageDb = new IMMessageDb(ChatLayoutActivity.this);
		userInfo = new UserSettingInfo(ChatLayoutActivity.this);

		System.out.println("userInfo=" + userInfo.getAccount());

		Bundle b = getIntent().getBundleExtra("info");
		style = b.getString("style");
		System.out.println("style=" + style);

		if (style.equals("crowd")) {
			crowd_jid = b.getString("jid");
			crowd_name = b.getString("name");
			System.out.println("crowd_jid=" + crowd_jid);
			System.out.println("crowd_name=" + crowd_name);
		} else {
			friends = b.getParcelable("friends");
			name = friends.getJID();
			dispalyName = friends.getName();
			System.out.println("dispalyName="+dispalyName);
			System.out.println("name=" + name);
		}
		this.pb = (ProgressBar) findViewById(R.id.formclient_pb_1);
		
		setupView();
	}

	private void setupView() {
		mPullDownView = (PullDownView)findViewById(R.id.chatlist);
		mPullDownView.setOnPullDownListener(new OnPullDownListener() {
			@Override
			public void onRefresh() {
				msgList = getMessages("refresh");
				mPullDownView.RefreshComplete();
				adapter.notifyDataSetChanged();
			}
			@Override
			public void onMore() {
				
			}
		});
		listView = mPullDownView.getListView();
		listView.setDividerHeight(0);
		listView.setDivider(getResources().getDrawable(R.drawable.white));
		listView.setVerticalScrollBarEnabled(false);

		titleView = (TextView) this.findViewById(R.id.title);
		String titleName = "";
		if (style.equals("crowd")) {
			titleName = crowd_name;
		} else {
			if(dispalyName!=null && !dispalyName.equals("")){
				titleName = dispalyName;
			}else{
				titleName = name;
			}
		}
		titleView.setText(titleName);
		
		msgList = getMessages("normol");
		adapter = new MessageListAdapter(ChatLayoutActivity.this, msgList, listView);
		listView.setAdapter(adapter);
		listView.setSelection(listView.getBottom());
		
		// ���ÿ����Զ���ȡ���� �������һ���Զ���ȡ �ĳ�false�������Զ���ȡ����
		mPullDownView.enableAutoFetchMore(true, 1);
		// ���� ������β��
		mPullDownView.setHideFooter();
		// ��ʾ�������Զ���ȡ����
		// mPullDownView.setShowFooter();
		// ���ز��ҽ���ͷ��ˢ��
		mPullDownView.setHideHeader();
		// ��ʾ���ҿ���ʹ��ͷ��ˢ��
		mPullDownView.setShowHeader();

		messageView = (Button) findViewById(R.id.bt_left);
		messageView.setVisibility(View.VISIBLE);
		messageView.setOnClickListener(this);

		imgView = (Button) findViewById(R.id.bt_right);
		imgView.setVisibility(View.INVISIBLE);
		imgView.setOnClickListener(this);

		bqView = (ImageView) findViewById(R.id.chat_bq);
		bqView.setOnClickListener(this);

		addView = (ImageView) findViewById(R.id.chat_add);
		addView.setOnClickListener(this);

		chatInputView = (EditText) findViewById(R.id.chat_input);

		speakView = (Button) findViewById(R.id.chat_speak);

		chatSendView = (Button) findViewById(R.id.chat_send);
		// ÿ�ν�������Ĭ����������
		input_type = 0;
		chatSendView.setText("����");
		chatSendView.setOnClickListener(this);

		// ����˵��������������
		chatInputView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String content = chatInputView.getText().toString();
				if (content.length() > 0) {
					input_type = 2;
					chatSendView.setText("����");
				} else {
					input_type = 0;
					chatSendView.setText("����");
				}
			}
		});

		FileTransferManager fileTransferManager = new FileTransferManager(
				ConnectionUtils.getConnection(ChatLayoutActivity.this));
		fileTransferManager
				.addFileTransferListener(new RecFileTransferListener());
	}

	@Override
	protected void receiveNewMessage(IMMessage message) {

	}

	@Override
	protected void refreshMessage(List<IMMessage> messages) {
		adapter.refreshList(messages);
		System.out.println("33");
	}

	// ��Ϣ����
	private class MessageListAdapter extends BaseAdapter {
		private List<IMMessage> items;
		private Context context;
		private ListView adapterList;

		public MessageListAdapter(Context context, List<IMMessage> items,
				ListView adapterList) {
			this.context = context;
			this.items = items;
			this.adapterList = adapterList;
		}

		public void refreshList(List<IMMessage> items) {
			System.out.println("44");
			this.items = items;
			this.notifyDataSetChanged();
			adapterList.setSelection(items.size() - 1);
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final IMMessage message = items.get(position);
			System.out.println("�б���message="+message);
			message.setUnReadCount(1);
			TextView timeView = null;
			ImageView avaterView = null;
			TextView contentView = null;

			ImageView conImageView = null;

			LinearLayout fileLayout = null;
			ImageView floder_image = null;
			TextView floder_name = null;
			TextView floder_size = null;

			Button receiveBt = null;
			Button rejectBt = null;

			LinearLayout voiceLayout = null;
			TextView voice_time = null;
			// ImageView voice_volumn=null;
			View view1 = null;
			// ���յ���Ϣ
			if (style.equals("crowd")) {

			} else if (style.equals("from")) {
				if (message.getFromSubJid() != null
						&& message.getMsgType() != 1) {
					// ���յ���Ϣ
					String tempJid = message.getFromSubJid();
					tempJid = tempJid.substring(0,tempJid.indexOf("@"));
					System.out.println("tempJid="+tempJid);
					messageDb.updateMessageReadOrNot(userInfo.getAccount(), tempJid, message, 1);
					view1 = ChatLayoutActivity.this.getLayoutInflater().inflate(
							R.layout.item_message, null);// ��Ϣ
					timeView = (TextView) view1.findViewById(R.id.messageTime);// ��Ϣ����
																				// ʱ��
					avaterView = (ImageView) view1
							.findViewById(R.id.senderAvatar);// ��Ϣ������ͷ��
					// ������Ϣ
					contentView = (TextView) view1
							.findViewById(R.id.messageContent);// ��Ϣ����
					contentView
							.setBackgroundResource(R.drawable.balloon_l_selector);
					// ����ͼƬ
					conImageView = (ImageView) view1
							.findViewById(R.id.messageImg);// ͼƬ
					conImageView
							.setBackgroundResource(R.drawable.balloon_l_selector);
					// �����ļ�
					fileLayout = (LinearLayout) view1
							.findViewById(R.id.send_folder_1);// �����ļ�
					fileLayout
							.setBackgroundResource(R.drawable.balloon_l_selector);
					// ��������
					voiceLayout = (LinearLayout) view1
							.findViewById(R.id.voice_layout_1);// ��������
					voiceLayout
							.setBackgroundResource(R.drawable.balloon_l_selector);
				} else {
					// ���͵���Ϣ
					String tempJid = message.getToSubJid();
					tempJid = tempJid.substring(0,tempJid.indexOf("@"));
					messageDb.updateMessageReadOrNot(userInfo.getAccount(), tempJid, message, 1);
					
					view1 = ChatLayoutActivity.this.getLayoutInflater().inflate(
							R.layout.item_news, null);// ����
					timeView = (TextView) view1.findViewById(R.id.newsTime);// ���ŷ���
																			// ʱ��
					avaterView = (ImageView) view1.findViewById(R.id.newsIcon);// ���ŷ�����ͼ��
					// ������Ϣ
					contentView = (TextView) view1.findViewById(R.id.newsTitle);// ���ű���
					contentView
							.setBackgroundResource(R.drawable.balloon_r_selector);
					// ����ͼƬ
					conImageView = (ImageView) view1.findViewById(R.id.newsImg);// ͼƬ
					conImageView
							.setBackgroundResource(R.drawable.balloon_r_selector);
					// �����ļ�
					fileLayout = (LinearLayout) view1
							.findViewById(R.id.send_folder_2);// �����ļ�
					fileLayout
							.setBackgroundResource(R.drawable.balloon_r_selector);
					// ��������
					voiceLayout = (LinearLayout) view1
							.findViewById(R.id.voice_layout);// ��������
					voiceLayout
							.setBackgroundResource(R.drawable.balloon_r_selector);
				}
			} else {// ������Ϣ
					// ����ǽ��յ���Ϣ�����ܵ���Ϣ��ʾ�����
				if (message.getFromSubJid() != null && message.getMsgType() != 1) {
					System.out.println("������Ϣ");
					String jid = message.getFromSubJid();
					if(jid.contains("@") && jid.contains(":")){
						jid = jid.substring(0,jid.indexOf("@"));
					}
					System.out.println("jid="+jid);
					messageDb.updateMessageReadOrNot(userInfo.getAccount(), jid, message, 1);
					// �������ֺͱ���
					view1 = ChatLayoutActivity.this.getLayoutInflater().inflate(
							R.layout.item_message, null);// ��Ϣ
					timeView = (TextView) view1.findViewById(R.id.messageTime);// ��Ϣ����
																				// ʱ��
					avaterView = (ImageView) view1
							.findViewById(R.id.senderAvatar);// ��Ϣ������ͷ��
					contentView = (TextView) view1
							.findViewById(R.id.messageContent);// ��Ϣ����
					contentView
							.setBackgroundResource(R.drawable.balloon_l_selector);
					// ����ͼƬ
					conImageView = (ImageView) view1
							.findViewById(R.id.messageImg);
					conImageView
							.setBackgroundResource(R.drawable.balloon_l_selector);
					// �����ļ�
					fileLayout = (LinearLayout) view1
							.findViewById(R.id.send_folder_1);
					fileLayout
							.setBackgroundResource(R.drawable.balloon_l_selector);
					// ��������
					voiceLayout = (LinearLayout) view1
							.findViewById(R.id.voice_layout_1);
					voiceLayout
							.setBackgroundResource(R.drawable.balloon_l_selector);
				} else {// ���͵���Ϣ��ʾ���ұ�
					System.out.println("������Ϣ");
					System.out.println("������Ϣmessage="+message);
					String jid = message.getToSubJid();
					System.out.println("jid="+jid);
					jid = jid.substring(0,jid.indexOf("@"));
					System.out.println("�����"+jid);
					messageDb.updateMessageReadOrNot(userInfo.getAccount(), jid, message, 1);
					// �������ֺͱ���
					view1 = ChatLayoutActivity.this.getLayoutInflater().inflate(
							R.layout.item_news, null);// ����
					timeView = (TextView) view1.findViewById(R.id.newsTime);// ���ŷ���
																			// ʱ��
					avaterView = (ImageView) view1.findViewById(R.id.newsIcon);// ���ŷ�����ͼ��
					contentView = (TextView) view1.findViewById(R.id.newsTitle);// ���ű���
					contentView
							.setBackgroundResource(R.drawable.balloon_r_selector);
					// ����ͼƬ
					conImageView = (ImageView) view1.findViewById(R.id.newsImg);
					conImageView
							.setBackgroundResource(R.drawable.balloon_r_selector);
					// �����ļ�
					fileLayout = (LinearLayout) view1
							.findViewById(R.id.send_folder_2);
					fileLayout
							.setBackgroundResource(R.drawable.balloon_r_selector);
					// ��������
					voiceLayout = (LinearLayout) view1
							.findViewById(R.id.voice_layout);
					voiceLayout
							.setBackgroundResource(R.drawable.balloon_r_selector);
				}
			}
			timeView.setText(message.getTime());
			String tempStr = message.getContent();// ��Ϣ��������
			System.out.println("chatMode=" + message.getChatMode());
			// �㲥��Ϣʱ�������ж��ǲ��ǹ㲥��Ϣ���㲥��Ϣ
			System.out.println("hhh=" + message.getChatMode());
			int chatMode = message.getChatMode();
			System.out.println("chatMode="+chatMode);
			String str = tempStr;
			System.out.println("tempStr="+tempStr);
			if(tempStr!=null && !tempStr.equals("")){
				if (tempStr.startsWith(";")) {
					chatMode = Integer.parseInt(tempStr.substring(
							tempStr.indexOf(";") + 1, 2));
					System.out.println("chatMode="+chatMode);
					str = tempStr.substring(2);
					System.out.println("chaMode=" + chatMode);
					System.out.println("tempStr=" + tempStr);
				}
			// �������֡�����
			if ((chatMode == 0) || (chatMode == 4)) {
				contentView.setVisibility(View.VISIBLE);
				conImageView.setVisibility(View.GONE);
				String zhengze = "f0[0-9]{2}|f10[0-7]";
				// ������ʽ�������ж���Ϣ���Ƿ��б���
				try {
					System.out.println("000");
					SpannableString spannableString = ExpressionUtil
							.getExpressionString(context, str, zhengze);
					contentView.setTextSize(20);
					contentView.setText(spannableString);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			} else if (chatMode == 1) {// ����ͼƬ
				System.out.println("111");
				contentView.setVisibility(View.GONE);
				conImageView.setVisibility(View.VISIBLE);
				final byte[] b = Base64.decode(str, Base64.DEFAULT);
				if (b.length != 0) {
					final Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0,b.length);
					// ���õ���bitmap��ԭ����
					conImageView.setImageBitmap(bitmap);
					conImageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent i = new Intent();
//							i.setClass(ChatLayoutActivity.this, PictureActivity.class);
							i.setClass(ChatLayoutActivity.this, ChatPictureActivity.class);
							Bundle bd = new Bundle();
							if (message.getFromSubJid() != null
									&& message.getMsgType() != 1) {
								bd.putString("from", "from");
							} else {
								bd.putString("from", "to");
								bd.putString("url", message.getInfoUrl());
								System.out.println("infoUrl="
										+ message.getInfoUrl());
							}
							bd.putByteArray("bit", b);
							i.putExtra("pic", bd);
							startActivity(i);
						}
					});
				}
			} else if(chatMode == 5){
				//��Ӻ�������
				
			}else if (chatMode == 2) {// �ļ�����
				System.out.println("222");
				contentView.setVisibility(View.GONE);
				conImageView.setVisibility(View.GONE);
				fileLayout.setVisibility(View.VISIBLE);
				// �ļ����
				floder_image = (ImageView) fileLayout
						.findViewById(R.id.send_file_pic);
				floder_name = (TextView) fileLayout
						.findViewById(R.id.file_name);
				floder_size = (TextView) fileLayout
						.findViewById(R.id.file_size);

				receiveState = (TextView) fileLayout
						.findViewById(R.id.receive_state);
				receiveBt = (Button) fileLayout.findViewById(R.id.send_file_ok);
				rejectBt = (Button) fileLayout
						.findViewById(R.id.send_file_reject);

				final String fileName = tempStr.substring(tempStr.indexOf(";") + 1);
				String fileSize = tempStr.substring(0, tempStr.indexOf(";"));

				System.out.println("fileName=" + fileName);
				floder_name.setText(fileName.substring(fileName.lastIndexOf("/")+1));
				floder_size.setText(fileSize);
				if (fileName.endsWith(".mp3")) {
					floder_image.setImageResource(R.drawable.music);
				} else if (fileName.endsWith(".mp4")) {
					floder_image.setImageResource(R.drawable.mp4);
				} else if (fileName.endsWith(".jpg")
						|| fileName.endsWith(".jpeg")
						|| fileName.endsWith(".png")) {
						Bitmap bitmap = ComPressUtils.getSmallBitmap(fileName);
						System.out.println("�ļ���СΪ��"+fileSize);
						byte[] bytes = Byte2KB.Bitmap2Bytes(bitmap);
						Long long1 = (long) ((bytes.length +1023)/1024);
						String kbs = Byte2KB.bytes2kb(long1);
						if (message.getFromSubJid() != null
								&& message.getMsgType() != 1) {
							if(kbs == fileSize){
								floder_image.setImageBitmap(bitmap);
							}else{
								floder_image.setImageResource(R.drawable.pic_4);
							}
						}else{
							floder_image.setImageBitmap(bitmap);
						}
					//}
				} else if (fileName.endsWith(".txt")
						|| fileName.endsWith(".lrc")) {
					floder_image.setImageResource(R.drawable.txt);
				} else if (fileName.endsWith(".doc")
						|| fileName.endsWith(".docx")) {
					floder_image.setImageResource(R.drawable.word);
				} else if (fileName.endsWith(".pdf")) {
					floder_image.setImageResource(R.drawable.pdf);
				} else {
					floder_image.setImageResource(R.drawable.unknown);
				}
				if (message.getFromSubJid() != null
						&& message.getMsgType() != 1) {// �����ļ�
				}
				fileLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent("android.intent.action.VIEW"); 
						intent.addCategory("android.intent.category.DEFAULT"); 
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
						Uri uri = Uri.fromFile(new File(fileName)); 
						if (fileName.endsWith(".mp3")
								||fileName.endsWith(".amr")) {
							intent.setDataAndType(uri, "audio/*"); 
						}else if (fileName.endsWith(".mp4")) {
							intent.setDataAndType(uri, "video/*"); 
						}else if (fileName.endsWith(".jpg")
								|| fileName.endsWith(".jpeg")
								|| fileName.endsWith(".png")) {
							intent.setDataAndType(uri, "image/*"); 
						} else if (fileName.endsWith(".txt")
								|| fileName.endsWith(".lrc")){
							intent.setDataAndType(uri, "text/plain"); 
						}else if(fileName.endsWith(".doc")
								|| fileName.endsWith(".docx")) {
							intent.setDataAndType(uri, "application/msword"); 
						} else if (fileName.endsWith(".pdf")) {
							intent.setDataAndType(uri, "application/pdf"); 
						} else if(fileName.endsWith("xsls")){
							intent.setDataAndType(uri, "application/vnd.ms-excel"); 

						}else if(fileName.endsWith("ppt")
								|| fileName.endsWith("pptx")){
							intent.setDataAndType(uri, "application/vnd.ms-powerpoint"); 
						}else if(fileName.endsWith("html")){
							intent.setDataAndType(uri, "text/html"); 
						}else if(fileName.endsWith("apk")){
							uri = Uri.parse("file://" + fileName);
							intent.setDataAndType(uri, "application/vnd.android.package-archive");  
						}
						startActivity(intent);
					}
				});
			} else if (chatMode == 3) {// ����
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					String time = str.substring(0, str.indexOf(":"));
					time = time.substring(0,time.indexOf("."))+"'"+Integer.parseInt(time.substring(time.indexOf(".")+1,time.indexOf(".")+2))+"''";
					System.out.println("¼��ʱ�䣺"+time);
					str = str.substring(str.indexOf(":")+1);
					System.out.println("333");
					contentView.setVisibility(View.GONE);
					conImageView.setVisibility(View.GONE);
					fileLayout.setVisibility(View.GONE);
					voiceLayout.setVisibility(View.VISIBLE);
					voice_time = (TextView) voiceLayout
							.findViewById(R.id.voice_time);
	//				voice_time.setText("5'");
					voice_time.setText(time);
					voice_volumn = (ImageView) voiceLayout
							.findViewById(R.id.voice_volumn);// ����ʱ��Ҫ����
					final String url = message.getInfoUrl();
					System.out.println("url=" + url);
					boolean isSave = false;
					String saveFileStr = null;
					if (message.getFromSubJid() != null
							&& message.getMsgType() != 1) {
						j = 1;// ����
						if (!isSave) {
							System.out.println("���յ���Ϣ");
							String sdStatus = Environment.getExternalStorageState();
							if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
								File f = new File(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/WASP/receive/audio"
										+ "/"
										+ message.getFromSubJid());
								if (!f.exists()) {// �����ڴ���
									f.mkdirs();
								}
								SimpleDateFormat sim = new SimpleDateFormat(
										"yyyy-MM-dd_HH-mm-ss_SS");
								String film = sim.format(new Timestamp(System
										.currentTimeMillis()));
								saveFileStr = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/WASP/receive/audio"
										+ "/"
										+ message.getFromSubJid()
										+ "/"
										+ film
										+ ".amr";
								System.out.println("¼���ļ����յ�ַ=" + saveFileStr);
								String byteStr = str;
								byte[] bytes = Base64.decode(byteStr,
										Base64.DEFAULT);
								System.out.println("bytes ����=" + bytes.length);
								FileOutputStream b1 = null;
								try {
									b1 = new FileOutputStream(saveFileStr);
									try {
										b1.write(bytes);
										b1.close();
									} catch (IOException e) {
										e.printStackTrace();
										System.out.println("IO�쳣");
									}
								} catch (FileNotFoundException e) {
									System.out.println("û�ҵ��ļ�");
									e.printStackTrace();
								}
							}
							final File saveFile = new File(saveFileStr);
							if (saveFile.exists()) {
								isSave = true;
							}
							voiceLayout.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
	
									isPlay = false;
									if (!isPlay) {
										if (mediaPlayer != null
												&& mediaPlayer.isPlaying()) {
											mediaPlayer.stop();
										}
										mediaPlayer = new MediaPlayer();
										try {
											System.out.println("path="
													+ saveFile.getPath());
											mediaPlayer.setDataSource(saveFile
													.getPath());
											mediaPlayer.prepare();
											mediaPlayer.start();
											isPlay = true;
											mediaPlayer
													.setOnCompletionListener(new OnCompletionListener() {
														@Override
														public void onCompletion(
																MediaPlayer mp) {
															if (isPlay) {
																isPlay = false;
															}
	
														}
													});
	
										} catch (IllegalArgumentException e) {
											e.printStackTrace();
										} catch (SecurityException e) {
											e.printStackTrace();
										} catch (IllegalStateException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
									} else {
										if (mediaPlayer.isPlaying()) {
											mediaPlayer.stop();
											isPlay = false;
										} else {
											isPlay = false;
										}
									}
								}
							});
							mythread1();
						} else {
							Toast.makeText(ChatLayoutActivity.this, "û��SD��",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						j = 0;// ����
						voiceLayout.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
	
								isPlay = false;
								if (!isPlay) {
									if (mediaPlayer != null
											&& mediaPlayer.isPlaying()) {
										mediaPlayer.stop();
									}
									mediaPlayer = new MediaPlayer();
									try {
										mediaPlayer.setDataSource(url);
										mediaPlayer.prepare();
										mediaPlayer.start();
										isPlay = true;
										mediaPlayer
												.setOnCompletionListener(new OnCompletionListener() {
													@Override
													public void onCompletion(
															MediaPlayer mp) {
														if (isPlay) {
															isPlay = false;
														}
	
													}
												});
	
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (SecurityException e) {
										e.printStackTrace();
									} catch (IllegalStateException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									if (mediaPlayer.isPlaying()) {
										mediaPlayer.stop();
										isPlay = false;
									} else {
										isPlay = false;
									}
								}
							}
						});
						mythread1();
					}
				}else{
					Toast.makeText(MContext, "û��sd�������ղ���������Ϣ", Toast.LENGTH_SHORT).show();
					contentView.setVisibility(View.VISIBLE);
					contentView.setText("������Ϣ");
					conImageView.setVisibility(View.GONE);
					fileLayout.setVisibility(View.GONE);
					voiceLayout.setVisibility(View.GONE);
				}
			 }
			}
			return view1;
		}

	}

	// ����¼�
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			Intent resultIntent = new Intent();
			this.setResult(RESULT_OK,resultIntent);
			this.finish();
			break;
		case R.id.bt_right:
			break;
		case R.id.chat_bq:
			createExpressionDialog();
			break;
		case R.id.chat_add:
			createMoreDialog();
			break;
		case R.id.chat_send:
			System.out.println("type=" + input_type);
			String message = chatInputView.getText().toString();
			// �жϷ�ʽ:Ĭ��Ϊ0---��ʾ��������,�������Ϊ1---��ʾ�����̡�,����������Ϊ2---��ʾ�����͡�
			if (input_type == 0) {// ԭ����ʾ�������������һ��Ϊ�����̡�
				input_type = 1;
				chatSendView.setText("����");
				chatInputView.setVisibility(View.GONE);
				speakView.setVisibility(View.VISIBLE);
				speakView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
							Toast.makeText(ChatLayoutActivity.this,"û��SD�������ܷ���������Ϣ", Toast.LENGTH_SHORT).show();
						} else {
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								System.out.println("����");
								if (RECODE_STATE != RECORD_ING) {// ����������¼��
									mr = new MakeAudio("audio");
									RECODE_STATE = RECORD_ING;
									showVoiceDialog();
									try {
										if (mr.path.equals("û��sd��")) {
											Toast.makeText(ChatLayoutActivity.this,
													mr.path, Toast.LENGTH_SHORT)
													.show();
										} else {
											mr.start();// ��ʼ¼��
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									mythread();
								}
								break;
							case MotionEvent.ACTION_UP:
								System.out.println("����");
								if (RECODE_STATE == RECORD_ING) {
									RECODE_STATE = RECODE_ED;
									if (dialog.isShowing()) {
										dialog.dismiss();
									}
									try {
										mr.stop();
										voiceValue = 0.0;
									} catch (IOException e) {
										e.printStackTrace();
									}
									if (recodeTime < MIX_TIME) {
										showWarnToast();// ��ʾ¼��ʱ��̫�̶Ի���
										RECODE_STATE = RECORD_NO;
									} else {
										// ���ر������
											String messageStr = ComPressUtils
													.getAudoiToStr(mr.path);
											System.out.println("path=" + mr.path);
											sendMessage(";3"+recodeTime+":" + messageStr, mr.path,
													3);
//										}
									}
								}
								break;
							default:
								break;
							}
						}
						return false;
					}
				});
			} else if (input_type == 1) {
				input_type = 0;
				chatInputView.setVisibility(View.VISIBLE);
				speakView.setVisibility(View.GONE);
				chatSendView.setText("����");
			} else {
				sendMessage(";0" + message, "", 0);
				chatInputView.setText("");
			}
			break;
		default:
			break;
		}

	}

	/*
	 * 
	 * ��������Ի���
	 */
	private void createMoreDialog() {
		builder = new Dialog(ChatLayoutActivity.this);
		GridView gridView = GridviewUtils.getMoreGridView(ChatLayoutActivity.this);
		builder.setContentView(gridView);
		builder.setTitle("�������");
		builder.show();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// ��ת��ͼƬѡ�����
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // �����ļ�����
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery,
							IMAGE_REQUEST_CODE);
					builder.dismiss();
					break;
				case 1:
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 1);
					builder.dismiss();
					break;
				case 2:
					// �ļ�����
					Intent i4 = new Intent();
					i4.setClass(ChatLayoutActivity.this, MyFileManager.class);
					startActivityForResult(i4, FILE_RESULT_CODE);
					builder.dismiss();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String picPath = null;
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				Uri uri = data.getData();
				String scheme = uri.getScheme();
				if (scheme.equalsIgnoreCase("file")) {
					picPath = uri.getPath();
					System.out.println("picPath=" + picPath);
					file = new File(picPath);
					String messageStr = ComPressUtils.bitmapToString(picPath);
					sendMessage(";1" + messageStr, picPath, 1);
				} else if (scheme.equalsIgnoreCase("content")) {
					Cursor cursor = getContentResolver().query(uri, null, null,
							null, null);
					cursor.moveToFirst();
					picPath = cursor.getString(1);
					file = new File(picPath);
					String messageStr = ComPressUtils.bitmapToString(picPath);
					sendMessage(";1" + messageStr, picPath, 1);
				}
				break;
			case CAMERA_REQUEST_CODE:
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
					return;
				}
				@SuppressWarnings("static-access")
				String name = new DateFormat().format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".jpg";
				Bundle bundle1 = data.getExtras();
				if(bundle1!=null){
					Bitmap bitmap2 = (Bitmap) bundle1.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ
					FileOutputStream b1 = null;
					File file1 = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/WASP/Image");
					file1.mkdirs();// �����ļ���
					String fileName = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/WASP/Image/" + name;
					try {
						b1 = new FileOutputStream(fileName);
						if(bitmap2!=null){
							bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, b1);// ������д���ļ�
							File files = new File(fileName);
							String messageStr = ComPressUtils.bitmapToString2(fileName);
							sendMessage(";1" + messageStr, fileName, 1);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							b1.flush();
							b1.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case FILE_RESULT_CODE:
				Bundle bd = null;
				if (data != null && (bd = data.getExtras()) != null) {
					bd = data.getExtras();
					String filePath = bd.getString("file");
					System.out.println("filePath=" + filePath);
					File file2 = new File(filePath);
					// �ļ�
					System.out.println("fileSize=" + file2.length());
					sendFile(filePath, friends.getJID());
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ����Ի���
	private void createExpressionDialog() {
		builder = new Dialog(ChatLayoutActivity.this);
		GridView gridView = GridviewUtils.createGridView(ChatLayoutActivity.this,
				imageIds);
		builder.setContentView(gridView);
		builder.setTitle("Ĭ�ϱ���");
		builder.show();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						imageIds[arg2 % imageIds.length]);
				ImageSpan imageSpan = new ImageSpan(ChatLayoutActivity.this, bitmap);
				String str = null;
				if (arg2 < 10) {
					str = "f00" + arg2;
				} else if (arg2 < 100) {
					str = "f0" + arg2;
				} else {
					str = "f" + arg2;
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, 4,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				System.out.println("���ͱ��飺"+spannableString);
				chatInputView.append(spannableString);
				builder.dismiss();
			}
		});

	}

	// ¼��ʱ��ʾDialog
	void showVoiceDialog() {
		dialog = new Dialog(ChatLayoutActivity.this, R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog_img = (ImageView) dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}

	// ¼����ʱ�߳�
	void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// ¼���߳�
	private Runnable ImgThread = new Runnable() {
		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {// ¼��ʱ�����ʱ
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue = mr.getAmplitude();// �������
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					// ¼������15���Զ�ֹͣ
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						try {
							mr.stop();// ֹͣ¼��
							voiceValue = 0.0;
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (recodeTime < 1.0) {
							showWarnToast();// ��ʵ¼��ʱ��̫�̶Ի���
							RECODE_STATE = RECORD_NO;
						} else {
							encoder.startEncoder(mr.path);// ��ʼ����
						}
					}
					break;
				case 1:// ��ʾ���ͼƬ
					setDialogImage();
					break;
				default:
					break;
				}

			}
		};
	};

	// ¼��DialogͼƬ��������С�л�
	void setDialogImage() {
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}

	// ¼��ʱ��̫��ʱToast��ʾ
	void showWarnToast() {
		Toast toast = new Toast(ChatLayoutActivity.this);
		LinearLayout linearLayout = new LinearLayout(ChatLayoutActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// ����һ��ImageView
		ImageView imageView = new ImageView(ChatLayoutActivity.this);
		imageView.setImageResource(R.drawable.voice_to_short); // ͼ��

		TextView mTv = new TextView(ChatLayoutActivity.this);
		mTv.setText("ʱ��̫��   ¼��ʧ��");
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// ������ɫ

		// ��ImageView��ToastView�ϲ���Layout��
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// ���ݾ���
		linearLayout.setBackgroundResource(R.drawable.record_bg);// �����Զ���toast�ı���

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// ���λ��Ϊ�м� 100Ϊ������100dp
		toast.show();
	}

	FileTransferManager fileTransferManager = null;

	// �l���ļ�
	protected void sendFile(String filepath, final String jid) {
		fileTransferManager = new FileTransferManager(
				ConnectionUtils.getConnection(ChatLayoutActivity.this));
		final OutgoingFileTransfer fileTransfer = fileTransferManager
				.createOutgoingFileTransfer(jid + "/Smack");
		System.out.println("�����ļ���·��"+filepath);
		final File file = new File(filepath);
		try {
			fileTransfer.sendFile(file, "Sending");
		} catch (Exception e) {
			e.printStackTrace();
		}
		File file1 = new File(filepath);
		String kbs = Byte2KB.bytes2kb(file1.length());
		System.out.println("kbs=" + kbs);
		String messageContent = kbs + ";"
				+ filepath;
		String time = ConnectionUtils.getStringTime();
		
		final IMMessage message = new IMMessage();
		message.setChatMode(2);// �l���ļ�
		message.setContent(messageContent);
		message.setMsgType(1);
		message.setTime(time);
		message.setToSubJid(jid);
		message.setInfoUrl(filepath);
		System.out.println("message=" + message.toString());
		System.out.println("jid=" + jid);
		System.out.println("userAccount=" + userInfo.getAccount());
		messageDb.saveMessage(message, jid, userInfo.getAccount());
		msgList.add(message);
		adapter.notifyDataSetChanged();

	}

	private FileTransferRequest request;
	private File file;

	class RecFileTransferListener implements FileTransferListener {
		@Override
		public void fileTransferRequest(FileTransferRequest prequest) {
			NotificationManager mgr = (NotificationManager) ChatLayoutActivity.this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification nt = new Notification();
			nt.defaults = Notification.DEFAULT_SOUND;
			int soundId = new Random(System.currentTimeMillis())
					.nextInt(Integer.MAX_VALUE);
			mgr.notify(soundId, nt);

			System.out.println("aaa");
			System.out.println("The file received from: "
					+ prequest.getRequestor());
			File fileDir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/WASP/receive");
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			System.out.println("bbb");
			System.out.println("filename=" + prequest.getFileName());
			file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()+ "/WASP/receive/"+ prequest.getFileName());
			request = prequest;
			
			System.out.println("ccc");
			// �O �����ļ��l���^��
			String time = ConnectionUtils.getStringTime();
			final IMMessage message = new IMMessage();
			message.setChatMode(2);// �l���ļ�
			System.out.println("�����ļ�·����"+Environment.getExternalStorageDirectory()
					.getAbsolutePath()+ "/WASP/receive/"+ prequest.getFileName());
			message.setContent(Byte2KB.bytes2kb(prequest.getFileSize()) + ";"
					+ Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/WASP/receive/"+ prequest.getFileName());
			message.setMsgType(0);// �l��߀�ǽ���,0����
			message.setTime(time);
			message.setFromSubJid(friends.getJID());
			System.out.println("message=" + message.toString());
			System.out.println("userAccount=" + userInfo.getAccount());
			System.out.println(friends.getJID().substring(0,friends.getJID().indexOf("@")));
			messageDb.saveMessage(message, friends.getJID().substring(0,friends.getJID().indexOf("@")),
					userInfo.getAccount());
			System.out.println("111");
			msgList.add(message);
			System.out.println("222");
			refreshMessage(msgList);
			
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				String[] args = (String[]) msg.obj;
				break;
			case 2:
				// if(pb.getVisibility()==View.GONE){
				System.out.println("1");
				pb.setMax(100);
				pb.setProgress(0);
				pb.setVisibility(View.VISIBLE);
				// }
				break;
			case 3:
				pb.setVisibility(View.VISIBLE);
				pb.setProgress(msg.arg1);
				break;
			case 4:
				pb.setVisibility(View.GONE);
				// receiveState.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				break;
			case 5:
				final IncomingFileTransfer infiletransfer = request.accept();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ChatLayoutActivity.this);
				builder.setTitle("�Ƿ���ܶԷ��������ļ�")
						.setCancelable(false)
						.setPositiveButton("����",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										try {
											infiletransfer.recieveFile(file);
										} catch (XMPPException e) {
											e.printStackTrace();
										}
										handler.sendEmptyMessage(2);
										Timer timer = new Timer();
										TimerTask updateProgessBar = new TimerTask() {
											public void run() {
												if ((infiletransfer
														.getAmountWritten() >= request
														.getFileSize())
														|| (infiletransfer
																.getStatus() == FileTransfer.Status.error)
														|| (infiletransfer
																.getStatus() == FileTransfer.Status.refused)
														|| (infiletransfer
																.getStatus() == FileTransfer.Status.cancelled)
														|| (infiletransfer
																.getStatus() == FileTransfer.Status.complete)) {
													cancel();
													handler.sendEmptyMessage(4);
												} else {
													long p = infiletransfer
															.getAmountWritten()
															* 100L
															/ infiletransfer
																	.getFileSize();
													android.os.Message message = handler
															.obtainMessage();
													message.arg1 = Math
															.round((float) p);
													message.what = 3;
													message.sendToTarget();
												}
											}
										};
										timer.scheduleAtFixedRate(
												updateProgessBar, 10L, 10L);
										dialog.dismiss();
									}
								})
						.setNegativeButton("�ܾ�",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										request.reject();
										dialog.cancel();
									}
								}).show();
				break;
			default:
				break;
			}
		};
	};

	// ¼����ʱ�߳�
	void mythread1() {
		recordThread = new Thread(ImgThread1);
		recordThread.start();
	}

	// ¼���߳�
	private Runnable ImgThread1 = new Runnable() {
		@Override
		public void run() {
			while (isPlay) {
				try {
					Thread.sleep(200);
					if (i < 3) {
						i = i + 1;
					} else {
						i = 1;
					}
					imgHandle1.sendEmptyMessage(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Handler imgHandle1 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if (j == 0) {// ����
						voice_volumn
								.setImageResource(R.drawable.velumn_right_1);
					} else {
						voice_volumn.setImageResource(R.drawable.volumn_left_1);
					}
					break;
				case 2:
					if (j == 0) {
						voice_volumn
								.setImageResource(R.drawable.velumn_right_2);
					} else {
						voice_volumn.setImageResource(R.drawable.velumn_left_2);
					}
					break;
				case 3:
					if (j == 0) {
						voice_volumn
								.setImageResource(R.drawable.velumn_right_3);
					} else {
						voice_volumn.setImageResource(R.drawable.velumn_left_3);
					}
					break;
				default:
					break;
				}

			}
		};
	};
	
}
