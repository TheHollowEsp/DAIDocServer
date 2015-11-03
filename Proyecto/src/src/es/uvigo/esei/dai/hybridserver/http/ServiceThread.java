package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;

public class ServiceThread implements Runnable {
	private Socket clientSocket;
	HtmlDAO dao;
	Properties pr;
	private boolean usaDB = false;
	

	public ServiceThread(Socket clientSocket, HtmlDAO dao){
		this.clientSocket = clientSocket;
		this.dao = dao;
		
	}

	public ServiceThread(Socket clientSocket, Properties pr){
		this.clientSocket = clientSocket;
		this.pr = pr;
		usaDB = true;
	}

	@Override
	public void run() {
		try (Socket socket = this.clientSocket) {

			p("Hilo de servicio iniciado.");
			if (!usaDB) {
				p("Modo sin DB");
				BufferedReader lector = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
				HTTPRequest request = new HTTPRequest(lector);
				HTMLController controlador = new HTMLController(dao);
				HTTPResponse response = controlador.process(request);
				response.print(new OutputStreamWriter(socket.getOutputStream()));
			} else {
				p("Modo con DB");
				BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				HTTPRequest request = new HTTPRequest(lector);
				HTMLControllerDB controlador = new HTMLControllerDB(pr);
				HTTPResponse response = controlador.processDB(request);
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
