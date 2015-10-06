package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1></body></html>";
	public ExecutorService threadPool;
	public HybridServer() {
		// Constructor necesario para los tests de la primera semana
		 threadPool = Executors.newFixedThreadPool(50);


	}

	public HybridServer(Map<String, String> pages) {
		// Constructor necesario para los tests de la segunda semana
	}

	public HybridServer(Properties properties) {
		// Constructor necesario para los tests de la tercera semana
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.threadPool.execute(new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					
					while (true) {
						try (Socket socket = serverSocket.accept()) {
							if (stop)
								break;

							// Responder al cliente
							HTTPResponse resp = new HTTPResponse();
							String version = "HTTP/1.1";
							HTTPResponseStatus status = HTTPResponseStatus.S200;
							resp.setStatus(status);							
							resp.setContent(WEB_PAGE);
							resp.setVersion(version);
							
							OutputStream output = socket.getOutputStream();
							
							output.write(resp.toString().getBytes());
							
							output.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		this.stop = false;
		

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
