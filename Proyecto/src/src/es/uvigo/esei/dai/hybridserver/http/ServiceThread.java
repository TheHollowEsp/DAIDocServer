package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Connection;

public class ServiceThread implements Runnable {
	private Socket clientSocket;
	HtmlDAO dao;
	Properties props;
	private boolean usaDB = false;
	private String dburl = "jdbc:mysql://localhost:3306/hstestdb";
	private String dbuser = "hsdb";
	private String dbpassword = "hsdbpass";
	private HTTPResponse response;
	private static final String version = "HTTP/1.1";
	private Connection connection;
	

	public ServiceThread(Socket clientSocket, HtmlDAO dao){
		this.clientSocket = clientSocket;
		this.dao = dao;
		
	}

	public ServiceThread(Socket clientSocket, Properties props){
		this.clientSocket = clientSocket;
		this.props = props;
		usaDB = true;
	}

	@Override
	public void run() {
		try (Socket socket = this.clientSocket) {

			if (!usaDB) {
				p("Hilo de servicio iniciado. Modo sin DB");
				BufferedReader lector = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
				HTTPRequest request = new HTTPRequest(lector);
				HTMLController controlador = new HTMLController(dao);
				response = controlador.process(request);
				response.print(new OutputStreamWriter(socket.getOutputStream()));
			} else {
				p("Hilo de servicio iniciado. Modo con DB");
				BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				HTTPRequest request = new HTTPRequest(lector);
				
				dburl = this.props.getProperty("db.url");
				dbuser = this.props.getProperty("db.user");
				dbpassword = this.props.getProperty("db.password");
				try {
					connection = (Connection) DriverManager.getConnection(dburl, dbuser, dbpassword);
				} catch (SQLException e) {
					e("SERVICETHREAD:No se pudo conectar a la DB");
					response = new HTTPResponse();
					response.setVersion(version);
					response.setStatus(HTTPResponseStatus.S500);
					response.setContent("Error conectando a la DB");
					response.print(new OutputStreamWriter(socket.getOutputStream()));
				}
				
				HTMLControllerDB controlador = new HTMLControllerDB(connection,props);				
				response = controlador.processDB(request);
				try {
					connection.close();
				} catch (SQLException e) {
					e("SERVICETHREAD: Error cerrando la conexion DB");
					response = new HTTPResponse();
					response.setVersion(version);
					response.setStatus(HTTPResponseStatus.S500);
					response.setContent("Error cerrando la DB");
					response.print(new OutputStreamWriter(socket.getOutputStream()));
				}
				response.print(new OutputStreamWriter(socket.getOutputStream()));
			}
			

		} catch (IOException e2) {
			e("IOException en ServiceThread");
			e(e2.getMessage());
		} catch (HTTPParseException e) {
			e("HTTPParseException en ServiceThread");
			e(e.getMessage());
		} 
	}

	public void p(String s) {
		System.out.println(s);
	}

	public void e(String s) {
		System.err.println(s);
	}

}
