package io.tracee.binding.httpcomponents;

import io.tracee.Tracee;
import io.tracee.TraceeBackend;
import io.tracee.TraceeConstants;
import io.tracee.configuration.TraceeFilterConfiguration;
import io.tracee.configuration.TraceeFilterConfiguration.Profile;
import io.tracee.transport.HttpHeaderTransport;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.Map;

import static io.tracee.configuration.TraceeFilterConfiguration.Channel.OutgoingRequest;

public class TraceeHttpRequestInterceptor implements HttpRequestInterceptor {

	private final TraceeBackend backend;
	private final HttpHeaderTransport transportSerialization;
	private final String profile;

	public TraceeHttpRequestInterceptor() {
		this(Profile.DEFAULT);
	}

	public TraceeHttpRequestInterceptor(String profile) {
		this(Tracee.getBackend(), profile);
	}

	TraceeHttpRequestInterceptor(TraceeBackend backend, String profile) {
		this.backend = backend;
		this.transportSerialization = new HttpHeaderTransport();
		this.profile = profile;
	}

	@Override
	public final void process(final HttpRequest httpRequest, final HttpContext httpContext) {
		final TraceeFilterConfiguration filterConfiguration = backend.getConfiguration(profile);
		if (!backend.isEmpty() && filterConfiguration.shouldProcessContext(OutgoingRequest)) {
			final Map<String, String> filteredParams = filterConfiguration.filterDeniedParams(backend.copyToMap(), OutgoingRequest);
			httpRequest.setHeader(TraceeConstants.TPIC_HEADER, transportSerialization.render(filteredParams));
		}
	}
}
