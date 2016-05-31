package com.xiaoguo.wasp.mobile.communication;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoguo.wasp.R;

public class ExchangeBlockActivity extends Activity implements OnClickListener {
	private LinearLayout titleLayot;
	private TextView blockClose;
	private ListView blockListView;
	// 栏目名称
	private List<String> blockNames;
	// 对应的栏目ID
	private List<String> blockIDs;
	// 屏幕的宽度
	private int screemWidth;
	private TextArrayAdapter mAdapter;
	// 之前选择的板块名字
	private int currentBlockID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_block);
		initView();
		initData();
		addListener();
	}

	private void initView() {
		titleLayot = (LinearLayout) findViewById(R.id.list_title);
		blockClose = (TextView) findViewById(R.id.block_close);
		blockListView = (ListView) findViewById(R.id.block_names);
	}

	private void initData() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		blockNames = new ArrayList<String>();
		blockNames = bundle.getStringArrayList("blockNames");
		blockIDs = new ArrayList<String>();
		blockIDs = bundle.getStringArrayList("blockIDs");
		currentBlockID = bundle.getInt("currentID", 0);
		mAdapter = new TextArrayAdapter(ExchangeBlockActivity.this, blockNames);
		blockListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

	}

	private class TextArrayAdapter extends BaseAdapter {
		private Context mContext;
		private List<String> contentList;

		public TextArrayAdapter(Context mContext, List<String> contentList) {
			this.mContext = mContext;
			this.contentList = contentList;
		}

		@Override
		public int getCount() {
			return contentList.size();
		}

		@Override
		public Object getItem(int position) {
			return contentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewhHolder viewHolder = null;
			String blockContent = contentList.get(position);

			if (convertView == null) {
				viewHolder = new ViewhHolder();
				convertView = LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_1, null);
				viewHolder.mTextView = (TextView) convertView
						.findViewById(android.R.id.text1);
				// viewHolder.mDraw = (TextView) convertView
				// .findViewById(R.id.exchange_block_draw);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewhHolder) convertView.getTag();
			}
			// if ((currentBlockID + "").equals(blockIDs.get(position))) {
			// viewHolder.mTextView.setText(blockContent);
			// viewHolder.mTextView.setTextColor(R.color.black);
			// viewHolder.mDraw.setVisibility(View.VISIBLE);
			// } else {
			// viewHolder.mTextView.setText(blockContent);
			// viewHolder.mTextView.setTextColor(R.color.black);
			// }
			viewHolder.mTextView.setText(blockContent);
			viewHolder.mTextView.setTextColor(R.color.black);
			return convertView;
		}

	}

	private class ViewhHolder {
		public TextView mTextView;
		// // 勾
		// public TextView mDraw;
	}

	private void addListener() {
		blockClose.setOnClickListener(this);
		blockListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				System.out.println("LISTView的item是否点击了的");
				Intent intent1 = new Intent();
				// 获取板块ID
				int blockId = Integer.parseInt(blockIDs.get(position));
				System.out.println("blockID-->>" + blockId);
				intent1.putExtra("blockID", blockId);
				ExchangeBlockActivity.this.setResult(1, intent1);
				System.out.println("测试setResuklt是否被 触发了");
				ExchangeBlockActivity.this.finish();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.exchange_block, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.block_close:
			ExchangeBlockActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
