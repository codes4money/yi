package com.studio.b56.im.app.ui.adpater;

import java.util.ArrayList;

import com.studio.b56.im.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ShopGridAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ShopGridBean> mArr;

	public ShopGridAdapter(Context c, ArrayList<ShopGridBean> arr) {
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
					R.layout.shopgrid_item, null);
			vh.shopImg = (ImageView) convertView.findViewById(R.id.shopImg);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.shopImg.setImageResource(mArr.get(position).getResImg());
		return convertView;
	}

	public static class ViewHolder {
		ImageView shopImg;
	}
}
