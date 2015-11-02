package es.uvigo.esei.dai.hybridserver.http;

import java.sql.SQLException;
import java.util.Map;

public interface HtmlDAO {

	public abstract boolean exists(String uuid);

	public abstract String get(String uuid) throws Exception;

	public abstract String create(String content) throws SQLException;

	public abstract boolean delete(String uuid);

	public Map<String, String> getMapa();

	public boolean update(String uuid, String content);

}