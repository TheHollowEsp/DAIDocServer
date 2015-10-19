package es.uvigo.esei.dai.hybridserver;

import java.util.HashMap;
import java.util.Map;

public class Launcher {
	public static void main(String[] args) {
		// Basico
		//HybridServer HY = new HybridServer();
		// Con mapa de paginas
		Map<String, String> pages = new HashMap<String,String>();
		pages.put("1234", "<html><h1> Hello 1234 </h1></html>");
		pages.put("1235", "<html><h1> Hello 1235 </h1></html>");
		HybridServer HY = new HybridServer(pages);
		System.out.println("iniciado");
		HY.start();
	}
}
