package es.uvigo.esei.dai.hybridserver.http;

import java.util.Map;
import java.util.UUID;

public class HTMLController {
	private HtmlDAO dao;
	private HTTPResponse resp;
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1><p>Pagina principal de Hector y Manu.</p></body></html>";
	private static final String version = "HTTP/1.1";
	String extension = "";
	String contenido = "";
	private String uuid;

	public HTMLController(HtmlDAO dao) {
		this.dao = dao;
		
	}

	public HTTPResponse process(HTTPRequest request) {
		resp = new HTTPResponse();
		extension = request.getResourceName();
		resp.setVersion(request.getHttpVersion());
		resp.setStatus(HTTPResponseStatus.S200);
		// response.setContent(request.getContent());

		if (request.getMethod() == HTTPRequestMethod.GET) {
			// Peticion vacia -> Presentacion y enlace a html
			if (request.getResourceChain().equals("/")) {
				HTTPResponse resp = new HTTPResponse();
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				resp.setVersion(version);
				resp.setContent(WEB_PAGE);
				return resp;
				// Peticion con "html"
			} else if (request.getResourceChain().equals("/html")) {
				// Peticion sin UUID -> Listado y formulario
				p("Sirviendo indice");
				HTTPResponse resp = new HTTPResponse();
				resp.setVersion("HTTP/1.1");
				resp.setStatus(HTTPResponseStatus.S200);
				String content = "<html><body>";
				content += "<h1>&Iacutendice</h1>";
				String form = "<form action=\"http://localhost/html\" method=\"POST\"><textarea name=\"html\" required></textarea><button type=\"submit\">Postear</button></form>";
				content += form;
				Map<String, String> pages = null;
				try{
				pages = dao.getMapa();
				} catch (NullPointerException e){
					e("Mapa vacio");
					
					content += "<p>No hay paginas en este momento</p>";
					content += "</body></html>";
					resp.setContent(content);
					return resp;
				}	
				content += "<ul>";
				// Mostramos todos los uuid posibles como link
				for (String currentKey : pages.keySet()) {
					content += "<li>";
					content += "<a href=\"http://127.0.0.1/html?uuid=" + currentKey + "\">" + currentKey + "</a>";
					content += "</li>";
				}
				content += "</ul>";
				
				content += "</body></html>";
				resp.setContent(content);
				
				
				return resp;
				
			} else if (request.getResourceChain().contains("/html?uuid=")) {
				// Peticion con uuid
				System.out.println("Tiene UUID seteado");
				uuid = request.getResourceParameters().get("uuid");
				Map<String, String> pages = dao.getMapa();
				// UUID existe -> Pagina
				if (pages.containsKey(uuid)) {
					p("UUID encontrado");
					HTTPResponse resp = new HTTPResponse();
					HTTPResponseStatus status = HTTPResponseStatus.S200;
					resp.setStatus(status);
					resp.setContent(pages.get(uuid));
					resp.setVersion(version);
					return resp;
					// UUID no existe -> 404
				} else {
					e("Error 404");
					HTTPResponse resp = new HTTPResponse();
					HTTPResponseStatus status = HTTPResponseStatus.S404;
					resp.setStatus(status);
					resp.setVersion(version);
					resp.setContent("<html><body><h1>Error 404</h1></body></html>");
					return resp;
				}

			} else {
				HTTPResponse pagina = new HTTPResponse();
				e("Error 400");
				pagina.setContent("Error 400 - Petición mal formada");
				pagina.setStatus(HTTPResponseStatus.S400);
				pagina.setVersion(version);
				return pagina;
			}

			// Peticion no vacia mal formada -> 400
		} else if (request.getMethod() == HTTPRequestMethod.POST) {
			if (!request.getTodo().contains("xxx")) { // Si hay contenido inapropiado
			p("Metiendo post");
			UUID uuid = UUID.randomUUID();
			Map<String, String> pages = dao.getMapa();
			pages.put(uuid.toString(), request.getResourceParameters().get("html"));
			HTTPResponse resp = new HTTPResponse();
			HTTPResponseStatus status = HTTPResponseStatus.S200;
			resp.setStatus(status);
			String content = "<html><body><h1>Pagina añadida:</h1><a href=\"http://127.0.0.1/html?uuid="
					+ uuid.toString() + "\"></body></html>";
			resp.setContent(content);
			resp.setVersion(version);
			return resp;
			}else{
			HTTPResponse resp = new HTTPResponse();
			resp.setStatus(HTTPResponseStatus.S400);
			resp.setVersion(version);
			return resp;
			}

		} else if (request.getMethod() == HTTPRequestMethod.DELETE) {
			String[] key = request.getResourceChain().split("=");
			HTTPResponse resp = new HTTPResponse();
			resp.setVersion(version);
			if (!dao.exists(key[1])) {
				resp.setStatus(HTTPResponseStatus.S404);
				resp.setContent(
						"<html><body><h1>Error 404:</h1><p>No existe la página que quieres borrar.</p></body></html>");
				return resp;
			} else if (dao.exists(key[1])) {
				boolean estado = dao.delete(key[1]);
				resp.setStatus(HTTPResponseStatus.S200);
				resp.setContent("<html><body><h1>Petición DELETE aceptada</h1><p>Borrado de pagina " + key[1]
						+ " con estado " + estado + "</p></body></html>");
				return resp;
			} else {
				resp.setContent("No se pudo borrar"); // TODO: Formatear html
				resp.setStatus(HTTPResponseStatus.S200);
				if (dao.exists(key[1])) {
					resp.setContent("The page already exists");
					resp.setStatus(HTTPResponseStatus.S404);
					return resp;
				}
				return resp;
			}
		} else {
			HTTPResponse resp = new HTTPResponse();
			resp.setContent("Error 400");
			resp.setStatus(HTTPResponseStatus.S400);
			resp.setVersion(version);
			return resp;
		}

	}

	public void p(String s) {
		System.out.println(s);
	}

	public void e(String s) {
		System.err.println(s);
	}
}
