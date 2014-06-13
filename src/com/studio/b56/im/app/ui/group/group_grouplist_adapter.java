package com.studio.b56.im.app.ui.group;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.studio.b56.im.R;
import com.studio.b56.im.vo.GroupVo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class group_grouplist_adapter extends BaseAdapter {
	private List<GroupVo> lastContacts=null;
	private LayoutInflater inflater=null;
	private Context context;
	private ViewHolder holder=null;
	
	
	public group_grouplist_adapter(Activity a,List<GroupVo> contacts){
		
		Log.v("grouplist====", "=="+contacts.toArray().toString());
		
		this.context=a;
		inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(contacts==null){
			lastContacts=new LinkedList<GroupVo>();
		}else{
			lastContacts=contacts;
		}
	}

	@Override
	public int getCount() {
		return lastContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return lastContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		
			convertView=inflater.inflate(R.layout.group_roomlist_item, null);
			holder=new ViewHolder();
			holder.avatar_iv=(ImageView) convertView.findViewById(R.id.group_grouplist_avatar_iv);
			holder.nickname_tv=(TextView)convertView.findViewById(R.id.group_grouplist_nickname_tv);
			holder.update_time_tv=(TextView)convertView.findViewById(R.id.group_grouplist_update_time_tv);
			holder.last_msg_tv=(TextView)convertView.findViewById(R.id.group_grouplist_msg_tv);

			convertView.setTag(lastContacts.get(position));
			
			holder.nickname_tv.setText(lastContacts.get(position).getGroupname());
			holder.last_msg_tv.setText(lastContacts.get(position).getGroupdes());
			holder.nickname_tv.setTag(lastContacts.get(position));

			
	
			
		return convertView;
	}
	private  class ViewHolder{
		ImageView avatar_iv; //ͷ��
		TextView nickname_tv;//�ǳ�
		TextView update_time_tv;//������ʱ��
		TextView last_msg_tv;//�����Ϣ
	}

}
