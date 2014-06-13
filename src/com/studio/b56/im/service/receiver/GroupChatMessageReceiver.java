package com.studio.b56.im.service.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalDb;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.packet.DelayInformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.UserInfoApi;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.service.SessionListUtil;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.service.XmppManager;
import com.studio.b56.im.service.receiver.PushChatMessage.MyBroadcastReceiver;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.MessageInfo;
import com.studio.b56.im.vo.MessageType;
import com.studio.b56.im.vo.SessionList;
import com.studio.b56.im.vo.UserInfoVo;

public class GroupChatMessageReceiver {
	private BroadcastReceiver broadcastReceiver;
	private XmppManager xmppManager;
    public static final String ACTION_ADD_GROUPCHAT = "com.sns.push.yixun.ACTION_ADD_GROUPCAHT";
	public static final String ACTION_ADD_CLOLSEGROUPCAHT = "com.sns.push.yixun.ACTION_EXIT_GROUPCAHT";
	public static final String ACTION_ADD_GROUP_LOGIN_STATUS = "com.sns.push.yixun.ACTION_GROUP_CHAT_LOGIN_STATUS";
	public static final String ACTION_ADD_GROUP_SENDCHAT = "ACTION_ADD_GROUP_SENDCHAT";
	
	public static final String ACTION_GROUP_SEND_STATE = "ACTION_GROUP_SEND_STATE";
	public static final String ACTION_GROUP_NEWS_MESSAGE = "ACTION_GROUP_NEWS_MESSAGE";
	public static final String EXTRAS_MESSAGE = "extras_messae";
	
	
	private ChatPacketListener chatListener;
	private MyPacketListener myPacketListener;
	private MyParticipantStatusListener myParticipantStatusListener;
	
    private UserInfoVo userInfoVo;
	private static final String TAG = "GroupChatMessage";
	private FinalDb finalDb;
    MultiUserChat muc;
	public String RoomName="";
	CustomerVo cvo=new CustomerVo();
	public String username="";
	public  GroupChatMessageReceiver(CustomerVo vo,XmppManager xmppManager){
		this.xmppManager = xmppManager;
		userInfoVo = new UserInfoCache(xmppManager.getSnsService()).getCacheUserInfo();
		cvo=vo;
		Log.v(TAG, "我的用户信息==="+cvo.getName());
		
	}
	public String GetRoomName()
	{
		return this.RoomName;
	}
	
	public Boolean StartGroupChat(String roomjid)
	{
		 username=cvo.getUid();
		
		 RoomName=roomjid;
	     if(!roomjid.contains("@"))
		 {
				roomjid+="@conference."+xmppManager.getConnection().getServiceName();
		 }
	     username=cvo.getName()+"@"+username;
	     
	     muc=CommFun.joinMultiUserChat(xmppManager.getConnection(),username, roomjid, "");
	     
	     
    	 //发送登录失败。可能是权限不足
 		Intent intent = new Intent(ACTION_ADD_GROUP_LOGIN_STATUS+"_"+ RoomName);
 		
	     if(muc!=null)
		 {
			   intent.putExtra("GROUP_LOGIN_CODE",1);
			   xmppManager.getSnsService().sendBroadcast(intent);
				
				chatListener = new ChatPacketListener(muc);
				myPacketListener = new MyPacketListener();
				myParticipantStatusListener = new MyParticipantStatusListener();
				muc.addMessageListener(chatListener);
				muc.addParticipantListener(myPacketListener);
				muc.addParticipantStatusListener(myParticipantStatusListener);
				
				this.broadcastReceiver = new MyBroadcastReceiver();//监听发布信息
				registerReceiver();
				
				Log.v(TAG, "===+聊天开启成功..");

				return true;
		 }
	     else
	     {
	    	 intent.putExtra("GROUP_LOGIN_CODE",0); 
	    		Log.v(TAG, "===+聊天开启失败..");
			 xmppManager.getSnsService().sendBroadcast(intent);
			 return false;
	     }

			
	}
	
	public void stopGroupChat()
	{
		 xmppManager.getSnsService().unregisterReceiver(broadcastReceiver);
		 muc.removeMessageListener(chatListener);
		 muc.removeParticipantListener(myPacketListener);
		 muc.removeParticipantStatusListener(myParticipantStatusListener);
		 muc.leave();
	}
	
