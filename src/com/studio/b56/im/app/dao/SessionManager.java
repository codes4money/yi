package com.studio.b56.im.app.dao;

import java.io.File;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.core.FileNameGenerator;
import android.content.Context;
import android.util.Log;

import com.studio.b56.im.app.control.ReaderImpl;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.MessageInfo;
import com.studio.b56.im.vo.MessageType;
import com.studio.b56.im.vo.SessionList;

public class SessionManager {
	private static final String TAG = "delMsg";
	private FinalBitmap finalBitmap;
	private File audioPath;	// 音频保存路径
	private FinalDb finalDb;
	
	/**
	 * 图片FinalBitmap类
	 * @param finalDb  本用户的数据库
	 * @param finalBitmap
	 * @param context
	 */
	public SessionManager(FinalDb finalDb, FinalBitmap finalBitmap, Context context) {
		super();
		this.finalBitmap = finalBitmap;
		audioPath = ReaderImpl.getAudioPath(context);
		this.finalDb = finalDb;
	}
	
	public boolean delFriend(CustomerVo customerVo){
		Log.d(TAG, "delFriend()");
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("fuid = '").append(customerVo.getUid()).append("'");
			List<SessionList> sessionLists = finalDb.findAllByWhere(SessionList.class, buffer.toString());
			SessionList sessionList = null;
			if(sessionLists != null && !sessionLists.isEmpty()){
				sessionList = sessionLists.get(0);
			}else{
				return false;
			}
			return delSessionList(sessionList);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delSessionList(SessionList sessionList){
		Log.d(TAG, "delSessionList()");
		if(sessionList == null){
			return false;
		}
		try {
			List<MessageInfo> infos = finalDb.findAllByWhere(MessageInfo.class, fileMsg(sessionList.getId()));
			if(infos != null){
				for (MessageInfo messageInfo : infos) {
					delMsg(messageInfo);
				}
			}
			finalDb.delete(sessionList);
			finalDb.deleteByWhere(MessageInfo.class, sessionList2MsgWhere(sessionList.getId()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean clearSessionList(SessionList sessionList, List<MessageInfo> infos){
		try {
			for (MessageInfo info : infos) {
				delMsg(info);
			}
			finalDb.deleteByWhere(MessageInfo.class, sessionList2MsgWhere(sessionList.getId()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean clearSessionList2(SessionList sessionList){
		try {
			finalDb.deleteByWhere(MessageInfo.class, sessionList2MsgWhere(sessionList.getId()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean delMsgDb(MessageInfo msg){
		Log.d(TAG, "delMsgDb()");
		try {
			finalDb.delete(msg);
			delMsg(msg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void delMsg(MessageInfo msg){
		Log.d(TAG, "delMsg()");
		int type = msg.getType();
		if(MessageType.PICTURE == type){
			delBitmapMsg(msg);
		}else if(MessageType.VOICE == type){
			delVoiceMsg(msg);
		}
	}
	
	private void delBitmapMsg(MessageInfo msg){
		Log.d(TAG, "delBitmapMsg()");
		String key = msg.getContent();
		finalBitmap.clearCache(key);
	}
	
	private void delVoiceMsg(MessageInfo msg){
		Log.d(TAG, "delVoiceMsg()");
		String name = FileNameGenerator.generator(msg.getContent());
		File voiceFile = new File(audioPath, name);
		if(voiceFile.exists()){
			voiceFile.delete();
		}
	}
	
	public static String fileMsg(int sessionId){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("(sessionId = '")
		.append(sessionId).append("'")
		.append("or sessionId = ")
		.append(sessionId).append(") and (type = '4' or type = 4 or type = 3 or type = '3') ");
		return stringBuffer.toString();
	}
	
	public static String sessionList2MsgWhere(int sessionId){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("sessionId = '")
		.append(sessionId).append("'")
		.append("or sessionId = ")
		.append(sessionId);
		
		return stringBuffer.toString();
	}
}
