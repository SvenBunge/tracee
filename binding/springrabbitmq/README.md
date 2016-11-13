> This document contains documentation for the tracee-springrabbitmq module. Check the [TracEE main documentation](/README.md) to get started.

# tracee-springrabbitmq

This module writes and retrieves TPIC header information to/from [RabbitMQ](http://www.rabbitmq.com/) messages with the [Spring-AMQP](http://projects.spring.io/spring-amqp/) abstraction.

 * __TraceeMessagePropertiesConverter__: Inherits from `DefaultMessagePropertiesConverter and handles the TPIC information from/to messages.

## Installation

If you're use Maven for your dependency management simple add this to your pom:

```xml
<dependencies>
...
    <dependency>
        <groupId>io.tracee.binding</groupId>
        <artifactId>tracee-springrabbitmq</artifactId>
        <version>${tracee.version}</version>
    </dependency>
...
</dependencies>
```

Then set the message post processors to Spring's `RabbitTemplate`. If you inject your `RabbitTemplate` with dependency injection use this few lines in your Java configuration:

```java
...
@Bean
public RabbitTemplate template() {
	final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
	rabbitTemplate.setAfterReceivePostProcessors(new TraceeAfterReceivePostProcessor());
	rabbitTemplate.setBeforePublishPostProcessors(new TraceeBeforePublishPostProcessor());
	return rabbitTemplate;
}
...
```

To support Spring's `@RabbitHandler` annotation as well you have to configure a `RabbitListener` via Java configuration:
```java
...
@Bean
public SimpleMessageListenerContainer simpleMessageListenerContainer() {
	final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
	container.setConnectionFactory(connectionFactory());
	container.setAfterReceivePostProcessors(new TraceeAfterReceivePostProcessor());
	container.setQueueNames("queue1");
	return container;
}
...
```
**Enter all queues you want attach the listener too. In this example it is just the queue with the name 'queue1'**

