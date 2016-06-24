package es.uvigo.esei.dai.hybridserver.http;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mysql.jdbc.Connection;

public class DBDAO_XSLT implements BaseDAO {
	protected final String TABLE_NAME = "XSLT";
	protected final String UUID_NAME = "uuid";
	protected final String CONTENT_NAME = "content";
	protected final String SCHEMA_NAME = "xsd";

	private Connection connection;

	public DBDAO_XSLT(Connection connection) throws SQLException {
		this.connection = connection;
	}

	protected String getTableName() {
		return TABLE_NAME;
	}

	protected String getUUIDName() {
		return UUID_NAME;
	}

	protected String getContentName() {
		return CONTENT_NAME;
	}

	public String getSchemaName() {
		return SCHEMA_NAME;
	}

	@Override
	public boolean exists(String uuid) {
		boolean estado = true;
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT WHERE uuid=?")) {
			statement.setString(1, uuid);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					estado = true;
				} else {
					estado = false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return estado;
	}

	public String getXSD(String uuid) throws Exception {
		// HybridServer d = new HybridServer(proper);
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT WHERE uuid=?")) {
			statement.setString(1, uuid);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getString("xsd");
				} else {
					throw new Exception("No existe el elemento solicitado en la base de datos.");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String uuid) throws Exception {
		// HybridServer d = new HybridServer(proper);
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT WHERE uuid=?")) {
			statement.setString(1, uuid);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getString("content");
				} else {
					throw new Exception("No existe el elemento solicitado en la base de datos.");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String create(String content, String xsd) throws SQLException {
		String id;

		try (PreparedStatement statement = connection
				.prepareStatement("INSERT INTO XSLT (uuid,content,xsd) VALUES (?, ?, ?)")) {
			id = UUID.randomUUID().toString();
			statement.setString(1, id);
			statement.setString(2, content);
			statement.setString(3, xsd);
			int result = statement.executeUpdate();
			if (result != 1) {
				throw new SQLException("Error insertando elemento");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return id;
	}

	@Override
	public String create(String content) throws SQLException {
		System.err.println("Intento de creacion de XSLT sin XSD");
		return null;
	}

	// Contemplamos que se pueda hacer un update con xsd nuevo (este metodo)
	public boolean update(String uuid, String content, String xsd) {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO XSLT WHERE uuid=?",
				Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(2, content);
			statement.setString(3, xsd);
			if (statement.executeUpdate() != 1) {
				throw new SQLException("Error actualizando elemento");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	// Contemplamos que se pueda hacer un update sin xsd nuevo (este metodo)
	@Override
	public boolean update(String uuid, String content) {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO XSLT WHERE uuid=?",
				Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(2, content);
			if (statement.executeUpdate() != 1) {
				throw new SQLException("Error actualizando elemento");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean delete(String uuid) {
		boolean estado = true;
		try (PreparedStatement statement = connection.prepareStatement("DELETE FROM XSLT WHERE uuid=?")) {
			try {
				statement.setString(1, uuid);
			} catch (NumberFormatException ex) {
				estado = false;
			}
			if (statement.executeUpdate() != 1) {
				System.out.println("Se ha producido un error");
				estado = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return estado;
	}

	@SuppressWarnings("null")
	@Override
	public Map<String, String> getMapa() {
		Map<String, String> map = null;
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT")) {
			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					map.put(result.getString("uuid"), result.getString("content"));
				}
				return map;
			} catch (SQLException e) {
				throw new SQLException("Error al convertir a mapa");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> listarContenido() {
		try (Statement statement = connection.prepareStatement("SELECT * FROM XSLT")) {
			try (ResultSet result = statement.executeQuery("SELECT * FROM XSLT")) {
				final List<String> contenido = new ArrayList<>();
				while (result.next()) {
					contenido.add(result.getString("content"));
				}
				return contenido;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> listarUUID() {
		try (Statement statement = connection.prepareStatement("SELECT * FROM XSLT")) {
			try (ResultSet result = statement.executeQuery("SELECT * FROM XSLT")) {
				final List<String> contenido = new ArrayList<>();

				while (result.next()) {
					contenido.add(result.getString("uuid"));
				}
				return contenido;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}