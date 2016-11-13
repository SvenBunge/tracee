package io.tracee.binding.springrabbitmq;

import io.tracee.TraceeBackend;
import io.tracee.TraceeConstants;
import io.tracee.Utilities;
import io.tracee.configuration.TraceeFilterConfiguration;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.tracee.TraceeConstants.TPIC_HEADER;
import static io.tracee.configuration.TraceeFilterConfiguration.Channel.AsyncDispatch;

public class TraceeBeforePublishPostProcessor implements MessagePostProcessor {

	@Autowired
	private TraceeBackend backend;

	private final String profile;

	public TraceeBeforePublishPostProcessor() {
		this(TraceeFilterConfiguration.Profile.DEFAULT);
	}

	public TraceeBeforePublishPostProcessor(String profile) {
		this.profile = profile;
	}

	TraceeBeforePublishPostProcessor(TraceeBackend backend, String profile) {
		this.backend = backend;
		this.profile = profile;
	}

	@Override
	public Message postProcessMessage(final Message message) throws AmqpException {

		// Write normal TPIC header to message
		final TraceeFilterConfiguration filterConfiguration = backend.getConfiguration(profile);
		if (!backend.isEmpty() && filterConfiguration.shouldProcessContext(AsyncDispatch)) {
			final Map<String, String> filteredParams = filterConfiguration.filterDeniedParams(backend.copyToMap(), AsyncDispatch);
			message.getMessageProperties().getHeaders().put(TPIC_HEADER, filteredParams);

			// Set correlationId for rabbitMQ if not filtered
			if (filteredParams.containsKey(TraceeConstants.INVOCATION_ID_KEY)) {
				message.getMessageProperties().setCorrelationIdString(filteredParams.get(TraceeConstants.INVOCATION_ID_KEY));
			}
		}

		return message;
	}
}
