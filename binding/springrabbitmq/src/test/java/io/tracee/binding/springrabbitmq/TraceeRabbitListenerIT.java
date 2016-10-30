package io.tracee.binding.springrabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class TraceeRabbitListenerIT {

	@Autowired
	private RabbitTemplate rabbitTemplate;


	@Autowired
	private Queue queue1;

	@Autowired
	private TraceeRabbitListener rabbitListener;

	@Test
	public void testTwoWay() throws Exception {
		assertEquals("FOO", this.rabbitTemplate.convertSendAndReceive(this.queue1.getName(), "foo"));

//		RabbitListenerTestHarness.InvocationData invocationData = this.harness.getNextInvocationDataFor("foo", 10, TimeUnit.SECONDS);
//		assertNotNull(invocationData);
//		assertThat((String) invocationData.getArguments()[0], equalTo("foo"));
//		assertThat((String) invocationData.getResult(), equalTo("FOO"));
	}

	@Configuration
	@RabbitListenerTest
	public static final class Config {

		@Bean
		public TraceeRabbitListener traceeMessageHandler() {
			return new TraceeRabbitListener();
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
			return new RabbitTemplate(connectionFactory());
		}

		@Bean
		public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf) {
			SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
			containerFactory.setConnectionFactory(cf);
			return containerFactory;
		}
	}

}
