package com.studio.b56.im.service.receiver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import net.tsz.afinal.FinalDb;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.service.XmppManager;
import com.studio.b56.im.vo.MessageInfo;

/**
 * 
 * 功能：发送聊天信息. <br />
 * 日期：2013-4-27<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class PushChatMessage implements PushMessage{
	private static final String TAG = "PushChatMessage";
	/**
	 * 可以接收该广播，帮助请求者发送消息<br/>
	 * 附加消息:{@link PushChatMessage#EXTRAS_MESSAGE}
	 */
	public static final String ACTION_SEND_MESSAGE = "com.sns.push.yixun.ACTION_SEND_MESSAGE";
	
	public static final String ACTION_ADD_GROUP = "com.sns.push.yixun.ACTION_DD_GROUP_群";
	/**
	 * 返回发送消息的结果<br/>
	 * 附加消息:{@link PushChatMessage#EXTRAS_MESSAGE}
	 */
	public static final String ACTION_SEND_STATE = "com.sns.push.yixun.ACTION_SEND_STATE";
	/**
	 * 附加信息变量.<br/>
	 * 属性值:{@link MessageInfo}
	 */
	public static final String EXTRAS_MESSAGE = "extras_messae";
	
	private BroadcastReceiver broadcastReceiver;
	private XmppManager xmppManager;
	public PushChatMessage(XmppManager xmppManager){
		this.xmppManager = xmppManager;
		
		init();
	}
	
	private void init(){
		Log.d(TAG, "init()");
		this.broadcastReceiver = new MyBroadcastReceiver();
		registerReceiver();
	}
	
	private void registerReceiver(){
		Log.d(TAG, "registerReceiver()");
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SEND_MESSAGE);
		filter.addAction(ACTION_ADD_GROUP);
		filter.addAction(SnsService.ACTION_SERVICE_STOP);
		xmppManager.getSnsService().registerReceiver(broadcastReceiver, filter);
		
	}
	
	private void unregisterReceiver(){
		Log.d(TAG, "unregisterReceiver()");
		xmppManager.getSnsService().unregisterReceiver(broadcastReceiver);
	}
	@Override
	public void pushMessage(MessageInfo msg) {
		boolean flag = false;
		try {
			flag = xmppManager.getSnsMessageLisener().sendMessage(msg);
		} catch (Exception e) {
		}
		msg.setSendState(flag ? 1 : 0);
		Log.d(TAG, "pushMessage():" + flag);
		sendBroadcast(msg);
	}
	
	
	private void sendBroadcast(MessageInfo msg){
		Log.d(TAG, "sendBroadcast()");
		Intent intent = new Intent(ACTION_SEND_STATE);
		intent.putExtra(EXTRAS_MESSAGE, msg);
		xmppManager.getSnsService().sendBroadcast(intent);
		
		try {
			FinalDb finalDb = FinalFactory.createFinalDb(xmppManager.getSnsService(), xmppManager.getSnsService().getUserInfoVo());
			finalDb.update(msg);
		} catch (Exception e) {
			Log.d(TAG, "通知:", e);
		}
	}
	
	 public Boolean AddGroup(String RoomName,String MyUserName,String des)
		{
			MultiUserChat muc= new MultiUserChat(xmppManager.getConnection(), RoomName+"@conference."+xmppManager.getConnection().getServiceName());        //第二个参数是房间的Jid
			try {
	                muc.create(RoomName);      //房间名称
	              
	        } catch (XMPPException e) {
	        	Log.v("创建错误", "=="+e.getMessage());
	        	return false;
	        }    
	        try {
	              // 获得聊天室的配置表单
	              Form form = muc.getConfigurationForm();
	              // 根据原始表单创建一个要提交的新表单。
	              Form submitForm = form.createAnswerForm();
	              // 向要提交的表单添加默认答复
	              for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {  
	             try {
	            	 
	                  FormField field = (FormField) fields.next();  
	       
	                  if (!FormField.TYPE_HIDDEN.equals(field.getType())  && field.getVariable() != null) {  
	                      // 设置默认值作为答复  
	                      submitForm.setDefaultAnswer(field.getVariable());  
	                  } 
	                  
	            	  } catch (Exception e) {Log.v("创建错误", "=="+e.getMessage());
	                  }
	              }  
	              
	                      //设置聊天室的新拥有者    
	            List<String> owners = new ArrayList<String>();   
	            owners.add(MyUserName);   
	            submitForm.setAnswer("muc#roomconfig_roomowners", owners);   
	                 // 设置聊天室是持久聊天室，即将要被保存下来  
	            submitForm.setAnswer("muc#roomconfig_persistentroom", true);  

	            // 房间仅对成员开放  
	            submitForm.setAnswer("muc#roomconfig_membersonly", false);  
	  
	            //设置描述    
	            submitForm.setAnswer("muc#roomconfig_roomdesc",des);

	          //  // 允许占有者邀请其他人  
	            submitForm.setAnswer("muc#roomconfig_allowinvites", true);  
	            // 能够发现占有者真实 JID 的角色  
	          //  submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
	            // 登录房间对话  
	            submitForm.setAnswer("muc#roomconfig_enablelogging", true); 
	            // 仅允许注册的昵 称登录  
	            submitForm.setAnswer("x-muc#roomconfig_reservednick", false);  
	            // 允许使用者修改昵称  
	            submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);  
	            submitForm.setAnswer("x-muc#roomconfig_registration", true);
	            
				 muc.sendConfigurationForm(submitForm);
				 return true;
				} catch (XMPPException e) {
					Log.v("创建错误2", "=="+e.getMessage());
					return false;
				}
		}
	 
	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "MyBroadcast:onReceive()");
			String action = intent.getAction();
			if(ACTION_SEND_MESSAGE.equals(action)){
				MessageInfo info = (MessageInfo) intent.getSerializableExtra(EXTRAS_MESSAGE);
				if(info != null){
					pushMessage(info);
				}
			}
			else if(ACTION_ADD_GROUP.equals(action))
			{
				String roomname=intent.getStringExtra("group_roomname");
				String myusername=intent.getStringExtra("group_myusername");
				String roomdes=intent.getStringExtra("group_roomdes");
				try {
				   AddGroup(roomname,myusername+"@"+xmppManager.getConnection().getServiceName(),roomdes);
            	  } catch (Exception e) {Log.v("创建错误", "=="+e.getMessage());}
			}
			else if(SnsService.ACTION_SERVICE_STOP.equals(action)){
				unregisterReceiver();
			}
		}
		
	}
	
}
