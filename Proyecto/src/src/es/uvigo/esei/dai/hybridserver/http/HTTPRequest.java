/*
HTTPRequest: Clase que contiene la información de una petición HTTP, cuyo constructor recibirá un Reader y parseará para recuperar la información de una petición HTTP. La información que contendrá será la siguiente:

  1  Método: GET, POST, PUT, DELETE, OPTIONS, TRACE, CONNECT, HEAD.
  2  Nombre del recurso solicitado (sin los parámetros). Por ejemplo, en una petición para "/index.php?param1=value1", el nombre del recurso será "index.php".
  3  Parámetros GET y POST. En el caso de GET formarán parte del rescurso solicitado y en el caso de POST formarán parte del recurso solicitado o del cuerpo de la petición. La mejor forma de almacenar estos valores es utilizar un Map<String, String>.
  4  Parámetros de la cabecera. Se encuentran después de la primera línea y siguen el formato "Parámetro: Valor".  La cabecera finaliza cuando hay una línea en blanco. La mejor forma de almacenar estos valores es utilizar un Map<String, String>.
  5  Longitud del contenido: En el caso de que haya contenido, la cabecera incluirá el parámetro "Content-length", que indicará la longitud del cuerpo.



	 GET /pub/WWW/TheProject.html HTTP/1.1
       Host: www.w3.org
*/
package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {
	private String httpVersion;	
	public HTTPRequestMethod method;// 1 Metodo	
	private String resourceName;// 2 Nombre del recurso	
	private String resourceChain;// 2.1 Cadena del recurso	
	private String[] resourcePath;// 2.1 Ruta del recurso
	private Map<String, String> ResourceParameters = new LinkedHashMap<String, String>();// 3 Parámetros GET y POST	
	private Map<String, String> HeaderParameters = new LinkedHashMap<String, String>();// 4 Parámetros de la cabecera.	
	private int contentLength;// 5 Longitud del contenido	
	private String content;// 6 Contenido
	private String contentEntero;
	private boolean codificado;
	private String[] x;	
	private String completo;


	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		BufferedReader bread = new BufferedReader(reader);
		String cadena = bread.readLine();
		String[] splitted = cadena.split("\\s+"); // Separamos la cadena por los espacios
		try {
			this.method = HTTPRequestMethod.valueOf(splitted[0]); // Metodo (GET/POST/PUT/DELETE...)
		} catch (Exception e) {
			throw new HTTPParseException("Missing method");
		}
		try {
			resourceChain = splitted[1]; // Metodo (GET/POST/PUT)
		} catch (Exception e1) {
			throw new HTTPParseException("Missing Resource");
		}
		try {
			httpVersion = splitted[2]; // Version HTTP
		} catch (Exception e) {
			throw new HTTPParseException("Missing version");
		}
		if (splitted[1].contains("?")) { // Parametros de la ruta
			// html?uuid=123123&edsd=23
			String[] aux2 = splitted[1].split("\\?"); // Dividimos en 2 por el ?
			resourceName = aux2[0].substring(1);		// Recogemos el recurso
			String[] parametros = aux2[1].split("&");	// Dividimos para recoger los parametros			
			for (int j = 0; j < parametros.length; j++) { // Recoge los parametros uno por uno
				String[] aux3 = parametros[j].split("=");
				ResourceParameters.put(aux3[0], aux3[1]);
			}
		} else {
			resourceName = splitted[1].substring(1);	// Recogemos el recurso
		}
		String linea = bread.readLine();
		while (!linea.matches("")) { // Parametros de la cabecera
			String[] elementos = linea.split(":\\s+"); // Separamos en partes por espacio+
			try {
				HeaderParameters.put(elementos[0], elementos[1]); // Introduce clave y valor
			} catch (Exception e) {
				throw new HTTPParseException("Missing new line after header");
			}
			if (elementos[0].startsWith("Content-Length")) { // Si hay content-length lo guardamos
				contentLength = Integer.parseInt(elementos[1]);
			}
			if (elementos[0].startsWith("Content-Type")) { // Si hay content-type lo guardamos
				codificado = true;
			}
			linea = bread.readLine();
		}

		if (method == HTTPRequestMethod.POST && HeaderParameters.containsKey("Content-Length")) { 		// Si es POST y tiene length
			char[] contentArray = new char[Integer.valueOf(HeaderParameters.get("Content-Length"))]; 	// Array de char de longitud "Content-length"
			bread.read(contentArray);

			content = new String(contentArray);															
			if (codificado == true) {
				content = URLDecoder.decode(content, "UTF-8");
			}			
			if (content.contains("&")) { 						// Si tiene varios parametros
				String[] p = content.split("&"); 				// Separamos por parametro
				for (int j = 0; j < p.length; j++) { 			// Y guardamos cada uno
					String[] aux3 = p[j].split("="); 			// Separando clave y valor
					ResourceParameters.put(aux3[0], aux3[1]);	
				}
			} else {											// Si no tiene mas que uno
				String[] param = content.split("=");			// Guardamos
				ResourceParameters.put(param[0], param[1]);
			}
			contentEntero = content;							
			x = content.split("=");
			content = x[1];
			completo = x[0].concat(x[1]);

		}
	}

	public HTTPRequestMethod getMethod() {
		//System.out.println("Metodo: "+method);
		return method;
	}

	public String getResourceChain() {
		//System.out.println("Cadena de recurso: "+resourceChain);
		return resourceChain;
	}

	public String[] getResourcePath() {
		// TODO Auto-generated method stub
		return resourcePath;
	}

	public String getResourceName() {
		//System.out.println("Nombre de recurso: "+resourceName);
		return resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return ResourceParameters;
	}

	public String getHttpVersion() {
		//System.out.println("Version HTTP: "+httpVersion);
		return httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return HeaderParameters;
	}

	public String getContent() {
		System.out.println("Contenido: "+contentEntero);
		return contentEntero;
	}

	public int getContentLength() {
		
		return contentLength;
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
