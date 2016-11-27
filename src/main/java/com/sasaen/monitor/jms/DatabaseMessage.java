package com.sasaen.monitor.jms;

/**
 * POJO class that contains the database message to be notified.
 * @author sasaen
 *
 */
public class DatabaseMessage {

	private String content;

	public DatabaseMessage() {
	}

	public DatabaseMessage(String message) {
		this.content = message;
	}

	public String getContent() {
		return content;
	}

}
