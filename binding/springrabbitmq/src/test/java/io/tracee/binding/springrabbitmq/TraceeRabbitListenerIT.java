package io.tracee.binding.springrabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TraceeRabbitListenerIT {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private Queue queue1;

	@Autowired
	private TraceeRabbitListener rabbitListener;

	@Test
	public void testTwoWay() throws Exception {
//		this.rabbitTemplate.convertAndSend(this.queue1.getName(), "foo");

//		Mockito.verify(rabbitListener).readTpicHeaderFromMessage(Matchers.anyMapOf(String.class, Object.class));
//		Mockito.verify(rabbitListener).cleanTpicAfterMessage();

//		RabbitListenerTestHarness.InvocationData invocationData = this.harness.getNextInvocationDataFor("foo", 10, TimeUnit.SECONDS);
//		assertNotNull(invocationData);
//		assertThat((String) invocationData.getArguments()[0], equalTo("foo"));
//		assertThat((String) invocationData.getResult(), equalTo("FOO"));
	}

	@Configuration
	@RabbitListenerTest
	public static class Config {

		@Bean
		public TraceeRabbitListener traceeMessageHandler() {
			return new TraceeRabbitListener();
		}

		@Bean(name = TraceeRabbitListener.DEFINE_QUEUES)
		public List<String> traceeQueues() {
			return Collections.singletonList("queue1");
		}

		@Bean
		public ConnectionFactory connectionFactory() {
			return new CachingConnectionFactory("localhost");
		}

		@Bean
		public Queue queue1() {
			return new AnonymousQueue();
		}


		@Bean
		public RabbitTemplate template() {
			final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
			rabbitTemplate.setMessagePropertiesConverter(new TraceeMessagePropertiesConverter());
			return rabbitTemplate;
		}

		@Bean
		public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf) {
			SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
			containerFactory.setConnectionFactory(cf);
			return containerFactory;
		}
	}

}
