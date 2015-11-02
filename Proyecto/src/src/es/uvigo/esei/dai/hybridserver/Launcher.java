package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/*
 * TODO: Hilo de servicio para el ExecutorService
 * TODO: Generar errores 400, 404, 500
 * TODO: Poner JDBC a funcionar
 * TODO: Permitir POST (Crear formulario, manejarlo y meter a JDBC o a mapa)
 * TODO: Permitir DELETE (Borrar pagina)
 */

public class Launcher {
	public static void main(String[] args) {
		HybridServer HY = null;
		/* Borrar antes de entrega */
		Map<String, String> pages = new LinkedHashMap<String, String>();
		pages.put("1234", "<html><h1> Hello 1234 </h1></html>");
		pages.put("1235", "<html><h1> Hello 1235 </h1></html>");
		//pages.put("1236", "<html><h1> Hello 1236 </h1></html>");
		//pages.put("1237", "<html><h1> Hello 1237 </h1></html>");
		//pages.put("1238", "<html><h1> Hello 1238 </h1></html>");
		/* END Borrar antes de entrega */

		if (args.length > 0) {	//////////////////////////////////////////////////////////////// Con Properties
			Properties properties = new Properties();
			try (BufferedReader br = new BufferedReader(new FileReader("config.props"))) {
				properties.load(br);
			} catch (IOException e) {
				System.err.println("Error");
			} finally {
				HY = new HybridServer(properties);
				System.out.println("HybridServer iniciado con DB");
			}

		} else if (!pages.isEmpty()) {	//////////////////////////////////////////////////////////// Con mapa de paginas
			HY = new HybridServer(pages);
			System.out.println("HybridServer iniciado con map");
		} else { /////////////////////////////////////////////////////////////////////////////// Basico
			HY = new HybridServer();
			System.out.println("HybridServer iniciado");
		}
		
		HY.start();
	}
}
