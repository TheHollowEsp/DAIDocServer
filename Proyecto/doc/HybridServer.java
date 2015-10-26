package ej1;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	public static final String WEB_PAGE = "<html><body><h1>Hola mundo</h1></body></html>";
	
	public static void main(String[] args){
		HybridServer HY = new HybridServer();
		HY.start();
	}
	
	public int getPort() {
		return SERVICE_PORT;
	}
	
	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					while (true) {
						try (Socket socket = serverSocket.accept()) {
							if (stop) break;
							
							// Responder al cliente
							int leng1 = WEB_PAGE.length();
							OutputStream output = socket.getOutputStream();
							output.write("HTTP/1.1 200 OK\r\n".getBytes());
							output.write("Content-Type: text/html\r\n".getBytes());
							output.write(("Content-Length: " + leng1 + "\r\n").getBytes());
							output.write("\r\n".getBytes());
							output.write(WEB_PAGE.getBytes());
							output.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}
	
	public void stop() {
		this.stop = true;
		
		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
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
