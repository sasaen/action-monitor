/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sasaen.monitor.jms;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.sasaen.monitor.ApplicationConstants;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseJmsListenerTest {


    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private DatabaseJmsListener listener;

    @Test
    public void test() throws Exception {
    	jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend(ApplicationConstants.JMS_TOPIC, "JMS message");
        
        // TODO find out a better way to wait and get the message from the JMSListener
        Thread.sleep(1000);
        assertEquals("JMS message", listener.getLastMessageSent());
    }

}
