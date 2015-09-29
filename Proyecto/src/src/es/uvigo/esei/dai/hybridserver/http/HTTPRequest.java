package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class HTTPRequest {
	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
	}

	public HTTPRequestMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourceChain() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getResourceParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHttpVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getHeaderParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
