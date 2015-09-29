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

public class HTTPRequestGETParametersTest {
	private String requestText;
	private HTTPRequest request;
	
	@Before
	public void setUp() throws Exception {
		this.requestText = 
			"GET /hello/world.html?country=Spain&province=Ourense&city=Ourense HTTP/1.1\r\n" +
			"Host: localhost\r\n" +
			"Accept: text/html\r\n" +
			"Accept-Encoding: gzip,deflate\r\n";
		
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
		assertEquals("/hello/world.html?country=Spain&province=Ourense&city=Ourense", this.request.getResourceChain());
	}

	@Test
	public final void testGetResourcePath() {
		final String[] path = new String[] { "hello", "world.html" };
		
		assertArrayEquals(path, this.request.getResourcePath());
	}

	@Test
	public final void testGetResourceName() {
		assertEquals("hello/world.html", this.request.getResourceName());
	}

	@Test
	public final void testGetResourceParameters() {
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("country", "Spain");
		parameters.put("province", "Ourense");
		parameters.put("city", "Ourense");
		
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
		parameters.put("Accept", "text/html");
		parameters.put("Accept-Encoding", "gzip,deflate");
		
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
