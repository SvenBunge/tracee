package io.tracee.binding.jaxws;

import io.tracee.TraceeBackend;
import io.tracee.TraceeConstants;
import org.junit.Test;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class AbstractTraceeHandlerTest {

	private final TraceeBackend traceeBackend = mock(TraceeBackend.class);

	private TestTraceeHandler unit = spy(new TestTraceeHandler(traceeBackend));

	@Test
	public void shouldHandleOutgoingContextInOutgoingMethod() {
		final SOAPMessageContext messageContext = mock(SOAPMessageContext.class);
		when(messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.TRUE);
		unit.handleMessage(messageContext);

		verify(unit).handleOutgoing(messageContext);
	}

	@Test
	public void shouldHandleIncomingContextInIncomingMethod() {
		final SOAPMessageContext messageContext = mock(SOAPMessageContext.class);
		unit.handleMessage(messageContext);
		verify(unit).handleIncoming(messageContext);
	}

	@Test
	public void shouldHandleNoContextPropertyInIncomingMethod() {
		final SOAPMessageContext messageContext = mock(SOAPMessageContext.class);
		when(messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.FALSE);
		unit.handleMessage(messageContext);

		verify(unit).handleIncoming(messageContext);
	}

	@Test
	public void closeDoesNothing() {
		final MessageContext context = mock(MessageContext.class);
		unit.close(context);
		verifyZeroInteractions(context, traceeBackend);
	}

	@Test
	public void shouldRegisterForTraceeHeaders() {
		assertThat(unit.getHeaders(), hasItem(TraceeConstants.SOAP_HEADER_QNAME));
	}

	@Test
	public void shouldReturnTraceeBackendFromInitializiation() {
		assertThat(unit.traceeBackend, is(traceeBackend));
	}


	private static class TestTraceeHandler extends AbstractTraceeHandler {

		TestTraceeHandler(TraceeBackend traceeBackend) {
			super(traceeBackend);
		}

		@Override
		protected void handleIncoming(SOAPMessageContext context) {

		}

		@Override
		protected void handleOutgoing(SOAPMessageContext context) {

		}

		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return false;
		}
	}
}
