package com.jiangdaxian.kafka.util;

import org.apache.commons.lang3.StringUtils;

public class KafkaUtil {
	public static final String GRAY_KAFKA = "gray_";
	public static boolean iskafkaGray(String kafkaGray){
		boolean isGray;
		if(StringUtils.isBlank(kafkaGray)){
			isGray = false;
		}else{
			try{
				Boolean kafkaIsGrap = Boolean.valueOf(kafkaGray);
				if(kafkaIsGrap!=null && Boolean.TRUE.equals(kafkaIsGrap)){
					isGray = true;
				}else{
					isGray = false;
				}
			}catch(Exception e){
				isGray = false;
			}
		}
		return isGray;
	}
}
