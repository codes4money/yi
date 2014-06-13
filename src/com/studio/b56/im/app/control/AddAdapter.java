package com.studio.b56.im.app.control;

import java.util.ArrayList;

import com.studio.b56.im.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<AddBean> mArr;

	public AddAdapter(Context c, ArrayList<AddBean> arr) {
		this.mContext = c;
		this.mArr = arr;
	}

	@Override
	public int getCount() {
		return mArr != null ? mArr.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mArr != null ? mArr.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return mArr != null ? position : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.add_popu_item, null);
			vh.addPopuImg = (ImageView) convertView
					.findViewById(R.id.addPopuImg);
			vh.addPopuTxt = (TextView) convertView
					.findViewById(R.id.addPopuTxt);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.addPopuImg.setImageResource(mArr.get(position).getResImg());
		vh.addPopuTxt.setText(mArr.get(position).getContent());
		return convertView;
	}

	public static class ViewHolder {
		ImageView addPopuImg;
		TextView addPopuTxt;
	}
}
