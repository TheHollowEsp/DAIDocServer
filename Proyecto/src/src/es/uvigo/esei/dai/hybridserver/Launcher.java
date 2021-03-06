package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

// TODO: Permitir launch con un configuration.xml
// TODO: Validar configuration.xml
// TODO: Hacerlo con DOM o SAX

public class Launcher {
	public static void main(String[] args) {
		HybridServer HY = null;
		/* Borrar antes de entrega */
		Map<String, String> pages = new LinkedHashMap<String, String>();

		if (args.length > 0) { // Con Properties
			if (args[0].contains("xml")) {
				Configuration conf = null;
				XMLConfigurationLoader xmlConfiguration = new XMLConfigurationLoader();
				File file = new File(args[0]);
				try {
					conf = xmlConfiguration.load(file);
				} catch (Exception e) {
					System.err.println("Error recogiendo configuration");
				}
				HY = new HybridServer(conf);
			} else {
				Properties properties = new Properties();
				try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
					properties.load(br);
				} catch (IOException e) {
					System.err.println("Error recogiendo propiedades");
				} finally {
					if (properties.isEmpty()) {
						properties.setProperty("numClients", "50");
						properties.setProperty("port", "8888");
						properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
						properties.setProperty("db.user", "hsdb");
						properties.setProperty("db.password", "hsdbpass");
					}
				}
				HY = new HybridServer(properties);
				System.out.println("HybridServer iniciado con DB");
			}
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
