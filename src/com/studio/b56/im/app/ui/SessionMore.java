package com.studio.b56.im.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.studio.b56.im.R;

import tools.AppManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SessionMore extends BaseActivity implements OnItemClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_more);
		initUI();
	}
	
	
	private void initUI() {
		List<String> addContent = new ArrayList<String>();
		addContent.add(getResources().getString(R.string.start_chat));
		addContent.add(getResources().getString(R.string.add_friends));
		addContent.add(getResources().getString(R.string.scan));
		ListView listview = (ListView) findViewById(R.id.title_list);
		listview.setAdapter(new MoreDialogAdapter(this, addContent));
		listview.setOnItemClickListener(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent intent = new Intent();
		intent.putExtra("position", position);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void initTitle() {
		
	}
	
	public class MoreDialogAdapter extends BaseAdapter{

		private Context context;
		private LayoutInflater inflater;
		private List<String> cards;
		
		public MoreDialogAdapter(Context context, List<String> cards) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
			this.cards = cards;
		}
		
		class CellHolder {
			TextView titleView;
		}
		
		@Override
		public int getCount() {
			return cards.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			CellHolder cell = null;
			if (convertView == null) {
				cell = new CellHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.session_more_cell, null);
				cell.titleView = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(cell);
			}
			else {
				cell = (CellHolder) convertView.getTag();
			}
			cell.titleView.setText(cards.get(position));
			return convertView;
		}

	}
}
