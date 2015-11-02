package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mysql.jdbc.Connection;

import es.uvigo.esei.dai.hybridserver.http.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.http.MemoryDAO;
import es.uvigo.esei.dai.hybridserver.http.ServiceThread;

public class HybridServer {
	private static int SERVICE_PORT = 80;

	private Thread serverThread;
	private boolean stop;
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1></body></html>";
	public ExecutorService threadPool;
	public static int numClientes = 50;
	private Properties properties;
	private String dburl = "jdbc:mysql://localhost/hybridserverdb";
	private String dbuser = "dai";
	private String dbpassword = "daipassword";
	private HtmlDAO dao;
	private Connection connection;
	private boolean esDB = false;
	String uuid = null;

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
		dburl = properties.getProperty("db.url");
		dbuser = properties.getProperty("db.user");
		dbpassword = properties.getProperty("db.password");

		esDB = true; // TODO: Cambiar nombre
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection(dburl, dbuser, dbpassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
						if (!esDB) {
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
