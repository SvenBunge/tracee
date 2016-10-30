package io.tracee.binding.springrabbitmq;

import io.tracee.Tracee;
import io.tracee.TraceeBackend;
import io.tracee.TraceeConstants;
import io.tracee.configuration.TraceeFilterConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;

import java.util.Map;

import static io.tracee.configuration.TraceeFilterConfiguration.Channel.AsyncProcess;

public class TraceeRabbitListener {

	private final TraceeBackend backend;

	private final String profile;

	public TraceeRabbitListener() {
		this(TraceeFilterConfiguration.Profile.DEFAULT);
	}

	public TraceeRabbitListener(String profile) {
		this(Tracee.getBackend(), profile);
	}

	TraceeRabbitListener(TraceeBackend backend, String profile) {
		this.backend = backend;
		this.profile = profile;
	}

	@RabbitListener(id="traceeListenerRead", priority = "99999999")
	public void readTpicHeaderFromMessage(@Header(TraceeConstants.TPIC_HEADER) final Map<String, ?> tpic) {

		if (tpic != null) {
			final TraceeFilterConfiguration filterConfiguration = backend.getConfiguration(profile);
			final Map<String, String> traceeContextMap = TraceeSpringRabbitUtils.transformToTraceeContextMap(tpic);

			if (traceeContextMap != null && !traceeContextMap.isEmpty()) {
				backend.putAll(filterConfiguration.filterDeniedParams(traceeContextMap, AsyncProcess));
			}
		}
	}

	@RabbitListener(id="traceeListenerClean", priority = "-99999999")
	public void cleanTpicAfterMessage() {
		backend.clear();
	}
}
