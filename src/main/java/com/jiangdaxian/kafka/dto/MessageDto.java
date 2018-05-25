package com.jiangdaxian.kafka.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MessageDto implements Serializable{
	private Object messageInfo;

	public MessageDto(){
		
	}
	
	public MessageDto(Object messageInfo) {
		super();
		this.messageInfo = messageInfo;
	}

	public Object getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(Object messageInfo) {
		this.messageInfo = messageInfo;
	}
}
