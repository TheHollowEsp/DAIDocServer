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
package es.uvigo.esei.dai.hybridserver.week9;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.XMLConfigurationLoader;

public class XMLConfigurationLoaderTest {
	private XMLConfigurationLoader xmlConfiguration;

	@Before
	public void setUp() throws Exception {
		this.xmlConfiguration = new XMLConfigurationLoader();
	}

	@Test
	public final void testLoad()
	throws Exception {
		final Configuration configuration = 
			xmlConfiguration.load(new File("test/configuration.xml"));
		
		assertEquals(10000, configuration.getHttpPort());
		assertEquals(15, configuration.getNumClients());
		assertEquals("http://localhost:20000/hybridserver", configuration.getWebServiceURL());
		
		assertEquals("hsdb", configuration.getDbUser());
		assertEquals("hsdbpass", configuration.getDbPassword());
		assertEquals("jdbc:mysql://localhost:3306/hsdb", configuration.getDbURL());
		
		assertEquals(3, configuration.getServers().size());
		ServerConfiguration server = configuration.getServers().get(0);
		assertEquals("Server 2", server.getName());
		assertEquals("http://localhost:20001/hs?wsdl", server.getWsdl());
		assertEquals("http://hybridserver.dai.esei.uvigo.es/", server.getNamespace());
		assertEquals("ControllerService", server.getService());
		assertEquals("http://localhost:10001/", server.getHttpAddress());
		
		server = configuration.getServers().get(1);
		assertEquals("Server 3", server.getName());
		assertEquals("http://localhost:20002/hs?wsdl", server.getWsdl());
		assertEquals("http://hybridserver.dai.esei.uvigo.es/", server.getNamespace());
		assertEquals("ControllerService", server.getService());
		assertEquals("http://localhost:10002/", server.getHttpAddress());
		
		server = configuration.getServers().get(2);
		assertEquals("Server 4", server.getName());
		assertEquals("http://localhost:20003/hs?wsdl", server.getWsdl());
		assertEquals("http://hybridserver.dai.esei.uvigo.es/", server.getNamespace());
		assertEquals("ControllerService", server.getService());
		assertEquals("http://localhost:10003/", server.getHttpAddress());
	}
}
