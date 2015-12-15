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

import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.readToString;

import org.junit.Before;

public class XMLClientRequestsWithDatabaseTest
extends AbstractClientRequestWithDatabaseTest {
	@Before
	public void initAttributes() {
		this.invalidUUID = "12345678-abcd-1234-ab12-9876543210ab";
		
		// Estas páginas se insertan en la base de datos al inicio del test.
		this.pages = new String[][] {
		//  { "uuid",                                 "texto contenido por la página" }
			{ "ddcab7d0-636c-11e4-8db3-685b35c84fb4", readToString(getClass().getResourceAsStream("sample1.xml")) },
			{ "ea118888-6908-11e4-9620-685b35c84fb4", readToString(getClass().getResourceAsStream("sample2.xml")) },
		};
	}

	@Override
	protected String getTableName() {
		return "XML";
	}

	@Override
	protected String getResourceName() {
		return "xml";
	}

	@Override
	protected String getContentType() {
		return "application/xml";
	}
}
