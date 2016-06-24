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

import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.getContentWithType;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.getStatus;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.readToString;
import static es.uvigo.esei.dai.hybridserver.utils.matchers.EqualsToIgnoringSpacesMatcher.equalsToIgnoringSpaces;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.HybridServer;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.utils.JdbcTestCase;

public class ConvertedXMLTest 
extends JdbcTestCase {
	protected HybridServer server;
	protected String url;
	protected String invalidXSLT;
	protected String[][] pages;
	
	@Before
	public void startServer() throws Exception {
		final Configuration configuration = new Configuration(
			8888, 50, null,
			getUsername(), getPassword(), getConnectionUrl(),
			new ArrayList<ServerConfiguration>()
		);
		this.server = new HybridServer(configuration);
		this.url = String.format("http://localhost:%d/", this.server.getPort());
		this.invalidXSLT = "12345678-abcd-1234-ab12-9876543210ab";
		
		// Estas páginas se insertan en la base de datos al inicio del test.
		this.pages = new String[][] {
		//  { "uuid",
		//    "xslt",
		//    "xslt with non valid xsd",
		//    "texto xml",
		//    "texto xml convertido",
		//	}
			{ "ddcab7d0-636c-11e4-8db3-685b35c84fb4", 
			  "f260dfee-636c-11e4-bbdd-685b35c84fb4",
			  "1fd26c94-6909-11e4-9a75-685b35c84fb4",
			  readToString(getClass().getResourceAsStream("sample1.xml")), 
			  readToString(getClass().getResourceAsStream("sample1.html")) 
			 },
			{ "ea118888-6908-11e4-9620-685b35c84fb4",
			  "1fd26c94-6909-11e4-9a75-685b35c84fb4",
			  "f260dfee-636c-11e4-bbdd-685b35c84fb4",
			  readToString(getClass().getResourceAsStream("sample2.xml")), 
			  readToString(getClass().getResourceAsStream("sample2.html")) 
			},
		};
		
		this.server.start();
	}

	@After
	public void stopServer() {
		this.server.stop();
	}

	@Test(timeout = 1000)
	public void testSimpleGet() throws IOException {
		final String pageURL = url + "/xml";
		
		for (String[] page : pages) {
			final String uuid = page[0];
			final String uuidPageURL = pageURL + "?uuid=" + uuid;
			
			final String content = getContentWithType(uuidPageURL, "application/xml");
	
			assertThat(content, is(equalsToIgnoringSpaces(page[3])));
		}
	}

	@Test(timeout = 30000)
	public void testConvertedGet() throws IOException {
		final String pageURL = url + "/xml";
		
		for (String[] page : pages) {
			final String uuid = page[0];
			final String xslt = page[1];
			final String uuidPageURL = pageURL + "?uuid=" + uuid + "&xslt=" + xslt;
			
			final String content = getContentWithType(uuidPageURL, "text/html");

			assertThat(content, is(equalsToIgnoringSpaces(page[4])));
		}
	}
	
	@Test(timeout = 1000)
	public void testInexistantXSLT() throws IOException {
		final String pageURL = url + "/xml";
		
		for (String[] page : pages) {
			final String uuid = page[0];
			final String uuidPageURL = pageURL + "?uuid=" + uuid + "&xslt=" + invalidXSLT;
			
			assertEquals(404, getStatus(uuidPageURL));
		}
	}
	
	@Test(timeout = 1000)
	public void testInvalidXSLT() throws IOException {
		final String pageURL = url + "/xml";
		
		for (String[] page : pages) {
			final String uuid = page[0];
			final String xslt = page[2]; // El XML no es válido para el XSD
			final String uuidPageURL = pageURL + "?uuid=" + uuid + "&xslt=" + xslt;
			
			assertEquals(400, getStatus(uuidPageURL));
		}
	}
}