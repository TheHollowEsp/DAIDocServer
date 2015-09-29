package es.uvigo.esei.dai.hybridserver.week1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;

public class HTTPRequestPOSTOneParameterTest {
	private String requestText;
	private HTTPRequest request;
	
	@Before
	public void setUp() throws Exception {
		this.requestText = "POST / HTTP/1.1\r\n" +
			"Host: localhost\r\n" +
			"Content-Length: 21\r\n\r\n" +
			"message=Hello world!!";
		
		this.request = new HTTPRequest(new StringReader(this.requestText));
	}

	@Test
	public final void testGetMethod() {
		assertEquals(HTTPRequestMethod.POST, this.request.getMethod());
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
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("message", "Hello world!!");
		
		assertEquals(parameters, this.request.getResourceParameters());
	}

	@Test
	public final void testGetHttpVersion() {
		assertEquals(HTTPHeaders.HTTP_1_1.getHeader(), this.request.getHttpVersion());
	}

	@Test
	public final void testGetHeaderParameters() {
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("Host", "localhost");
		parameters.put("Content-Length", "21");
		
		assertEquals(parameters, this.request.getHeaderParameters());
	}

	@Test
	public final void testGetContent() {
		assertEquals("message=Hello world!!", this.request.getContent());
	}

	@Test
	public final void testGetContentLength() {
		assertEquals(21, this.request.getContentLength());
	}

	@Test
	public final void testToString() {
		assertEquals(this.requestText, this.request.toString());
	}

}
