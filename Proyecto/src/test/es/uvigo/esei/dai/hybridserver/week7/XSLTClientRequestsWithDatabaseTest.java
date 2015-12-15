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
package es.uvigo.esei.dai.hybridserver.week7;

import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.extractUUIDFromText;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.getContent;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.postContent;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.postStatus;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.readToString;
import static es.uvigo.esei.dai.hybridserver.utils.matchers.TableMatcher.hasTable;
import static java.util.Collections.singletonMap;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class XSLTClientRequestsWithDatabaseTest
extends AbstractClientRequestWithDatabaseTest {
	@Before
	public void initAttributes() {
		this.invalidUUID = "12345678-abcd-1234-ab12-9876543210ab";
		
		// Estas páginas se insertan en la base de datos al inicio del test.
		this.pages = new String[][] {
		//  { "uuid",                                 "texto contenido por la página"                             "xsd" }
			{ "f260dfee-636c-11e4-bbdd-685b35c84fb4", readToString(getClass().getResourceAsStream("sample1.xslt")), "e5b64c34-636c-11e4-b729-685b35c84fb4"},
			{ "1fd26c94-6909-11e4-9a75-685b35c84fb4", readToString(getClass().getResourceAsStream("sample2.xslt")), "05b88faa-6909-11e4-aadc-685b35c84fb4"}
		};
	}
	
	@Override
	protected String getTableName() {
		return "XSLT";
	}

	@Override
	protected String getResourceName() {
		return "xslt";
	}

	@Override
	protected String getContentType() {
		return "application/xml";
	}
	
	@Test
	@Override
	public void testPost() throws Exception {
		final String content = "<xml><name>Testing POST</name></xml>";
		
		// Envío de la página y extracción del uuid de la nueva página
		final Map<String, String> contentMap = new HashMap<>();
		contentMap.put(getResourceName(), content);
		contentMap.put("xsd", pages[0][2]);
		
		final String responseContent = postContent(url + getResourceName(), contentMap);
		final String uuid = extractUUIDFromText(responseContent);
		assertNotNull(uuid);
		
		// Verificación de que la página de respuesta contiene un enlace a la nueva página
		final String uuidHyperlink = "<a href=\"" + getResourceName() + "?uuid=" + uuid + "\">" + uuid + "</a>";
		assertThat(responseContent, containsString(uuidHyperlink));
		
		// Recuperación de la nueva página
		final String url = this.url + getResourceName() + "?uuid=" + uuid;
		assertEquals("The new page couldn't be retrieved", content, getContent(url));
		
		// Comprobación de la inserción en la base de datos
		assertThat(getConnection(), hasTable(getTableName())
			.withColumn("uuid").withValue(uuid)
		);
	}
	
	@Test
	public void testPostWithoutXSD() throws Exception {
		final String content = "<xml><name>Testing POST</name></xml>";
		
		// Envío de la página y comprobación del código de respuesta
		assertEquals(400, postStatus(url + getResourceName(), singletonMap(getResourceName(), content)));
	}
	
	@Test
	public void testPostWithBadXSD() throws Exception {
		final String content = "<xml><name>Testing POST</name></xml>";
		
		// Envío de la página y comprobación del código de respuesta
		final Map<String, String> contentMap = new HashMap<>();
		contentMap.put(getResourceName(), content);
		contentMap.put("xsd", invalidUUID);
		
		assertEquals(404, postStatus(url + getResourceName(), contentMap));
	}
}
