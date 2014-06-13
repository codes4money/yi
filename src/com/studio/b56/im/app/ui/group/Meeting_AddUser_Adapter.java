package com.studio.b56.im.app.ui.group;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.studio.b56.im.R;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.vo.CustomerVo;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class Meeting_AddUser_Adapter extends BaseAdapter {
	private LayoutInflater inflater=null;
	private Context context;
	private ViewHolder holder=null;
	private View[] views=null;
	public 	List<CustomerVo> customerVos;
	public Meeting_AddUser_Adapter(Activity a,List<CustomerVo> contactPersions){
		this.context=a;
		this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		this.customerVos=contactPersions;
		views=new View[this.customerVos.size()];
		 Log.v("jϵ����===",String.valueOf(this.customerVos.size()));
	}	
	@Override
	public int getCount() {
		return this.customerVos.size();
	}

	@Override
	public Object getItem(int position) {
		return this.customerVos.get(position);
	}
	public CustomerVo getItem2(int position) {
		return this.customerVos.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	public FinalBitmap getHeadBitmap() {
		return FinalFactory.createFinalBitmap(this.context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 if (views[position] != null ) {
             return views[position];
         }
			CustomerVo customerVo =getItem2(position);
			
			convertView=inflater.inflate(R.layout.contact_item, null);
			holder=new ViewHolder();
			holder.contactitem_catalog=(TextView) convertView.findViewById(R.id.contactitem_catalog);
			holder.contactitem_avatar_iv=(ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
			holder.contactitem_nick=(TextView)convertView.findViewById(R.id.contactitem_nick);
			holder.contactitem_account=(TextView)convertView.findViewById(R.id.contactitem_account);
			holder.contactitem_signature=(TextView)convertView.findViewById(R.id.contactitem_signature);
			holder.contactitem_select_cb=(CheckBox)convertView.findViewById(R.id.contactitem_select_cb);
			convertView.setTag(customerVo);
		
			
	    FinalOnloadBitmap.finalDisplay(this.context, customerVo, holder.contactitem_avatar_iv, getHeadBitmap());
			
		holder.contactitem_account.setText("");
		holder.contactitem_nick.setText(customerVo.getName());
		holder.contactitem_select_cb.setVisibility(1);
		holder.contactitem_select_cb.setTag(customerVo.getUid());
		

		
		
		//holder.contactitem_signature.setText(contacts.get(position).state);
		
		views[position]=convertView;
		return convertView;
	}
	public class ViewHolder{
		public TextView contactitem_catalog;//����
		public TextView contactitem_nick;//�ǳ�
		public ImageView contactitem_avatar_iv;//ͷ��
		public ImageView contactitem_icon;//
		public TextView contactitem_account;//
		public TextView contactitem_signature;//ǩ��
		public CheckBox contactitem_select_cb;//ѡ��ť
	}

}
