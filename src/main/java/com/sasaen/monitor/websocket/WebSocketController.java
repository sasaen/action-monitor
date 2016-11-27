package com.sasaen.monitor.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.sasaen.monitor.ApplicationConstants;
import com.sasaen.monitor.jms.DatabaseMessage;

@Controller
public class WebSocketController {
	
	private Logger logger = LoggerFactory.getLogger(WebSocketController.class);

	@Autowired
	private SimpMessagingTemplate template;

	public void sendMessage(DatabaseMessage message) {

		logger.info("Send message over websocket ["+message+"]");
		this.template.convertAndSend(ApplicationConstants.WEBSOCKET_TOPIC, message);
	}

}
