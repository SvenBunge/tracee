package io.tracee.binding.springrabbitmq;

import com.rabbitmq.client.LongString;
import io.tracee.TraceeBackend;
import io.tracee.TraceeConstants;
import io.tracee.configuration.TraceeFilterConfiguration;
import io.tracee.testhelper.FieldAccessUtil;
import io.tracee.testhelper.SimpleTraceeBackend;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static io.tracee.TraceeConstants.INVOCATION_ID_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertTrue;

public class TraceeAfterReceivePostProcessorTest {
	private static final String USED_PROFILE = "A_PROFILE";
	private static final String CHARSET_UTF8 = "UTF-8";
	private final TraceeBackend backend = SimpleTraceeBackend.createNonLoggingAllPermittingBackend();

	private final TraceeAfterReceivePostProcessor unit = new TraceeAfterReceivePostProcessor(backend, USED_PROFILE);

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void parseMessageHeaderIntoBackend() throws Exception {
		final HashMap<String, String> tpic = new HashMap<>();
		tpic.put(INVOCATION_ID_KEY, "Crazy ID");

		MessageProperties properties = new MessageProperties();
		properties.setHeader(TraceeConstants.TPIC_HEADER, tpic);
		unit.postProcessMessage(new Message("my Body".getBytes(CHARSET_UTF8), properties));

		assertThat(backend.copyToMap(), hasEntry(INVOCATION_ID_KEY, "Crazy ID"));
	}

	@Test
	public void messageWithoutMessagePropertiesShouldNotCreateNPE() throws Exception {
		unit.postProcessMessage(new Message("ohh my body".getBytes(CHARSET_UTF8), null));
		assertTrue(true); // Dummyassert - no exception is expected!
	}

	@Test
	public void noTraceeHeaderAndNoCorrelationIdShouldResultInGeneratedRequestId() throws Exception {
		MessageProperties properties = new MessageProperties();
		properties.setHeader(TraceeConstants.TPIC_HEADER, new HashMap<String, String>());
		unit.postProcessMessage(new Message("my Body".getBytes(CHARSET_UTF8), properties));

		assertThat(backend.copyToMap(), hasKey(INVOCATION_ID_KEY));
		assertThat(backend.size(), is(1));
	}

	@Test
	public void noTraceeHeaderButCorrelationIdOfRabbitMqInHeaderShouldUseRabbitMqCorrelation() throws Exception {
		final String correlationIdString = "ourRabbitMQ correlationId";

		MessageProperties properties = new MessageProperties();
		properties.setCorrelationIdString(correlationIdString);
		properties.setHeader(TraceeConstants.TPIC_HEADER, new HashMap<String, String>());
		unit.postProcessMessage(new Message("my Body".getBytes(CHARSET_UTF8), properties));

		assertThat(backend.copyToMap().get(INVOCATION_ID_KEY), is(correlationIdString));
	}

	@Test
	public void defaultConstructorUsesDefaultProfile() {
		final TraceeAfterReceivePostProcessor converter = new TraceeAfterReceivePostProcessor();
		MatcherAssert.assertThat((String) FieldAccessUtil.getFieldVal(converter, "profile"), is(TraceeFilterConfiguration.Profile.DEFAULT));
	}

	@Test
	public void constructorStoresProfileNameInternal() {
		final TraceeAfterReceivePostProcessor converter = new TraceeAfterReceivePostProcessor("testProf");
		MatcherAssert.assertThat((String) FieldAccessUtil.getFieldVal(converter, "profile"), is("testProf"));
	}

	@Test
	public void shouldConvertEmptyMap() {
		final Map<String, String> convertedMap = unit.transformToTraceeContextMap(new HashMap<String, Object>());
		assertThat(convertedMap, not(nullValue()));
	}

	@Test
	public void shouldConvertNullIntoEmptyMap() {
		final Map<String, String> convertedMap = unit.transformToTraceeContextMap(null);
		assertThat(convertedMap, not(nullValue()));
	}


	// Because LongStringHelper.ByteArrayLongString is private ..
	private class TestLongString implements LongString {

		private final String value;

		public TestLongString(String value) {
			this.value = value;
		}

		@Override
		public long length() {
			return value.length();
		}

		@Override
		public DataInputStream getStream() throws IOException {
			return new DataInputStream(new ByteArrayInputStream(getBytes()));
		}

		@Override
		public byte[] getBytes() {
			try {
				return value.getBytes(CHARSET_UTF8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
