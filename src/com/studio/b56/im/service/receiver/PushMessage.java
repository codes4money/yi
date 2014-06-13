package com.studio.b56.im.service.receiver;

import com.studio.b56.im.vo.MessageInfo;

/**
 * 
 * 功能：推送信息,好友之间的信息推送. <br />
 * 日期：2013-4-27<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public interface PushMessage {
	void pushMessage(MessageInfo msg);
}
