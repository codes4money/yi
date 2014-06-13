package com.studio.b56.im.app.ui;

import java.util.ArrayList;

import com.studio.b56.im.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApproximatelyAdapter extends BaseAdapter {

	private Context mContext;
	public ArrayList<ApproximatelyBean> mArr;

	public ApproximatelyAdapter(Context c, ArrayList<ApproximatelyBean> arr) {
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
					R.layout.activity_approximately_item, null);
			vh.aItemImg = (ImageView) convertView.findViewById(R.id.aItemImg);
			vh.aItemArrow = (ImageView) convertView
					.findViewById(R.id.aItemArrow);
			vh.aItemTxt = (TextView) convertView.findViewById(R.id.aItemTxt);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.aItemImg.setImageResource(mArr.get(position).getResImg());
		vh.aItemTxt.setText(mArr.get(position).getTitle());
		vh.aItemArrow.setImageResource(R.drawable.arrow);
		return convertView;
	}

	public static class ViewHolder {
		ImageView aItemImg, aItemArrow;
		TextView aItemTxt;
	}
}
