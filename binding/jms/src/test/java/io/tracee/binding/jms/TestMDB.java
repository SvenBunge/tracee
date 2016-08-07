package io.tracee.binding.jms;

import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@MessageDriven
@Interceptors(TraceeMessageListener.class)
public class TestMDB implements MessageListener {

	@Resource
	private ConnectionFactory connectionFactory;

	@Resource(name = "Response")
	private Queue responses;

	@Override
	public void onMessage(Message message) {
		final TextMessage incomingMessage = (TextMessage) message;

		Connection connection = null;
		Session session = null;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			final MessageProducer producer = TraceeMessageWriter.wrap(session.createProducer(responses));
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			final TextMessage responseMessage = session.createTextMessage(incomingMessage.getText());
			producer.send(responseMessage);
		} catch (JMSException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				if (session != null) session.close();
				if (connection != null) connection.close();
			} catch (JMSException ignored) {
			}
		}

	}
}
