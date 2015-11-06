package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;



public class Launcher {
	public static void main(String[] args) {
		HybridServer HY = null;
		/* Borrar antes de entrega */
		Map<String, String> pages = new LinkedHashMap<String, String>();
		pages.put("1234", "<html><h1> Hello 1234 </h1></html>");
		pages.put("1235", "<html><h1> Hello 1235 </h1></html>");
		

		if (args.length > 0) { // Con Properties
			Properties properties = new Properties();
			try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
				properties.load(br);
			} catch (IOException e) {
				System.err.println("Error recogiendo propiedades");
			} finally {
				if(properties.isEmpty()){
					properties.setProperty("numClients","50");
					properties.setProperty("port","8888");
					properties.setProperty("db.url","jdbc:mysql://localhost:3306/hstestdb");
					properties.setProperty("db.user","hsdb");
					properties.setProperty("db.password","hsdbpass");
				}
			}
			HY = new HybridServer(properties);
			System.out.println("HybridServer iniciado con DB");

		} else if (!pages.isEmpty()) { // Con mapa
			HY = new HybridServer(pages);
			System.out.println("HybridServer iniciado con map");
		} else { // Basico
			HY = new HybridServer();
			System.out.println("HybridServer iniciado");
		}

		HY.start();
	}
}
