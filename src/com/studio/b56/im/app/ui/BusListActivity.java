/**
 * @Title: CityActivity.java 
 * @Package com.example.baidumap 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Alex.Z   
 * @date 2013-5-9 下午1:44:07 
 * @version V1.0
 */
package com.studio.b56.im.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.studio.b56.im.R;

public class BusListActivity extends Activity {
	/** 组件相关 */
	private ListView cityList;
	/** 属性 */
	private String[] city;
	private String[] lon,lat;
	private ArrayList<HashMap<String, Object>> listItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
	}



}
