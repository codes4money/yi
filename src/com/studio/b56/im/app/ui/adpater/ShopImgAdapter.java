package com.studio.b56.im.app.ui.adpater;

import java.util.ArrayList;

import com.studio.b56.im.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ShopImgAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ShopImgBean> mArr;

	public ShopImgAdapter(Context c, ArrayList<ShopImgBean> arr) {
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
					R.layout.shopimg_item, null);
			vh.img = (ImageView) convertView.findViewById(R.id.shopImgItem);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.img.setImageResource(mArr.get(position % mArr.size()).getResImg());
		return convertView;
	}

	public static class ViewHolder {
		ImageView img;
	}
}
