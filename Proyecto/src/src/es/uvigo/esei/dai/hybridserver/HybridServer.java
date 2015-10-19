package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridServer {
	private static final int SERVICE_PORT = 80;
	private static final String version = "HTTP/1.1";
	private Thread serverThread;
	private boolean stop;
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1></body></html>";
	public ExecutorService threadPool;
	public static final int numClientes = 50;
	private short modo = 0;
	private Map<String, String> pages;
	private Map<String, String> parametros;
	String uuid = null;

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
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		System.out.println("Iniciado start");
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
								System.out.println("Pagina servida. Bye.");
							} else if (modo == 2) { // Si hay un map inicializado
								System.out.println("Modo 2 iniciado");

								// Recoger peticion
								BufferedReader lector = new BufferedReader(
										new InputStreamReader(socket.getInputStream()));

								// Sacar uuid (procesar en un HTTPRequest)
								try {
									HTTPRequest req = new HTTPRequest(lector);
									parametros = req.getResourceParameters();

								} catch (HTTPParseException e) {
									System.out.println("HTTP no parseado");
								}
								if (parametros.isEmpty()) {
									System.out.println("No tiene parametros");
									String content = "<html><body>";
									// Mostramos todos los uuid posibles como link
									for(String currentKey : pages.keySet()){
										
										content += "<a href=\"127.0.0.1/html?uuid='"+currentKey+"'\"></a>";
									}
									content += "</body></html>";
									OutputStream output = socket.getOutputStream();
									HTTPResponse resp = new HTTPResponse();
									HTTPResponseStatus status = HTTPResponseStatus.S200;
									resp.setStatus(status);
									resp.setContent(content);
									System.out.println(content);
									resp.setVersion(version);
									output.write(resp.toString().getBytes());
									output.flush();
								} else {
									System.out.println("Tiene parametros");
									uuid = parametros.get("uuid");
									System.out.println(parametros.toString());
									// Buscar en map el uuid
									if (pages.containsKey(uuid)) { // Si existe, mostrar contenido
										System.out.println("UUID encontrado");
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
									}

								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		System.out.println("Terminado definicion start");
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

}
