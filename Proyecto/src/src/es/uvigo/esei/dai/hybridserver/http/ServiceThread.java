package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import com.mysql.jdbc.Connection;

public class ServiceThread implements Runnable {

	private static final String version = "HTTP/1.1";

	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1></body></html>";
	public ExecutorService threadPool;
	public static int numClientes = 50;
	private short modo = 0;
	private Map<String, String> pages;
	private Map<String, String> parametros;
	private String recurso = null;
	private Properties properties;
	String uuid = null;
	private String dbpassword;
	private String dbuser;
	private String dburl;
	private Connection connection;

	private Socket socket;

	private HTTPRequestMethod metodo;

	public ServiceThread(Socket socket) {
		this.socket = socket;
	}

	public ServiceThread(Map<String, String> pages, Socket socket) {
		this.socket = socket;
		this.pages = pages;
		modo = 2;
	}

	public ServiceThread(Properties properties, Socket socket) {
		this.socket = socket;
		this.properties = properties;
		dburl = properties.getProperty("db.url");
		dbuser = properties.getProperty("db.user");
		dbpassword = properties.getProperty("db.password");
		try {
			// No necesario
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection(dburl, dbuser, dbpassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		modo = 3;

	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {

			p("Hilo de servicio iniciado.");
			if (modo == 0) {
				// Responder al cliente
				HTTPResponse resp = new HTTPResponse();

				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				resp.setContent(WEB_PAGE);
				resp.setVersion(version);

				try (OutputStream output = socket.getOutputStream()) {
					resp.print(new OutputStreamWriter(output));
				} catch (IOException e) {
					e("IOException en hilo de servicio 1");
					e.printStackTrace();
				}
			} else if (modo == 2) { // Si hay un map
				p("Modo 2");
				// Recoger peticion
				BufferedReader lector = null;
				try {
					lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e1) {
					e("IOException en modo Map");
				}

				// Sacar uuid (procesar en un HTTPRequest)
				HTTPRequest req = null;
				try {
					req = new HTTPRequest(lector);
					parametros = req.getResourceParameters();
					recurso = req.getResourceName();
					metodo = req.getMethod();
				} catch (HTTPParseException e) {
					e("HTTP no parseado");
				} catch (IOException e) {
					e("IOException en modo Map (2)");
				}
				if (recurso.compareTo("html") == 0) {
					
					if (parametros.isEmpty()) {
						p("No tiene parametros, sirviendo indice");
						String content = "<html><body>\n";
						content += "<h1>&Iacutendice</h1>\n";
						content += "<ul>\n";
						
						// Mostramos todos los uuid posibles
						// como link
						for (String currentKey : pages.keySet()) {
							content += "<li>";
							content += "<a href=\"http://127.0.0.1/html?uuid=" + currentKey + "\">" + currentKey
									+ "</a>";
							content += "</li>";
						}
						content += "</ul>\n";
						String form = "<form action=\"http://localhost/html\" method=\"POST\"><textarea name=\"html\" required></textarea><button type=\"submit\">Postear</button></form>";
						content += form;
						content += "</body></html>";

						try (OutputStream output = socket.getOutputStream()) {
							HTTPResponse resp = new HTTPResponse();
							HTTPResponseStatus status = HTTPResponseStatus.S200;
							resp.setStatus(status);
							resp.setContent(content);
							p("Contenido:");
							p(content);
							resp.setVersion(version);
							resp.print(new OutputStreamWriter(output));
							p("Pagina servida. Bye.");
						} catch (IOException e) {
							e("IOException en Map (3)");
						}

					} else if (metodo.compareTo(HTTPRequestMethod.POST) == 0){
						p("Metiendo post");
						UUID uuid = UUID.randomUUID();
						pages.put(uuid.toString(), req.getResourceParameters().get("html"));
						try (OutputStream output = socket.getOutputStream()) {
							HTTPResponse resp = new HTTPResponse();
							HTTPResponseStatus status = HTTPResponseStatus.S200;
							resp.setStatus(status);
							String content = "<html><body><a href=\"http://127.0.0.1/html?uuid=" + uuid.toString() + "\"></body></html>";
							resp.setContent(content);
							resp.setVersion(version);
							resp.print(new OutputStreamWriter(output));
						} catch (IOException e) {
							e("IOException en Map (4)");
						}
					}
					else {
						System.out.println("Tiene parametros");
						uuid = parametros.get("uuid");
						System.out.println(parametros.toString());
						// Buscar en map el uuid
						if (pages.containsKey(uuid)) { // Existe
							// Mostramos el contenido
							p("UUID encontrado");

							try (OutputStream output = socket.getOutputStream()) {
								HTTPResponse resp = new HTTPResponse();
								HTTPResponseStatus status = HTTPResponseStatus.S200;
								resp.setStatus(status);
								resp.setContent(pages.get(uuid));
								resp.setVersion(version);
								resp.print(new OutputStreamWriter(output));
							} catch (IOException e) {
								e("IOException en Map (4)");
							}

						} else { // Si no existe, mostrar error
							e("Error 404");
							HTTPResponse resp = new HTTPResponse();
							OutputStream output = socket.getOutputStream();
							HTTPResponseStatus status = HTTPResponseStatus.S404;
							resp.setStatus(status);
							resp.setVersion(version);
							resp.setContent("<html><body><h1>Error 404</h1></body></html>");
							resp.print(new OutputStreamWriter(output));
						}
					}
				} else {
					e("Metodo no seleccionado. HTML.");
					HTTPResponse resp = new HTTPResponse();
					OutputStream output = socket.getOutputStream();
					HTTPResponseStatus status = HTTPResponseStatus.S400;
					resp.setStatus(status);
					resp.setVersion(version);
					resp.setContent("<html><body><h1>Error 400</h1></body></html>");
					resp.print(new OutputStreamWriter(output));
				}
			} else if (modo == 3) {
				p("Modo 3");
			}
			p("Pagina servida. Bye.");

		} catch (IOException e2) {

			e2.printStackTrace();
		}
	}

	public void p(String s) {
		System.out.println(s);
	}

	public void e(String s) {
		System.err.println(s);
	}

}
