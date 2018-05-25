package com.jiangdaxian.kafka;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSONObject;
import com.jiangdaxian.common.async.AsyncTaskProcesser;
import com.jiangdaxian.common.base.ServerLauncherStatus;
import com.jiangdaxian.kafka.dto.MessageDto;
import com.jiangdaxian.kafka.dto.MessageVo;
import com.jiangdaxian.kafka.util.KafkaUtil;

/**
 * KAFKA接收端的代码
 * 
 * @author jdx
 *
 */
public abstract class BaseConsumer extends AsyncTaskProcesser implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseConsumer.class);
	// 组ID
	private String groupId;
	// KAKFA消息接收名称
	private String topicName;
	
	private String kafkaIp;
	
	private String kafkaPort;
	
	private String kafkaServer;

	
	
	@Autowired
	@Qualifier("baseProducer")
	private BaseProducer baseProducer;

	public BaseConsumer(String groupId, String topicName, String kafkaIp, String kafkaPort, String kafkaServer) {
		super();
		this.groupId = groupId;
		this.topicName = topicName;
		this.kafkaIp = kafkaIp;
		this.kafkaPort = kafkaPort;
		this.kafkaServer = kafkaServer;
	}

	@SuppressWarnings("resource")
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(topicName)) {
			throw new Exception("groupId and topicName is not allow empty,class is " + this.getClass().getName());
		}
		Properties props = new Properties();

		props.put("bootstrap.servers", kafkaServer);
		LOGGER.info("class:" + this.getClass().getName() + ",kafkaIp:" + kafkaIp + ",kafkaPort:" + kafkaPort
				+ ",kafkaServer:" + kafkaServer);

		// 消费者的组id
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		// props.put("auto.offset.reset", "earliest");
		// 从poll(拉)的回话处理时长
		props.put("session.timeout.ms", "30000");
		// poll的数量限制
		// props.put("max.poll.records", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		// 订阅主题列表topic

		consumer.subscribe(Arrays.asList(topicName));

		// 使用线程监控KAFKA接收信息
		new Thread() {
			public void run() {
				// 等待服务完全启动后再消费消息
				LOGGER.info("{},kafka启动线程，进行服务完全启动等待start：{}", Thread.currentThread().getName(), new Date());
				ServerLauncherStatus.get().waitStarted();
				LOGGER.info("{},kafka启动线程，进行服务完全启动等待end：{}", Thread.currentThread().getName(), new Date());

				while (true) {
					ConsumerRecords<String, String> records = consumer.poll(100);
					for (ConsumerRecord<String, String> record : records) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("receive messag,topic:{},partition:{},offset:{}", record.topic(),
									record.partition(), record.offset());
						}

						// 当处理线程满后，阻塞处理线程
						while (executor.getMaximumPoolSize() <= executor.getActiveCount()) {
							try {
								Thread.sleep(200);
							} catch (Exception e) {

							}
						}
						runInExecutor(record);
					}
				}
			}
		}.start();
		LOGGER.info(this.getClass().getName() + " kafka is start,topicName is:" + topicName);
	}

	protected void runInExecutor(ConsumerRecord<String, String> record) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					String receiveStr = record.value();
					MessageDto dto = JSONObject.parseObject(receiveStr, MessageDto.class);
					onMessage(dto);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {

				}
			}
		});
	}

	/**
	 * KAFKA接收信息后实际的操作逻辑
	 * 
	 * @param dto
	 */
	public abstract void onMessage(MessageDto dto);
}
