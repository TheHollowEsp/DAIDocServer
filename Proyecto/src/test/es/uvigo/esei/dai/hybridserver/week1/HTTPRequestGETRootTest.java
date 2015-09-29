package es.uvigo.esei.dai.hybridserver.week1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;

public class HTTPRequestGETRootTest {
	private String requestText;
	private HTTPRequest request;
	
	@Before
	public void setUp() throws Exception {
		this.requestText = "GET / HTTP/1.1\r\n" +
			"Host: localhost\r\n";
		
		this.request = new HTTPRequest(new StringReader(
			this.requestText + "\r\n"
		));
	}

	@Test
	public final void testGetMethod() {
		assertEquals(HTTPRequestMethod.GET, this.request.getMethod());
	}

	@Test
	public final void testGetResourceChain() {
		assertEquals("/", this.request.getResourceChain());
	}

	@Test
	public final void testGetResourcePath() {
		final String[] path = new String[0];
		
		assertArrayEquals(path, this.request.getResourcePath());
	}

	@Test
	public final void testGetResourceName() {
		assertEquals("", this.request.getResourceName());
	}

	@Test
	public final void testGetResourceParameters() {
		assertEquals(Collections.emptyMap(), this.request.getResourceParameters());
	}

	@Test
	public final void testGetHttpVersion() {
		assertEquals(HTTPHeaders.HTTP_1_1.getHeader(), this.request.getHttpVersion());
	}

	@Test
	public final void testGetHeaderParameters() {
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("Host", "localhost");
		
		assertEquals(parameters, this.request.getHeaderParameters());
	}

	@Test
	public final void testGetContent() {
		assertEquals(null, this.request.getContent());
	}

	@Test
	public final void testGetContentLength() {
		assertEquals(0, this.request.getContentLength());
	}

	@Test
	public final void testToString() {
		assertEquals(this.requestText, this.request.toString());
	}

}
