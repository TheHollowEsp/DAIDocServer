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

public class HTTPRequestPOSTEncodedTest {
	private String requestText;
	private String encodedRequestText;
	private HTTPRequest request;
	
	@Before
	public void setUp() throws Exception {
		this.requestText = "POST / HTTP/1.1\r\n" +
			"Host: localhost\r\n" +
			"Content-Type: application/x-www-form-urlencoded\r\n" + 
			"Content-Length: 86\r\n\r\n" +
			"message=Hello world!!&mensaje=¡¡Hola mundo!!&mensaxe=Ola mundo!!";
		
		this.encodedRequestText = "POST / HTTP/1.1\r\n" +
			"Host: localhost\r\n" +
			"Content-Type: application/x-www-form-urlencoded\r\n" + 
			"Content-Length: 86\r\n\r\n" +
			"message=Hello+world%21%21&mensaje=%C2%A1%C2%A1Hola+mundo%21%21&mensaxe=Ola+mundo%21%21";
		
		this.request = new HTTPRequest(new StringReader(this.encodedRequestText));
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
		parameters.put("mensaje", "¡¡Hola mundo!!");
		parameters.put("mensaxe", "Ola mundo!!");
		
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
		parameters.put("Content-Type", "application/x-www-form-urlencoded");
		parameters.put("Content-Length", "86");
		
		assertEquals(parameters, this.request.getHeaderParameters());
	}

	@Test
	public final void testGetContent() {
		assertEquals("message=Hello world!!&mensaje=¡¡Hola mundo!!&mensaxe=Ola mundo!!", this.request.getContent());
	}

	@Test
	public final void testGetContentLength() {
		assertEquals(86, this.request.getContentLength());
	}

	@Test
	public final void testToString() {
		assertEquals(this.requestText, this.request.toString());
	}

}
