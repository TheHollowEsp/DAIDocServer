package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.esei.dai.hybridserver.http.ServiceThread;

public class HybridServer {
	private static int SERVICE_PORT = 80;

	private Thread serverThread;
	private boolean stop;
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1></body></html>";
	public ExecutorService threadPool;
	public static int numClientes = 50;
	private short modo = 0;
	private Map<String, String> pages;
	private Properties properties;
	String uuid = null;

	public HybridServer() {
		// Constructor necesario para los tests de la primera semana

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
		modo = 3;
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				System.out.println("Hilo servidor iniciado");
				try (final ServerSocket serverSocket = new ServerSocket(getPort())) {
					ExecutorService threadPool = Executors.newFixedThreadPool(numClientes);

					while (true) {
						Socket clientSocket = serverSocket.accept();

						System.out.println("Peticion aceptada");
						if (stop)
							break;
						switch (modo) {
						case 2:
							threadPool.execute(new ServiceThread(pages, clientSocket));
							break;
						case 3:
							threadPool.execute(new ServiceThread(properties, clientSocket));
							break;
						default:
							threadPool.execute(new ServiceThread(clientSocket));
							break;
						}

					}

				} catch (IOException e) {
					System.err.println("IOException en HybridServer.java");
				}
			}
		};
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
