package es.uvigo.esei.dai.hybridserver.http;

import java.util.Properties;

public class HTMLControllerDB {
	private HTTPResponse response;
	String extension = "";
	String contenido = "";
	private Properties propiedades;

	public HTMLControllerDB(Properties p) {
		propiedades = p;
	}
	
	public HTTPResponse processDB(HTTPRequest request){
		return response;
		
	}
}
