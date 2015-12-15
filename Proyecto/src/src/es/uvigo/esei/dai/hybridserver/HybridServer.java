package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mysql.jdbc.Connection;

import es.uvigo.esei.dai.hybridserver.http.BaseDAO;
import es.uvigo.esei.dai.hybridserver.http.MemoryDAO;
import es.uvigo.esei.dai.hybridserver.http.ServiceThread;

public class HybridServer {

	// Basico
	private static int SERVICE_PORT = 80;
	public static int numClientes = 50;
	private Thread serverThread;
	private boolean stop;
	public ExecutorService threadPool;
	// Mapa
	private BaseDAO dao;
	// DB
	private Properties properties;

	private Connection connection;
	private boolean usaDB = false;
	@SuppressWarnings("unused")
	private Configuration configuration;

	public HybridServer() {
		// Constructor necesario para los tests de la primera semana
	}

	public HybridServer(Map<String, String> pages) {
		// Constructor necesario para los tests de la segunda semana
		dao = new MemoryDAO(pages);
	}

	public HybridServer(Properties properties) {
		// Constructor necesario para los tests de la tercera semana
		this.properties = properties;
		SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
		numClientes = Integer.parseInt(properties.getProperty("numClients"));
		usaDB = true;

	}

	public HybridServer(Configuration configuration) {
		// Constructor necesario para los tests de Configuration
		this.configuration = configuration;
		usaDB = true;
		if (validarConf(configuration)) {
			SERVICE_PORT = configuration.getHttpPort();
			numClientes = configuration.getNumClients();
			this.properties = new Properties();
			properties.setProperty("port", Integer.toString(configuration.getHttpPort()));
			properties.setProperty("numClients", Integer.toString(configuration.getNumClients()));
			properties.setProperty("db.url", configuration.getDbURL());
			properties.setProperty("db.user", configuration.getDbUser());
			properties.setProperty("db.password", configuration.getDbPassword());

		} else {
			System.err.println("El fichero de configuracion no parsea");
		}

	}

	private boolean validarConf(Configuration configuration) {
		// TODO: Validar realmente el fichero
		return true;
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void close() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			System.err.println("SQLEx en Hybridserver.java");
			System.err.println(e.getMessage());
		}
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				System.out.println("Hilo servidor iniciado");
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					ExecutorService threadPool = Executors.newFixedThreadPool(numClientes);
					System.out.println("Pool inicializada");
					while (true) {
						Socket socket = serverSocket.accept();
						if (!usaDB) {
							if (stop)
								break;
							threadPool.execute(new ServiceThread(socket, dao));
						} else {
							if (stop)
								break;
							threadPool.execute(new ServiceThread(socket, properties));
						}
					}
				} catch (IOException e) {
					System.err.println("IOException en switch HybridServer.java");
					System.err.println(e.getMessage());
				}
			}
		};
		this.stop = false;
		this.serverThread.start();

	}

	/****** CODIGO DEFAULT *****/

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

	/****** FIN CODIGO DEFAULT *****/

}
