/*
HTTPResponse: Clase que contiene la información de una respuesta HTTP. Deberá disponer de un método "print(Writer)" que escriba la respuesta en formato HTTP correcto. La información que contendrá será la siguiente:
    1 Estado: Código de estado del servidor. Más información aquí.
    2 Versión de HTTP (normalmente, HTTP/1.1)
    3 Contenido. Será una cadena de texto con el cuerpo de la respuesta.
    4 Parámetros de cabecera. La mejor forma de almacenar estos valores es utilizar un Map<String, String>.
*/
package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	
	private HTTPResponse response;
	private HTTPResponseStatus status; 					//1 Estado
	private String version;								//2 Versión
	private String content;								//3 Contenido
	private LinkedHashMap<String, String> parameters;	//4 Parámetros de cabecera
	
	public HTTPResponse() {
		parameters = new LinkedHashMap<String, String>();
	}

	public HTTPResponseStatus getStatus() {
		return status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public String putParameter(String name, String value) {
		parameters.put(name,value);
		return name;
	}

	public boolean containsParameter(String name) {			// Si LA PETICION contiene el parametro
		if (response.listParameters().contains(name)) {
			return true;
		} else
			return false;
	}

	public String removeParameter(String name) {
		String aux = "";
		if (response.listParameters().contains(name)) { // Si existe
			aux = response.getParameters().get(name);	
			response.getParameters().remove(name);		// Lo borramos
		} else {
			System.out.println("no se ha encontrado ningun parametro con esa clave");
		}
		return aux;
	}

	public void clearParameters() {
		parameters.clear();
	}

	public List<String> listParameters() {
		List<String> listaParametros = new LinkedList<String>();
		for (String clave : parameters.keySet()) {
			listaParametros.add(parameters.get(clave));
		}
		return listaParametros;
	}

	public void print(Writer writer) throws IOException { 		// Printea la respuesta
		PrintWriter salida = new PrintWriter(writer);
		salida.print(version + " " + status.getCode() + " " + status.getStatus()); 	// VERSION CODE STATUS

		for (final String header : parameters.keySet()) { 							// Parametros
			salida.write(header + ": " + parameters.get(header));
			salida.println();
		}

		if (content != null) {														// Con contenido
			salida.println();
			salida.println("Content-Length: " + content.length());
			salida.println();
			salida.write(content.toCharArray());
		} else {																	// Sin contenido
			salida.println();
			salida.println();
		}

		salida.flush();
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
