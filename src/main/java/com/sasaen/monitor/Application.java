package com.sasaen.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application class that implements an action-monitor on a database using
 * Tomcat, Rest endpoints, WebSockets, ActiveMQ and HSQLDB within Spring.
 * 
 * @author sasaen
 *
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
