package io.tracee.binding.springrabbitmq;

import io.tracee.TraceeBackend;
import io.tracee.TraceeConstants;
import io.tracee.Utilities;
import io.tracee.configuration.TraceeFilterConfiguration;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static io.tracee.configuration.TraceeFilterConfiguration.Channel.AsyncProcess;

public class TraceeAfterReceivePostProcessor implements MessagePostProcessor {

	@Autowired
	private TraceeBackend backend;

	private final String profile;

	public TraceeAfterReceivePostProcessor() {
		this(TraceeFilterConfiguration.Profile.DEFAULT);
	}

	public TraceeAfterReceivePostProcessor(String profile) {
		this.profile = profile;
	}

	TraceeAfterReceivePostProcessor(TraceeBackend backend, String profile) {
		this.backend = backend;
		this.profile = profile;
	}

	@Override
	public Message postProcessMessage(final Message message) throws AmqpException {

		if (message.getMessageProperties() != null) {
			final Map<String, ?> rawTpic = (Map<String, ?>) message.getMessageProperties().getHeaders().get(TraceeConstants.TPIC_HEADER);
			final Map<String, String> traceeContextMap = transformToTraceeContextMap(rawTpic);
			if (traceeContextMap != null && !traceeContextMap.isEmpty()) {
				final TraceeFilterConfiguration filterConfiguration = backend.getConfiguration(profile);
				backend.putAll(filterConfiguration.filterDeniedParams(traceeContextMap, AsyncProcess));
			}

			// Use rabbitMQ correlation if set - or generate a new id.
			if (backend.getInvocationId() == null || backend.getInvocationId().isEmpty()) {
				final String rabbitCorrelationId = message.getMessageProperties().getCorrelationIdString();
				if (rabbitCorrelationId != null && !rabbitCorrelationId.isEmpty()) {
					backend.put(TraceeConstants.INVOCATION_ID_KEY, rabbitCorrelationId);
				} else {
					Utilities.generateInvocationIdIfNecessary(backend);
				}
			}
		}

		return message;
	}

	Map<String, String> transformToTraceeContextMap(final Map<String, ?> tpicMessageHeader) {
		final Map<String, String> traceeContext = new HashMap<>();
		if (tpicMessageHeader != null) {
			for (Map.Entry<String, ?> stringObjectEntry : tpicMessageHeader.entrySet()) {
				traceeContext.put(stringObjectEntry.getKey(), String.valueOf(stringObjectEntry.getValue()));
			}
		}
		return traceeContext;
	}
}
