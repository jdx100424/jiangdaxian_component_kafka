package com.jiangdaxian.kafka.dto;

public enum MessageVo {
	/**
	 * ECHO KAFKA消息测试
	 */
	ECHO_MESSAGE("groupEchoTest","topicEchoTest"),
	ECHO_MESSAGE_SUB("groupEchoTest","topicEchoSubTest"),
	JDX_MESSAGE("groupJdxTest","topicJdxTest");

	private String groupId;

	private String topicName;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	private MessageVo(String groupId, String topicName) {
		this.groupId = groupId;
		this.topicName = topicName;
	}
}
