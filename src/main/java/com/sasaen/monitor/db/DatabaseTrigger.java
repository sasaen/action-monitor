package com.sasaen.monitor.db;

import org.hsqldb.Trigger;
import org.hsqldb.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;

import com.sasaen.monitor.ApplicationConstants;
import com.sasaen.monitor.jms.DatabaseMessage;

/**
 * This class is registered as a Trigger listener in db/sql/create-db.sql. It
 * receives notifications when INSERT, UPDATE or DELETE are executed on the
 * USERS table.
 * 
 * These notifications are sent to a JMS topic.
 * 
 * @author sasaen
 *
 */
@Controller
public class DatabaseTrigger implements Trigger, ApplicationContextAware {

	// TODO a new a DatabaseTrigger is created from hsqldb every time a trigger
	// is fired,
	// so I made the context static. Find another way to get the context.
	private static ApplicationContext context;

	private Logger logger = LoggerFactory.getLogger(DatabaseTrigger.class);

	/**
	 * A sample HSQLDB Trigger interface implementation.
	 * <p>
	 *
	 * This sample prints information about the firing trigger and records
	 * actions in an audit table.
	 * <p>
	 *
	 * The techniques used here are simplified dramatically for demonstration
	 * purposes and are in no way recommended as a model upon which to build
	 * actual installations involving triggered actions.
	 *
	 * @param typ
	 *            trigger type
	 * @param trn
	 *            trigger name
	 * @param tn
	 *            table name
	 * @param or
	 *            old row
	 * @param nr
	 *            new row
	 */
	public void fire(int typ, String trn, String tn, Object[] or, Object[] nr) {

		String oldRow = or == null ? "null" : StringUtil.arrayToString(or);
		String newRow = nr == null ? "null" : StringUtil.arrayToString(nr);

		if ("USERS".equals(tn)) {
			String message = null;
			switch (typ) {

			case INSERT_AFTER_ROW:
				message = "Inserted=" + newRow;
				break;
			case UPDATE_AFTER_ROW:
				message = "Updated=" + newRow;
				break;
			case DELETE_AFTER_ROW:
				message = "Deleted=" + oldRow;
				break;
			default:
				break;

			}

			sendJmsMessage(message);
		}
	}

	private void sendJmsMessage(String message) {
		// How else can I get the context here?
		if (getContext() == null) {
			return;
		}
		JmsTemplate jmsTemplate = getContext().getBean(JmsTemplate.class);

		// Send a message with a POJO - the template reuse the message
		// converter
		logger.info("Sending a JMS message: [" + message + "]");
		if (message != null) {
			jmsTemplate.setPubSubDomain(true);
			jmsTemplate.convertAndSend(ApplicationConstants.JMS_TOPIC, new DatabaseMessage(message));
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		DatabaseTrigger.context = applicationContext;
	}

	private ApplicationContext getContext() {
		return DatabaseTrigger.context;
	}

}
