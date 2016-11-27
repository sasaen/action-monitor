package com.sasaen.monitor.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.sasaen.monitor.ApplicationConstants;
import com.sasaen.monitor.websocket.WebSocketController;

/**
 * This JMSListener is subscribed to the topic
 * <code>ApplicationConstants.JMS_TOPIC</code> and hands over the messages to
 * the <code>WebSocketController</code>
 * 
 * @author sasaen
 *
 */
@Component
public class DatabaseJmsListener {
	private Logger logger = LoggerFactory.getLogger(DatabaseJmsListener.class);

	@Autowired
	private ApplicationContext context;
	
	// Used for testing.
	private String lastMessageSent;
	

	@JmsListener(destination = ApplicationConstants.JMS_TOPIC, containerFactory = "myFactory")
	public void receiveMessage(DatabaseMessage message) {
		logger.info("Received JMS message [" + message.getContent() + "]");

		WebSocketController bean = context.getBean(WebSocketController.class);
		bean.sendMessage(message);
		
		lastMessageSent = message.getContent();
	}

	String getLastMessageSent() {
		return lastMessageSent;
	}
}
