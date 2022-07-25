package com.example.bankingsystem.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.repository.IAccountRepository;

@Component
public class Consumer {
	@Autowired
	@Qualifier("mybatisRepository")
	private IAccountRepository accountRepository;
	@KafkaListener(topics = "logs", groupId = "logs_group")
	public void listenTransfer(@Payload String message, 
			  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
	) {
		this.accountRepository.writeAccountLog(message);
	}
}

