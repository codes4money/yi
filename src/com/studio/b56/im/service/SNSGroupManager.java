package com.studio.b56.im.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.http.AjaxParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.app.ChatMessageNotifiy;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.service.receiver.GroupChatMessageReceiver;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.MessageInfo;
import com.studio.b56.im.vo.UserInfoVo;
import com.studio.b56.im.app.FinalFactory;

public class SNSGroupManager {
	
	public XmppManager xmppManager;
	public UserInfoVo userInfoVo;
	CustomerVo cvo=new CustomerVo();
    public static final String ACTION_ADD_GROUPCHAT = "com.sns.push.yixun.ACTION_ADD_GROUPCAHT";
    public static final String ACTION_ADD_GROUPUSER = "com.sns.push.yixun.ACTION_ADD_GROUPUSER";
	private static final String TAG = "SNSGroupManager";
	private FinalDb finalDb;
	private BroadcastReceiver broadcastReceiver;
	GroupChatMessageReceiver cmre;
	LinkedList<GroupChatMessageReceiver> allcmre =new LinkedList<GroupChatMessageReceiver>();
	
	
	public  SNSGroupManager(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
		this.userInfoVo = xmppManager.getSnsService().getUserInfoVo();
		
		userInfoVo= new UserInfoCache(xmppManager.getSnsService().getBaseContext()).getCacheUserInfo();
		finalDb = FinalFactory.createFinalDb(xmppManager.getSnsService().getBaseContext(),userInfoVo);
		cvo=finalDb.findById(userInfoVo.getUid(), CustomerVo.class);
		
		this.broadcastReceiver = new MyBroadcastReceiver();//监听发布信息
		registerReceiver();
		
		Log.v(TAG, "成功开启群服务..");
		
	}
	
	private void registerReceiver(){
		Log.d(TAG, "registerReceiver()");
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ADD_GROUPCHAT);//发送群信息
		filter.addAction(ACTION_ADD_GROUPUSER);
		xmppManager.getSnsService().registerReceiver(broadcastReceiver, filter);
	}
	
class LoginAjaxCallBack extends ClientAjaxCallback{	
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			Log.v("群组邀请===", "==="+ t);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			Log.v("群组邀请2===", "==="+ strMsg);
		}
		
	}

	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "MyBroadcast:onReceive()");
			String action = intent.getAction();
			
			Log.v(TAG, "..."+action);
			
			if((ACTION_ADD_GROUPCHAT).equals(action)){
				
				String group_name=intent.getStringExtra("group_name");
				
				try
				{
					
				for(int i=0;i<allcmre.size();i++)
				{
					if(allcmre.get(i).RoomName.equals(group_name))
					{
						allcmre.get(i).stopGroupChat();
						allcmre.remove(i);
						break;
					}
				}
				
				cmre=new GroupChatMessageReceiver(cvo,xmppManager);
				
				if(cmre.StartGroupChat(group_name))
				{
					allcmre.add(cmre);	
					Log.v(TAG, "成功开启一个群组列表...");
				}
				
				
				}catch(Exception e){}

			}
			else if((ACTION_ADD_GROUPUSER).equals(action))
			{
				String addUserids=intent.getStringExtra("addUserids");
				String roomName=intent.getStringExtra("roomName");
				String jid=roomName;
				jid=jid+"@conference."+xmppManager.getConnection().getServiceName();
				String orgName = "@"+xmppManager.getConnection().getServiceName();
				
		    	 for(int ii=0;ii< addUserids.split(";").length;ii++)
		    	 {
		    		 try {
		    			    //发送群邀请
				        	MultiUserChat muc=new MultiUserChat(xmppManager.getConnection(),jid);
				        	muc.invite(addUserids.split(";")[ii]+orgName, "一起来聊聊吧");
				        	muc.grantMembership(addUserids.split(";")[ii]+orgName);
				        	
				        	FinalHttp finalHttp = new FinalHttp();
				    		AjaxParams params = new AjaxParams();
				    		params.put("toUid", addUserids.split(";")[ii]);
				    		params.put("uid", cvo.getUid());
				    		params.put("type", "21");
				    		params.put("content", roomName);
				    		finalHttp.post(Constants.ApiUrl.BASE_URL+"/notice/Index/action/", params,new LoginAjaxCallBack());
				    		
				        	
		    		 } catch (Exception e) {
						e.printStackTrace();
					}
		    	 }
			}
			
		}
		
	}
	
}
