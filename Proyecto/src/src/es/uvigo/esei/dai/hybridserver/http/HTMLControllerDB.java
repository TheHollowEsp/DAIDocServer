package es.uvigo.esei.dai.hybridserver.http;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class HTMLControllerDB {
	private HTTPResponse resp;
	private static final String version = "HTTP/1.1";
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1><p>Pagina principal de Hector y Manu.</p></body></html>";
	String extension = "";
	String content = "";
	private Properties props;

	private boolean pruebas = false;

	public HTMLControllerDB(Properties props) {
		this.props = props;
	}

	public HTTPResponse processDB(HTTPRequest request) {
		DBDAO db = new DBDAO(props);
		if (pruebas) { // Comprobar funcionamiento base
			resp = new HTTPResponse();
			resp.setStatus(HTTPResponseStatus.S200);
			resp.setVersion(version);
			content = "<h1>HybridServer con DB funcionando</h1>";
			content += "Propiedades:\n" + props.toString();
			resp.setContent(content);
		} else {
			if (request.getMethod() == HTTPRequestMethod.GET) { // GET
				if (request.getResourceChain().equals("/")) { // Bienvenida
					resp = new HTTPResponse();
					HTTPResponseStatus status = HTTPResponseStatus.S200;
					resp.setStatus(status);
					resp.setVersion(version);
					resp.setContent(WEB_PAGE);

				} else if (request.getResourceChain().equals("/html")) {// Indice
					resp = new HTTPResponse();
					HTTPResponseStatus status = HTTPResponseStatus.S200;
					resp.setStatus(status);
					resp.setVersion(version);
					content = "<html><body>";
					content += "<h1>&Iacutendice</h1>";
					String form = "<form action=\"http://localhost/html\" method=\"POST\"><textarea name=\"html\" required></textarea><button type=\"submit\">Postear</button></form>";
					content += form;

					List<String> listid = db.listarUUID();

					// TODO: Obtener contenido de la DB (si lo hay) y ponerlo en
					// content

					if (!listid.isEmpty()) {
						for (String currentKey : listid) {
							content += "<li>";
							content += "<a href=\"http://127.0.0.1:" + props.get("port") + "/html?uuid=" + currentKey
									+ "\">" + currentKey + "</a>";
							content += "</li>";
						}
					} else content+= "<p> No tenemos paginas</p>";

					content += "</body></html>";
					resp.setContent(content);

				} else if (request.getResourceChain().contains("/html?uuid=")) {
					// Peticion uuid (127.0.0.1:puerto/html)
					String uuid = request.getResourceParameters().get("uuid");
					boolean cond = db.exists(uuid);
					if (cond) { 
						// uuid existe -> Mostrar
						try {
							content = db.get(uuid);
						} catch (Exception e) {
							e("Error obteniendo pagina de DB");
							e(e.getMessage());
						}
						resp = new HTTPResponse();
						resp.setContent(content);
						resp.setStatus(HTTPResponseStatus.S200);
						resp.setVersion(version);
					} else {
						// uuid no existe -> error 404
						resp = new HTTPResponse();
						e("Error 404");
						resp.setContent("Error 404 - No existe esa pagina");
						resp.setStatus(HTTPResponseStatus.S404);
						resp.setVersion(version);
					}
				} else {
					resp = new HTTPResponse();
					e("Error 400");
					resp.setContent("Error 400 - Petición mal formada");
					resp.setStatus(HTTPResponseStatus.S400);
					resp.setVersion(version);
				}
			} else if (request.getMethod() == HTTPRequestMethod.POST) {// POST
				// Peticion sin campo "html" -> error 400
				// Peticion con campo "html"

				if (!request.getTodo().contains("xxx") && (request.getResourceParameters().get("html") != null)) {
					// Si no hay porno y
					if (request.getResourceParameters().get("uuid") != null) {
						// Peticion con uuid -> Actualizar
						p("Actualizando post");
						String uuid = request.getResourceParameters().get("uuid");
						String contenido = request.getResourceParameters().get("html");
						// TODO: Meter contenido y uuid con update
						HTTPResponse resp = new HTTPResponse();
						HTTPResponseStatus status = HTTPResponseStatus.S200;
						resp.setStatus(status);
						String content = "<html><body><h1>Pagina añadida:</h1><a href=\"http://127.0.0.1/html?uuid="
								+ uuid.toString() + "\"></body></html>";
						resp.setContent(content);
						resp.setVersion(version);
					} else {
						// Peticion sin uuid -> Insertar
						p("Metiendo post");
						
						String contenido = request.getResourceParameters().get("html");
						// TODO: Meter contenido y uuid con insert
						String uuid = null;
						try {
							uuid = db.create(contenido);
						} catch (SQLException e) {
							e("Error metiendo post");
							e(e.getMessage());
						}
						p("Metido post");
						resp = new HTTPResponse();
						HTTPResponseStatus status = HTTPResponseStatus.S200;
						resp.setStatus(status);
						String content = "<html><body><h1>Pagina insertada:</h1><a href=\"html?uuid=" + uuid + "\">" + uuid + "</a></body></html>";
						
						resp.setContent(content);
						resp.setVersion(version);
						return resp;
					}

				} else {
					HTTPResponse resp = new HTTPResponse();
					resp.setStatus(HTTPResponseStatus.S400);
					resp.setVersion(version);
					return resp;
				}

			} else if (request.getMethod() == HTTPRequestMethod.DELETE) {// DELETE
				if (request.getResourceChain().contains("/html?uuid=")){
					String[] key = request.getResourceChain().split("=");
					if(db.exists(key[1])){
						boolean estado = db.delete(key[1]);
						resp.setStatus(HTTPResponseStatus.S200);
						resp.setVersion(version);
						resp.setContent("<html><body><h1>Petición DELETE aceptada</h1><p>Borrado de pagina " + key[1]
								+ " con estado " + estado + "</p></body></html>");
						return resp;
					} else {
						HTTPResponse resp = new HTTPResponse();
						resp.setContent("Error 404 - uuid no encontrada");
						resp.setStatus(HTTPResponseStatus.S404);
						resp.setVersion(version);
						return resp;
					}
				// Peticion con uuid
				// uuid existe -> Borrar
				// uuid no existe -> error 404
				} else {
					HTTPResponse resp = new HTTPResponse();
					resp.setContent("Error 400");
					resp.setStatus(HTTPResponseStatus.S400);
					resp.setVersion(version);
					return resp;
				}
			} else { // Peticiones malformadas
				resp = new HTTPResponse();
				resp.setContent("Error 400");
				resp.setStatus(HTTPResponseStatus.S400);
				resp.setVersion(version);
			}
		}
		return resp;
	}

	public void p(String s) {
		System.out.println(s);
	}

	public void e(String s) {
		System.err.println(s);
	}

}
