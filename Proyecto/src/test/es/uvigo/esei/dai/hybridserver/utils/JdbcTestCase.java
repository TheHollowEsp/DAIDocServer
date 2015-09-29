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
package es.uvigo.esei.dai.hybridserver.utils;

import java.sql.Connection;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.junit.After;
import org.junit.Before;

public abstract class JdbcTestCase {
	private IDatabaseTester tester;
	
	@Before
	public void setUpJdbc() throws Exception {
		this.tester = this.createDatabaseTester();
		
		this.tester.getConnection().getConfig().setProperty(
			DatabaseConfig.PROPERTY_DATATYPE_FACTORY, 
			new MySqlDataTypeFactory()
		);
		
		this.tester.setDataSet(getDataSet());
		
		this.tester.onSetup();
	}

	@After
	public void tearDownJdbc() throws Exception {
		this.tester.onTearDown();
	}

	protected JdbcDatabaseTester createDatabaseTester()
	throws ClassNotFoundException {
		Class.forName(this.getDriverClass());
		
		return new JdbcDatabaseTester(
			getDriverClass(), getConnectionUrl(),
			getUsername(), getPassword()
		);
	}
	
	protected String getConnectionUrl() {
		// Esta base de datos debe existir con las tablas creadas 
		// y el usuario debe tener acceso.
		return "jdbc:mysql://localhost/hstestdb";
//		return "jdbc:mysql:/localhost/hstestdb;create=true";
	}
	
	protected String getUsername() {
		return "hsdb";
	}
	
	protected String getPassword() {
		return "hsdbpass";
	}
	
	protected String getDriverClass() {
		return "com.mysql.jdbc.Driver";
//		return "org.apache.derby.jdbc.EmbeddedDriver";
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSetBuilder()
			.setMetaDataSetFromDtd(getClass().getResourceAsStream("dataset.dtd"))
			.setCaseSensitiveTableNames(false)
			.setColumnSensing(true)
		.build(getClass().getResourceAsStream("dataset.xml"));
	}
	
	protected Connection getConnection() throws Exception {
		return this.tester.getConnection().getConnection();
	}
}
