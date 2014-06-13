package com.studio.b56.im.app.ui.group;

import java.util.ArrayList;

import com.studio.b56.im.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class group_groupuserlist_adapter extends BaseAdapter {
	private ArrayList<groupuseritem> lastContacts=null;
	private LayoutInflater inflater=null;
	private Context context;
	private ViewHolder holder=null;
	
	
	public group_groupuserlist_adapter(Activity a,ArrayList<groupuseritem> contacts){
		this.context=a;
		inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(contacts==null){
			lastContacts=new ArrayList<groupuseritem>();
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
	
		
			convertView=inflater.inflate(R.layout.contact_item, null);
			holder=new ViewHolder();
			holder.contactitem_catalog=(TextView) convertView.findViewById(R.id.contactitem_catalog);
			holder.contactitem_avatar_iv=(ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
			holder.contactitem_nick=(TextView)convertView.findViewById(R.id.contactitem_nick);
			holder.contactitem_icon=(ImageView)convertView.findViewById(R.id.contactitem_icon);
			holder.contactitem_account=(TextView)convertView.findViewById(R.id.contactitem_account);
			holder.contactitem_signature=(TextView)convertView.findViewById(R.id.contactitem_signature);
			holder.contactitem_select_cb=(CheckBox)convertView.findViewById(R.id.contactitem_select_cb);

			convertView.setTag(lastContacts.get(position));
			holder.contactitem_nick.setText(lastContacts.get(position).username);
			holder.contactitem_account.setText(lastContacts.get(position).userjid);
			if(lastContacts.get(position).role==1)
			{
			  holder.contactitem_icon.setVisibility(View.VISIBLE);
			  holder.contactitem_signature.setText("群主");
			}
			else
			{
				holder.contactitem_signature.setText("成员");
			}
			
		return convertView;
	}
	private  class ViewHolder{
		public TextView contactitem_catalog;//����
		public TextView contactitem_nick;//�ǳ�
		public ImageView contactitem_avatar_iv;//ͷ��
		public ImageView contactitem_icon;//
		public TextView contactitem_account;//
		public TextView contactitem_signature;//ǩ��
		public CheckBox contactitem_select_cb;//ѡ��ť
	}

}
