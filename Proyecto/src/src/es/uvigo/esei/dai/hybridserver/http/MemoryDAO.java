package es.uvigo.esei.dai.hybridserver.http;

import java.util.Map;
import java.util.UUID;

public class MemoryDAO implements BaseDAO {

	Map<String, String> mapa;
	String uuid;

	public MemoryDAO(Map<String, String> mapa) {
		this.mapa = mapa;
	}

	public Map<String, String> getMapa() {
		return this.mapa;
	}

	@Override
	public boolean exists(String uuid) {
		return this.mapa.containsKey(uuid);
	}

	@Override
	public String get(String uuid) {
		String aux = "";
		if (this.mapa.containsKey(uuid)) {
			return this.mapa.get(uuid);
		}
		return aux;
	}

	@Override
	public String create(String content) {
		UUID key = UUID.randomUUID();
		while (this.mapa.containsKey(key)) {
			key = UUID.randomUUID();
		}
		content = content.replaceFirst("html", "");
		this.mapa.put(key.toString(), content);

		return key.toString();
	}

	@Override
	public boolean delete(String uuid) {
		boolean estado = true;
		if (this.mapa.containsKey(uuid)) {
			this.mapa.remove(uuid);
			estado = true;

		} else {
			estado = false;
		}

		return estado;
	}

	@Override
	public boolean update(String uuid, String content) {
		if (this.mapa.containsKey(uuid)) {
			this.mapa.put(uuid, content);
			return true;
		} else {
			return false;
		}
	}
}
