package es.uvigo.esei.dai.hybridserver.http;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.HybridServer;


public class DBDAO implements HtmlDAO {
	protected final String TABLE_NAME = "HTML";
	protected final String UUID_NAME = "uuid";
	protected final String CONTENT_NAME = "content";
	private Properties proper;

	public DBDAO(Properties p) {
		proper = p;
	}

	protected String getContentName() {
		return CONTENT_NAME;
	}

	protected String getTableName() {
		return TABLE_NAME;
	}

	protected String getUUIDName() {
		return UUID_NAME;
	}

	@Override
	public boolean exists(String uuid) {
		HybridServer d = new HybridServer(proper);
		boolean estado = true;
		try (PreparedStatement statement = d.getConnection().prepareStatement(
				"SELECT * FROM HTML WHERE uuid=?")) {
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

	@Override
	public String get(String uuid) throws Exception {
		HybridServer d = new HybridServer(proper);
		try (PreparedStatement statement = d.getConnection().prepareStatement(
				"SELECT * FROM HTML WHERE uuid=?")) {
			statement.setString(1, uuid);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getString("content");
				} else {
					throw new Exception(
							"No existe el elemento solicitado en la base de datos.");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String create(String content) throws SQLException {
		String id;
		HybridServer d = new HybridServer(proper);
		try (PreparedStatement statement = d.getConnection().prepareStatement(
				"INSERT INTO HTML (uuid,content) VALUES (?, ?)")) {
			id = UUID.randomUUID().toString();
			statement.setString(1, id);
			statement.setString(2, content);
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
	public boolean update(String uuid, String content) {
		HybridServer d = new HybridServer(proper);
		try (PreparedStatement statement = d.getConnection().prepareStatement(
				"INSERT INTO HTML WHERE uuid=?",
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
		HybridServer d = new HybridServer(proper);
		try (PreparedStatement statement = d.getConnection().prepareStatement(
				"DELETE FROM HTML WHERE uuid=?")) {
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
		HybridServer d = new HybridServer(proper);
		Map<String, String> map = null;
		try (PreparedStatement statement = d.getConnection().prepareStatement(
				"SELECT * FROM HTML")) {
			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					map.put(result.getString("uuid"),
							result.getString("content"));
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
		HybridServer d = new HybridServer(proper);
		try (Statement statement = d.getConnection().prepareStatement(
				"SELECT * FROM HTML")) {
			try (ResultSet result = statement
					.executeQuery("SELECT * FROM HTML")) {
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
		HybridServer d = new HybridServer(proper);
		try (Statement statement = d.getConnection().prepareStatement(
				"SELECT * FROM HTML")) {
			try (ResultSet result = statement
					.executeQuery("SELECT * FROM HTML")) {
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