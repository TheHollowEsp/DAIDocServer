package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mysql.jdbc.Connection;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridServer {
	private static int SERVICE_PORT = 80;
	private static final String version = "HTTP/1.1";
	private Thread serverThread;
	private boolean stop;
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

	public HybridServer() {
		// Constructor necesario para los tests de la primera semana
		modo = 1;

	}

	public HybridServer(Map<String, String> pages) {
		// Constructor necesario para los tests de la segunda semana
		this.pages = pages;
		modo = 2;
	}

	public HybridServer(Properties properties) {
		// Constructor necesario para los tests de la tercera semana
		this.properties = properties;
		SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
		numClientes = Integer.parseInt(properties.getProperty("numClients"));
		dburl = properties.getProperty("db.url");
		dbuser = properties.getProperty("db.user");
		dbpassword = properties.getProperty("db.password");		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection(dburl,
					dbuser, dbpassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		modo = 3;
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		System.out.println("Definiendo start");
		this.serverThread = new Thread() {
			@Override
			public void run() {
				System.out.println("Hilo servidor iniciado");
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					// ExecutorService threadPool =
					// Executors.newFixedThreadPool(numClientes);
					while (true) {
						try (Socket socket = serverSocket.accept()) {
							System.out.println("Peticion aceptada");
							if (stop)
								break;
							// TODO: Hacer un nuevo hilo de servicio para meter
							// la logica
							/* Meter en un hilo desde aqui */
							if (modo == 1) {
								// Responder al cliente
								HTTPResponse resp = new HTTPResponse();

								HTTPResponseStatus status = HTTPResponseStatus.S200;
								resp.setStatus(status);
								resp.setContent(WEB_PAGE);
								resp.setVersion(version);

								OutputStream output = socket.getOutputStream();

								output.write(resp.toString().getBytes());

								output.flush();
							} else if (modo == 2) { // Si hay un map
								p("Modo 2 iniciado");
								// Recoger peticion
								BufferedReader lector = new BufferedReader(
										new InputStreamReader(socket.getInputStream()));

								// Sacar uuid (procesar en un HTTPRequest)
								try {
									HTTPRequest req = new HTTPRequest(lector);
									parametros = req.getResourceParameters();
									recurso = req.getResourceName();
								} catch (HTTPParseException e) {
									e("HTTP no parseado");
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
											content += "<a href=\"http://127.0.0.1/html?uuid=" + currentKey + "\">"
													+ currentKey + "</a>";
											content += "</li>";
										}
										content += "</ul>\n";
										content += "</body></html>";
										OutputStream output = socket.getOutputStream();
										HTTPResponse resp = new HTTPResponse();
										HTTPResponseStatus status = HTTPResponseStatus.S200;
										resp.setStatus(status);
										resp.setContent(content);
										p("Contenido:");
										p(content);
										resp.setVersion(version);
										output.write(resp.toString().getBytes());
										output.flush();
										p("Pagina servida. Bye.");
									} else {
										System.out.println("Tiene parametros");
										uuid = parametros.get("uuid");
										System.out.println(parametros.toString());
										// Buscar en map el uuid
										if (pages.containsKey(uuid)) { // Existe
											// Mostramos el contenido
											p("UUID encontrado");
											OutputStream output = socket.getOutputStream();
											HTTPResponse resp = new HTTPResponse();
											HTTPResponseStatus status = HTTPResponseStatus.S200;
											resp.setStatus(status);
											resp.setContent(pages.get(uuid));
											resp.setVersion(version);
											output.write(resp.toString().getBytes());
											output.flush();
											
										} else { // Si no existe, mostrar error
											// A tomar por el orto parte 2
											// 404 Not Found
										}
									}
								} else {
									e("Metodo no seleccionado. HTML.");
									// A tomar por el orto parte 3
									// 400 Bad Request
								}
							} else if (modo == 3) {
								p("Modo 3 iniciado");
							}
							p("Pagina servida. Bye.");
							/* Meter en un hilo hasta aqui */
						}
					}
				} catch (IOException e) {
					e("IOException en HybridServer.java");
				}
			}
		};
		p("Terminado definicion start");
		this.stop = false;
		this.serverThread.start();

	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo
			// servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
	}

	public void p(String s) {
		System.out.println(s);
	}

	public void e(String s) {
		System.err.println(s);
	}

}
