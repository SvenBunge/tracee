package io.tracee.binding.springrabbitmq;

import java.util.HashMap;
import java.util.Map;

class TraceeSpringRabbitUtils {

	static Map<String, String> transformToTraceeContextMap(final Map<String, ?> tpicMessageHeader) {
		final Map<String, String> traceeContext = new HashMap<>();
		if (tpicMessageHeader != null) {
			for (Map.Entry<String, ?> stringObjectEntry : tpicMessageHeader.entrySet()) {
				traceeContext.put(stringObjectEntry.getKey(), String.valueOf(stringObjectEntry.getValue()));
			}
		}
		return traceeContext;
	}

	private TraceeSpringRabbitUtils() {

	}
}