	private void registerReceiver(){
		Log.d(TAG, "registerReceiver()");
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ADD_GROUP_SENDCHAT+"_"+RoomName);//发送群信息
		xmppManager.getSnsService().registerReceiver(broadcastReceiver, filter);
	}
	
	//发送群信息
	//////////////////////////////////
	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "MyBroadcast:onReceive()");
			String action = intent.getAction();
			
			if((ACTION_ADD_GROUP_SENDCHAT+"_"+RoomName).equals(action)){
				
				MessageInfo info = (MessageInfo) intent.getSerializableExtra(EXTRAS_MESSAGE);
				if(info != null){
					pushMessage(info);
				}
				
			}
			
		}
		
	}
	
	//发送群信息
	public void pushMessage(MessageInfo msg) {
		boolean flag = false;
		try {
			flag =sendMessage(msg);
		} catch (Exception e) {
		}
		msg.setSendState(flag ? 1 : 0);
		Log.d(TAG, "pushMessage():" + flag);
		sendBroadcast(msg);
	}
	
	public boolean sendMessage(MessageInfo info) {
		boolean flag = false;
		if(muc != null){
			try {
				JSONObject json = (JSONObject) JSON.toJSON(info);
				json.remove("id");
				json.remove("sendState");
				json.remove("readState");
				json.remove("sessionId");
				json.remove("pullTime");
				if(MessageType.MAP == info.getType()){
					json.put("content", JSONObject.parseObject(info.getContent()));
				}
				muc.sendMessage(json.toJSONString());
				flag = true;
			} catch (XMPPException e) {
				flag = false;
			} catch (IllegalStateException e) {
				// 没连接上服务器
				flag = false;
			}
		}
		
		info.setSendState(flag? 1 : 0);
		return flag;
	}
	
	private void sendBroadcast(MessageInfo msg){
		Log.d(TAG, "sendBroadcast()");
		Intent intent = new Intent(ACTION_GROUP_SEND_STATE+"_"+RoomName);
		intent.putExtra(EXTRAS_MESSAGE, msg);
		xmppManager.getSnsService().sendBroadcast(intent);
		
		try {
			FinalDb finalDb = FinalFactory.createFinalDb(xmppManager.getSnsService(), xmppManager.getSnsService().getUserInfoVo());
			finalDb.update(msg);
		} catch (Exception e) {
			Log.d(TAG, "通知:", e);
		}
	}
	

	class ChatPacketListener implements PacketListener {
		private String _number;
		private Date _lastDate;
		private MultiUserChat _muc;
		private String _roomName;

		public ChatPacketListener(MultiUserChat muc) {
			_number = "0";
			_lastDate = new Date(0);
			_muc = muc;
			_roomName = muc.getRoom();
		}

		@Override
		public void processPacket(Packet packet) {
			
			Log.v(TAG, "来了群组消息:"+ packet.toXML());
			
			Message message = (Message) packet;
			String from = message.getFrom();
			if (message.getBody() != null) 
			{
				DelayInformation inf = (DelayInformation) message.getExtension("x", "jabber:x:delay");
				System.out.println("判断消息");
				if (inf == null)
				{
					System.out.println("新消息来了");
					notifyMessage(message);
				} else {
					System.out.println("旧消息来了===");
					notifyMessage2(message);
				}
			}
		}
	}
	
	public void notifyMessage2(Message msg1) {
		
		try {
			String msg=msg1.getBody();
			JSONObject json = JSON.parseObject(msg);
			MessageInfo info = JSON.toJavaObject(json, MessageInfo.class);
			if(!info.getFromId().equals(username))
			{
			
			if(MessageType.MAP == info.getType()){
					info.setContent(json.getString("content"));}
			
			String msgId=msg1.getPacketID();
			info.setMsgId(msg1.getPacketID());
			if(msg1.getPacketID()==null)
			{
				msgId=CommFun.Md5(info.getContent());
				info.setMsgId(msgId);
			}
			
			if (info != null) {
				saveMessageInfo2(info,msgId);
			}	
			
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notifyMessage(Message msg1) {
		
		try {
			String msg=msg1.getBody();
			JSONObject json = JSON.parseObject(msg);
			MessageInfo info = JSON.toJavaObject(json, MessageInfo.class);
			info.setMsgId(msg1.getPacketID());
			String msgId=msg1.getPacketID();
			if(msg1.getPacketID()==null)
			{
				msgId=CommFun.Md5(info.getContent());
				info.setMsgId(msgId);
			}
			
			if(!info.getFromId().equals(username))
			{

			if(MessageType.MAP == info.getType()){
				info.setContent(json.getString("content"));
			}
			if (info != null) {
				saveMessageInfo(info);
			}	
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    private void saveMessageInfo2(MessageInfo info,String msgId) {
		
		FinalDb db = FinalFactory.createFinalDb(xmppManager.getSnsService(),this.userInfoVo);
		List<MessageInfo> msginfo=db.findAllByWhere(MessageInfo.class, " msgId='"+msgId+"'");
		if(msginfo==null || msginfo.size()<=0)
		{
			
			System.out.println("旧消息入库....."+msgId);
			
		// 1. 获取本地会话列表.
		SessionList sessionList = SessionListUtil.toDbMessage2(xmppManager.getSnsService(), info, userInfoVo,RoomName);
		String lastContent = "";
		info.setSendState(2);
		switch (info.getType()) {
		case MessageType.PICTURE :
			lastContent = "图片";
			break;
		case MessageType.VOICE :
			lastContent = "声音";
			info.setSendState(4);
			break;
		case MessageType.TEXT :
			lastContent = info.getContent();
			break;
		case MessageType.MAP :
			lastContent = "位置信息";
			break;

		default:
			break;
		}

		saveInfo(sessionList, info, lastContent, db);
		
		}
	}
    
    
	
	private void saveMessageInfo(MessageInfo info) {
		
		FinalDb db = FinalFactory.createFinalDb(xmppManager.getSnsService(),this.userInfoVo);
		// 1. 获取本地会话列表.
		SessionList sessionList = SessionListUtil.toDbMessage2(xmppManager.getSnsService(), info, userInfoVo,RoomName);
		String lastContent = "";
		switch (info.getType()) {
		case MessageType.PICTURE :
			lastContent = "图片";
			break;
		case MessageType.VOICE :
			lastContent = "声音";
			info.setSendState(4);
			break;
		case MessageType.TEXT :
			lastContent = info.getContent();
			break;
		case MessageType.MAP :
			lastContent = "位置信息";
			break;

		default:
			break;
		}
		saveInfo(sessionList, info, lastContent, db);
	}
	
	private void saveInfo(SessionList sessionList, MessageInfo info, String lastContent, FinalDb db){
		Log.d(TAG, "saveInfo()");
		info.setPullTime(System.currentTimeMillis() + "");  // 得到消息的时间.
		String fuid = RoomName;  // 得到对方的id.
		if (sessionList == null) {
			sessionList = new SessionList();
			sessionList.setCreateTime(System.currentTimeMillis());
			sessionList.setFuid(RoomName+"@conference");
			//sessionList.setNotReadNum(sessionList.getNotReadNum() + 1);
			sessionList.setUpdateTime(System.currentTimeMillis());
			sessionList.setLastContent(lastContent);
			db.saveBindId(sessionList);
		} else {
			//sessionList.setNotReadNum(sessionList.getNotReadNum() + 1);
			sessionList.setUpdateTime(System.currentTimeMillis());
			sessionList.setLastContent(lastContent);
			db.update(sessionList);
		}

		info.setSessionId(sessionList.getId());
		db.saveBindId(info);  // 保存聊天信息。
		
		/*
		if(!checkLocalFuidInfo(db, fuid)){
			if(downFuidInfo(fuid, db)){
				Log.d(TAG, "downSuccess()");
			}
		}*/
		
		sendBroad(sessionList, info);
	}
	
	// 检查本来是否有Fuid的用户信息
	private boolean checkLocalFuidInfo(FinalDb db, String fuid){
		CustomerVo customerVo = db.findById(fuid, CustomerVo.class);
		if(customerVo == null){
			return false;
		}else{
			return true;
		}
	}
	// 下载用户信息.
		private boolean downFuidInfo(String fuid, FinalDb db){
			Log.d(TAG, "downFuidInfo()");
			String result = new UserInfoApi().getInfo(userInfoVo.getUid(), fuid);
			
			String data = ErrorCode.getData(result);
			if(data != null){
				try {
					CustomerVo customerVo = JSON.toJavaObject(JSON.parseObject(data), CustomerVo.class);
					if(customerVo != null){
						try {
							// 标记为陌生人.
							customerVo.setFriend("0");
							db.save(customerVo);
							return true;
						} catch (Exception e) {
							Log.d(TAG, "downFuidInfo()", e);
							return false;
						}
						
					}else{
						return false;
					}
				} catch (Exception e) {
					Log.d(TAG, "downFuidInfo()", e);
					return false;
				}
			}else{
				return false;
			}
		}
		
		private void sendBroad(SessionList sessionList, MessageInfo info) {
			Log.d(TAG, "sendBroad()");
			Intent intent = new Intent(ACTION_GROUP_NEWS_MESSAGE+"_"+RoomName);
			intent.putExtra("extras_message", info);
			intent.putExtra("extras_session", sessionList);
			//chatMessageNotifiy.notifiy(info);
			if (xmppManager != null && xmppManager.getSnsService() != null) {
				xmppManager.getSnsService().sendBroadcast(intent);
			}
		}
	/**
	 * 聊天室成员的监听器
	 * 
	 * @author 廖乃波
	 * 
	 */
	class MyParticipantStatusListener implements ParticipantStatusListener {

		@Override
		public void adminGranted(String arg0) {
			Log.i(TAG, "执行了adminGranted方法:" + arg0);
		}

		@Override
		public void adminRevoked(String arg0) {
			Log.i(TAG, "执行了adminRevoked方法:" + arg0);
		}

		@Override
		public void banned(String arg0, String arg1, String arg2) {
			Log.i(TAG, "执行了banned方法:" + arg0);
		}

		@Override
		public void joined(String arg0) {
			Log.i(TAG, "执行了joined方法:" + arg0 + "加入了房间");
		}

		@Override
		public void kicked(String arg0, String arg1, String arg2) {
			Log.i(TAG, "执行了kicked方法:" + arg0 + "被踢出房间");
		}

		@Override
		public void left(String arg0) {
			String lefter = arg0.substring(arg0.indexOf("/") + 1);
			Log.i(TAG, "执行了left方法:" + lefter + "离开的房间");
			//getAllMember();
		}

		@Override
		public void membershipGranted(String arg0) {
			Log.i(TAG, "执行了membershipGranted方法:" + arg0);
		}

		@Override
		public void membershipRevoked(String arg0) {
			Log.i(TAG, "执行了membershipRevoked方法:" + arg0);
		}

		@Override
		public void moderatorGranted(String arg0) {
			Log.i(TAG, "执行了moderatorGranted方法:" + arg0);
		}

		@Override
		public void moderatorRevoked(String arg0) {
			Log.i(TAG, "执行了moderatorRevoked方法:" + arg0);
		}

		@Override
		public void nicknameChanged(String arg0, String arg1) {
			Log.i(TAG, "执行了nicknameChanged方法:" + arg0);
		}

		@Override
		public void ownershipGranted(String arg0) {
			Log.i(TAG, "执行了ownershipGranted方法:" + arg0);
		}

		@Override
		public void ownershipRevoked(String arg0) {
			Log.i(TAG, "执行了ownershipRevoked方法:" + arg0);
		}

		@Override
		public void voiceGranted(String arg0) {
			Log.i(TAG, "执行了voiceGranted方法:" + arg0);
		}

		@Override
		public void voiceRevoked(String arg0) {
			Log.i(TAG, "执行了voiceRevoked方法:" + arg0);
		}
	}
	
	public class MyPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet arg0) {
			// 线上--------------chat
			// 忙碌--------------dnd
			// 离开--------------away
			// 隐藏--------------xa
			Presence presence = (Presence) arg0;
			// PacketExtension pe = presence.getExtension("x",
			// "http://jabber.org/protocol/muc#user");
			String LogKineName = presence.getFrom().toString();
			String kineName = LogKineName
					.substring(LogKineName.indexOf("/") + 1);
			String stats = "";
			if ("chat".equals(presence.getMode().toString())) {
				stats = "[线上]";
			}
			if ("dnd".equals(presence.getMode().toString())) {
				stats = "[忙碌]";
			}
			if ("away".equals(presence.getMode().toString())) {
				stats = "[离开]";
			}
			if ("xa".equals(presence.getMode().toString())) {
				stats = "[隐藏]";
			}

			
		}
	}
	
}
