package com.sasaen.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.sasaen.monitor.db.DatabaseTrigger;
import com.sasaen.monitor.jms.DatabaseMessage;

/**
 * This is an action-monitor Integration test.
 * 
 * Invokes the DatabaseTrigger trigger method in order to get messages sent to JMS --> WebSocket.
 * 
 * <li>Invoke the trigger method 3 times, UPDATE, INSERT and DELETE</li>
 * <li>The trigger publishes one JMS message per trigger invocation to a JMS topic.</li>
 * <li>JMS messages are Received by DatabaseJmsListener</li>
 * <li>DatabaseJmsListener sends the content of the JMS messages to the WebSocketController</li>
 * <li>The WebSocketController publishes the content of the JMS messages into the WebSocketSession</li>
 * @author salsan01
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MonitorIntegrationTests {

    @LocalServerPort
    private int port;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    
	@Autowired
	private ApplicationContext context;

    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void getDatabaseMessage() throws Exception {

        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final List<DatabaseMessage> result = new ArrayList<>();

        StompSessionHandler handler = new TestSessionHandler(failure) {

            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
            	
            	

            	
                session.subscribe(ApplicationConstants.WEBSOCKET_TOPIC, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return DatabaseMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                    	
                    	try {
                    		DatabaseMessage message = (DatabaseMessage) payload;
                    		result.add(message);
                        } catch (Throwable t) {
                            failure.set(t);
                        } finally {
//                            session.disconnect();
                            latch.countDown();
                        }   
                    }
                });
                
                try {
                	// Trigger database to get messages sent to JMS --> WebSocket 
                    DatabaseTrigger trigger = context.getBean(DatabaseTrigger.class);
                    trigger.fire(DatabaseTrigger.INSERT_AFTER_ROW, "", "USERS", null, new Object[]{1, "mkyong", "mkyong@gmail.com"});                 
                    trigger.fire(DatabaseTrigger.UPDATE_AFTER_ROW, "", "USERS", null, new Object[]{1, "mkyong", "mkyong@gmail.com"});
                    trigger.fire(DatabaseTrigger.DELETE_AFTER_ROW, "", "USERS", new Object[]{1, "mkyong", "mkyong@gmail.com"}, null);
                                        
				} catch (Exception e) {
					failure.set(e);
				}
                
                
            }
        };

        this.stompClient.connect("ws://localhost:{port}/database-websocket", this.headers, handler, this.port);

        if (latch.await(4, TimeUnit.SECONDS)) {
        	
        	// Check messages received in the websocket session
        	assertEquals(3, result.size());
        	assertEquals("Inserted={1,mkyong,mkyong@gmail.com}", result.get(0).getContent());
            assertEquals("Updated={1,mkyong,mkyong@gmail.com}", result.get(1).getContent());
            assertEquals("Deleted={1,mkyong,mkyong@gmail.com}", result.get(2).getContent());
            
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
        }
        else {
            fail("Greeting not received");
        }

    }

    private class TestSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;


        public TestSessionHandler(AtomicReference<Throwable> failure) {
            this.failure = failure;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
        }
    }
}
