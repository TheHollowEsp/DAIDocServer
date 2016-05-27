/**
 *  HybridServer
 *  Copyright (C) 2014 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class XMLConfigurationLoader {

	public Configuration load(File xmlFile) throws Exception {
		Configuration conf = new Configuration();

		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document doc = db.parse(xmlFile);
		doc.getDocumentElement().normalize();
		
		/* <connections> */
		NodeList con = doc.getElementsByTagName("http"); 		// HTTP
		NodeList con2 = doc.getElementsByTagName("webservice");	// WebService URL
		NodeList con3 = doc.getElementsByTagName("numClients");	// Numero clientes
		Element htp = (Element) con.item(0);
		Element ws = (Element) con2.item(0);
		Element nc = (Element) con3.item(0);
		conf.setHttpPort(Integer.parseInt(htp.getTextContent()));		
		conf.setWebServiceURL(ws.getTextContent());		
		conf.setNumClients(Integer.parseInt(nc.getTextContent()));
		/* </connections> */
		
		/* <database> */
		NodeList con4 = doc.getElementsByTagName("user");
		NodeList con5 = doc.getElementsByTagName("password");
		Element user = (Element) con4.item(0);
		Element password = (Element) con5.item(0);
		conf.setDbUser(user.getTextContent());		
		conf.setDbPassword(password.getTextContent());
		conf.setDbURL(((Element) doc.getElementsByTagName("url").item(0)).getTextContent());
		/* </database> */
		/* <servers> */
		NodeList serversl = doc.getElementsByTagName("server");
		List<ServerConfiguration> listaServidor = new ArrayList<ServerConfiguration>();
		
		for (int i = 0; i < serversl.getLength(); i++) {	// Para cada nodo servidor
			ServerConfiguration servers = new ServerConfiguration();	// Creamos una config
			Element lista = (Element) serversl.item(i);					// Obtenemos los datos
			servers.setHttpAddress(lista.getAttribute("httpAddress"));	// Seteamos
			servers.setName(lista.getAttribute("name"));
			servers.setNamespace(lista.getAttribute("namespace"));
			servers.setService(lista.getAttribute("service"));
			servers.setWsdl(lista.getAttribute("wsdl"));
			listaServidor.add(servers);									// Metemos en la lista
		}
		conf.setServers(listaServidor); // Seteamos la lista de servidores
		/* </servers> */
		
		return conf;

	}
}