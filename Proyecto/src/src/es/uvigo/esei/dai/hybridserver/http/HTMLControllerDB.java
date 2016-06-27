package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.mysql.jdbc.Connection;

public class HTMLControllerDB {
	private HTTPResponse resp;
	private static final String version = "HTTP/1.1";
	public static final String WEB_PAGE = "<html><body><h1>Hybrid Server</h1><h2>P&aacutegina principal de Hector y Manu.</h2><p> Opciones disponibles: </p><ul><li><a href='html'>Documentos HTML</a></li><li><a href='xml'>Documentos XML</a></li><li><a href='xsd'>Documentos XSD</a></li><li><a href='xslt'>Documentos XSLT</a></li></ul></body></html>";
	String extension = "";
	String content = "";
	private Connection connection;

	private Properties props;

	public HTMLControllerDB(Connection connection, Properties props) {
		this.connection = connection;
		this.props = props;
	}

	public HTTPResponse processDB(HTTPRequest request) {

		resp = new HTTPResponse();
		resp.setVersion(version);

		if (request.getMethod() == HTTPRequestMethod.GET) { // GET
			if (request.getResourceChain().equals("/")) { // Bienvenida
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				resp.setContent(WEB_PAGE);

			} else if (request.getResourceChain().equals("/html")) {// Indice
				DBDAO_HTML db = null;
				try {
					db = new DBDAO_HTML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				content = "<html><body>";
				content += "<h1>&Iacutendice</h1>";
				String form = "<form action=\"http://localhost/html\" method=\"POST\"><textarea name=\"html\" required></textarea><button type=\"submit\">Postear</button></form>";
				content += form;
				List<String> listid = db.listarUUID();
				if (!listid.isEmpty()) {
					for (String currentKey : listid) {
						content += "<li>";
						content += "<a href=\"http://127.0.0.1:" + props.get("port") + "/html?uuid=" + currentKey
								+ "\">" + currentKey + "</a>";
						content += "</li>";
					}
				} else
					content += "<p> No tenemos paginas</p>";

				content += "</body></html>";
				resp.setContent(content);

			} else if (request.getResourceChain().contains("/html?uuid=")) {
				DBDAO_HTML db = null;
				try {
					db = new DBDAO_HTML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				// Peticion uuid (127.0.0.1:puerto/html)
				String uuid = request.getResourceParameters().get("uuid");
				boolean cond = db.exists(uuid);
				if (cond) {
					// uuid existe -> Mostrar
					try {
						content = db.get(uuid);
					} catch (Exception e) {
						e("Error obteniendo pagina de DB");
						e(e.getMessage());
					}
					resp.setContent(content);
					resp.setContentType("text/html");
					resp.setStatus(HTTPResponseStatus.S200);
				} else {
					// uuid no existe -> error 404
					e("Error 404");
					resp.setContent("Error 404 - No existe esa pagina");
					resp.setStatus(HTTPResponseStatus.S404);
				}
			} else if (request.getResourceChain().equals("/xml")) {
				DBDAO_XML db = null;
				try {
					db = new DBDAO_XML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				content = "<html><body>";
				content += "<h1>&Iacutendice XML</h1>";
				String form = "<form action=\"http://localhost/xml\" method=\"POST\"><textarea name=\"xml\" required></textarea><button type=\"submit\">Postear</button></form>";
				content += form;
				List<String> listid = db.listarUUID();
				if (!listid.isEmpty()) {
					for (String currentKey : listid) {
						content += "<li>";
						content += "<a href=\"http://127.0.0.1:" + props.get("port") + "/xml?uuid=" + currentKey + "\">"
								+ currentKey + "</a>";
						content += "</li>";
					}
				} else
					content += "<p> No tenemos paginas</p>";

				content += "</body></html>";
				resp.setContent(content);
			} else if (request.getResourceChain().contains("/xml?uuid=")) {

				DBDAO_XML db = null;

				// Si se adjunta un uuid XSLT
				if (request.getResourceParameters().get("xslt") != null) {
					DBDAO_XSLT db2 = null;
					DBDAO_XSD db3 = null;
					String uuidXSLT = request.getResourceParameters().get("xslt");
					try {
						db = new DBDAO_XML(connection);
						db2 = new DBDAO_XSLT(connection);
						db3 = new DBDAO_XSD(connection);
					} catch (Exception e1) {
						e("No se pudo conectar a la DB");
						e1.printStackTrace();
						resp.setStatus(HTTPResponseStatus.S500);
						resp.setContent("Error 500 - No se pudo conectar a la DB");
						return resp;
					}
					String uuid = request.getResourceParameters().get("uuid");
					boolean condXML = db.exists(uuid);
					boolean condXSLT = db2.exists(uuidXSLT); // Invalido = 400,
					String uuidXSD = null;
					if (condXML) {
						if (condXSLT) {
							try {
								uuidXSD = db2.getXSD(uuidXSLT);
							} catch (Exception e1) {
								e("Error obteniendo XSD asociado de DB");
								e(e1.getMessage());
							}
							String xsd = null;
							String xml = null;
							try {
								xsd = db3.get(uuidXSD);
								xml = db.get(uuid);
							} catch (Exception e) {
								e("Error obteniendo pagina de DB");
								e(e.getMessage());
							}
							try {
								// DOM parser
								// Obtenemos la factoria de schemas
								// SchemaFactory schemaFactory = SchemaFactory
								// .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
								// Schema schema;
								// // Encapsulamos el Schema
								// schema = schemaFactory.newSchema(new
								// StreamSource(new StringReader(xsd)));
								// // Creamos instancia del DocumentBuilder
								// DocumentBuilder dbuilder =
								// DocumentBuilderFactory.newInstance().newDocumentBuilder();
								// // Default Error Handler. Si da tiempo
								// ampliar
								// dbuilder.setErrorHandler(new
								// DefaultErrorHandler());
								// // Parseamos el xml a documento
								// Document doc = dbuilder.parse(new
								// InputSource(new StringReader(xml)));
								// // Creamos validador en base al XSD
								// Validator validator = schema.newValidator();
								// // Validamos
								// validator.validate(new DOMSource(doc));

								final Source schemaSource = new StreamSource(new StringReader(xsd));
								final Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
										.newSchema(schemaSource);
								final SAXParserFactory factory = SAXParserFactory.newInstance();
						        factory.setNamespaceAware(true);
						        factory.setSchema(schema);
						        final SAXParser parser = factory.newSAXParser();

						        
						        final MyContentHandler handler = new MyContentHandler();

						        
						        parser.parse(new InputSource(new StringReader(xml)), handler);

							} catch (SAXException e) {
								e("SAXException parse");
								e.printStackTrace();
								resp.setContent("Error 400 Bad Request");
								resp.setStatus(HTTPResponseStatus.S400);
								return resp;
							} catch (ParserConfigurationException e) {
								e("ParserConfigurationException parse");
								e.printStackTrace();
							} catch (IOException e) {
								e("IOException parse");
								e.printStackTrace();
							}
							// El parseo ha sido completado con exito
							String XSLT = null;
							String HTML = null;
							try {
								XSLT = db2.get(uuidXSLT);
								TransformerFactory tFactory = TransformerFactory.newInstance();
								Transformer transformer = tFactory
										.newTransformer(new StreamSource(new StringReader(XSLT)));
								StringWriter salida = new StringWriter();

								transformer.transform(new StreamSource(new StringReader(xml)),
										new StreamResult(salida));
								StringBuffer sb = salida.getBuffer();
								HTML = sb.toString();
							} catch (Exception e) {
								e("Excepcion en el transformador");
								e.printStackTrace();
							}
							if (HTML != null) {
								resp.setContent(HTML);
								resp.setContentType("text/html");

							} else {
								resp.setContent(xml);
								resp.setContentType("application/xml");
							}
							resp.setStatus(HTTPResponseStatus.S200);
						} else {
							// uuid no existe -> error 404
							e("Error 404 - XSLT no existente");
							resp.setContent("Error 404 - No existe ese XSLT");
							resp.setStatus(HTTPResponseStatus.S404);
						}
					} else {
						// uuid no existe -> error 404
						e("Error 404");
						resp.setContent("Error 404 - No existe esa pagina");
						resp.setStatus(HTTPResponseStatus.S404);
					}
				} else { // Si va sin XSLT
					try {
						db = new DBDAO_XML(connection);
					} catch (Exception e1) {
						e("No se pudo conectar a la DB");
						e1.printStackTrace();
						resp.setStatus(HTTPResponseStatus.S500);
						resp.setContent("Error 500 - No se pudo conectar a la DB");
						return resp;
					}
					String uuid = request.getResourceParameters().get("uuid");
					boolean cond = db.exists(uuid);
					if (cond) {
						// uuid existe -> Mostrar
						try {
							content = db.get(uuid);
						} catch (Exception e) {
							e("Error obteniendo pagina de DB");
							e(e.getMessage());
						}
						resp.setContent(content);
						resp.setContentType("application/xml");
						resp.setStatus(HTTPResponseStatus.S200);
					} else {
						// uuid no existe -> error 404
						e("Error 404");
						resp.setContent("Error 404 - No existe esa pagina");
						resp.setStatus(HTTPResponseStatus.S404);
					}
				}
			} else if (request.getResourceChain().equals("/xsd")) {
				DBDAO_XSD db = null;
				try {
					db = new DBDAO_XSD(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				content = "<html><body>";
				content += "<h1>&Iacutendice XSD</h1>";
				String form = "<form action=\"http://localhost/xsd\" method=\"POST\"><textarea name=\"xsd\" required></textarea><button type=\"submit\">Postear</button></form>";
				content += form;
				List<String> listid = db.listarUUID();
				if (!listid.isEmpty()) {
					for (String currentKey : listid) {
						content += "<li>";
						content += "<a href=\"http://127.0.0.1:" + props.get("port") + "/xsd?uuid=" + currentKey + "\">"
								+ currentKey + "</a>";
						content += "</li>";
					}
				} else
					content += "<p> No tenemos paginas</p>";

				content += "</body></html>";
				resp.setContent(content);
			} else if (request.getResourceChain().contains("/xsd?uuid=")) {
				DBDAO_XSD db = null;
				try {
					db = new DBDAO_XSD(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				String uuid = request.getResourceParameters().get("uuid");
				boolean cond = db.exists(uuid);
				if (cond) {
					// uuid existe -> Mostrar
					try {
						content = db.get(uuid);
					} catch (Exception e) {
						e("Error obteniendo pagina de DB");
						e(e.getMessage());
					}
					resp.setContent(content);
					resp.setContentType("application/xml");
					resp.setStatus(HTTPResponseStatus.S200);
				} else {
					// uuid no existe -> error 404
					e("Error 404");
					resp.setContent("Error 404 - No existe esa pagina");
					resp.setStatus(HTTPResponseStatus.S404);
				}
			} else if (request.getResourceChain().equals("/xslt")) {
				DBDAO_XSLT db = null;
				try {
					db = new DBDAO_XSLT(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				content = "<html><body>";
				content += "<h1>&Iacutendice XSLT</h1>";
				String form = "<form action=\"http://localhost/xslt\" method=\"POST\"><textarea name=\"xslt\" required></textarea></br><p>XSD relacionado</p><input type=\"text\" name=\"xsd\"></input><button type=\"submit\">Postear</button></form>";
				content += form;
				List<String> listid = db.listarUUID();
				if (!listid.isEmpty()) {
					for (String currentKey : listid) {
						content += "<li>";
						content += "<a href=\"http://127.0.0.1:" + props.get("port") + "/xslt?uuid=" + currentKey
								+ "\">" + currentKey + "</a>";
						content += "</li>";
					}
				} else
					content += "<p> No tenemos paginas</p>";

				content += "</body></html>";
				resp.setContent(content);
			} else if (request.getResourceChain().contains("/xslt?uuid=")) {
				DBDAO_XSLT db = null;
				try {
					db = new DBDAO_XSLT(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				String uuid = request.getResourceParameters().get("uuid");
				boolean cond = db.exists(uuid);
				if (cond) {
					// uuid existe -> Mostrar
					try {
						content = db.get(uuid);
					} catch (Exception e) {
						e("Error obteniendo pagina de DB");
						e(e.getMessage());
					}
					resp.setContent(content);
					resp.setContentType("application/xml");
					resp.setStatus(HTTPResponseStatus.S200);
				} else {
					// uuid no existe -> error 404
					e("Error 404");
					resp.setContent("Error 404 - No existe esa pagina");
					resp.setStatus(HTTPResponseStatus.S404);
				}
			} else {
				e("Error 400 - peticion mal formada");
				resp.setContent("Error 400 - Petición mal formada");
				resp.setStatus(HTTPResponseStatus.S400);
			}
		} else if (request.getMethod() == HTTPRequestMethod.POST) {// POST
			if ((request.getResourceParameters().get("html") != null)) {
				DBDAO_HTML db = null;
				try {
					db = new DBDAO_HTML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}

				if (request.getResourceParameters().get("uuid") != null) {
					// Peticion con uuid -> Actualizar post
				} else {
					// Peticion sin uuid -> Insertar
					p("Metiendo post");

					String contenido = request.getResourceParameters().get("html");
					String uuid = null;
					try {
						uuid = db.create(contenido);
					} catch (SQLException e) {
						e("Error metiendo post");
						e(e.getMessage());
					}
					p("Metido post");
					HTTPResponseStatus status = HTTPResponseStatus.S200;
					resp.setStatus(status);
					String content = "<html><body><h1>Pagina insertada:</h1><a href=\"html?uuid=" + uuid + "\">" + uuid
							+ "</a></body></html>";
					resp.setContent(content);
					return resp;
				}

			} else if (request.getResourceParameters().get("xml") != null) { // POST
																				// de
																				// XML
				DBDAO_XML db = null;
				try {
					db = new DBDAO_XML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				p("Metiendo XML");

				String contenido = request.getResourceParameters().get("xml");
				String uuid = null;
				try {
					uuid = db.create(contenido);
				} catch (SQLException e) {
					e("Error metiendo XML");
					e(e.getMessage());
				}
				p("Metido post");
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				String content = "<html><body><h1>P&aacutegina insertada:</h1><a href=\"xml?uuid=" + uuid + "\">" + uuid
						+ "</a></body></html>";
				resp.setContent(content);
				return resp;

			} else if (request.getResourceName().equals("xsd")) {// POST de XSD
				DBDAO_XSD db = null;
				try {
					db = new DBDAO_XSD(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				p("Metiendo XSD");
				String contenido = request.getResourceParameters().get("xsd");
				String uuid = null;
				try {
					uuid = db.create(contenido);
				} catch (SQLException e) {
					e("Error metiendo XSD");
					e(e.getMessage());
				}
				p("Metido post");
				HTTPResponseStatus status = HTTPResponseStatus.S200;
				resp.setStatus(status);
				String content = "<html><body><h1>P&aacutegina insertada:</h1><a href=\"xsd?uuid=" + uuid + "\">" + uuid
						+ "</a></body></html>";
				resp.setContent(content);
				return resp;

			} else if (request.getResourceName().equals("xslt")) {// POST de
																	// XSLT
				DBDAO_XSLT db = null;
				DBDAO_XSD db2 = null;
				try {
					db = new DBDAO_XSLT(connection);
					db2 = new DBDAO_XSD(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				p("Metiendo XSLT");
				String contenido = request.getResourceParameters().get("xslt");
				String xsdrel = request.getResourceParameters().get("xsd");
				if (xsdrel != null) { // Si hay un XSD escrito
					boolean cond = db2.exists(xsdrel);
					if (cond) { // Si existe el XSD relacionado
						String uuid = null;
						try {
							uuid = db.create(contenido, xsdrel);
						} catch (SQLException e) {
							e("Error metiendo XSLT");
							e(e.getMessage());
						}
						p("Metido post");
						HTTPResponseStatus status = HTTPResponseStatus.S200;
						resp.setStatus(status);
						String content = "<html><body><h1>P&aacutegina insertada:</h1><a href=\"xslt?uuid=" + uuid
								+ "\">" + uuid + "</a></body></html>";
						resp.setContent(content);
						return resp;
					} else { // Si el XSD relacionado no existe 404 Not Found
						resp.setStatus(HTTPResponseStatus.S404);
						resp.setContent("Error 404: El XSD que indicaste no existe.");
						return resp;
					}
				} else { // Si no hay un uuid de XSD 400 Bad Request
					resp.setStatus(HTTPResponseStatus.S400);
					resp.setContent("Error 400: Debes indicar un XSD.");
					return resp;
				}
			} else {
				resp.setStatus(HTTPResponseStatus.S400);
				return resp;
			}

		} else if (request.getMethod() == HTTPRequestMethod.DELETE) {// DELETE
			if (request.getResourceChain().contains("/html?uuid=")) {
				DBDAO_HTML db = null;
				try {
					db = new DBDAO_HTML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				String[] key = request.getResourceChain().split("=");
				if (db.exists(key[1])) {
					boolean estado = db.delete(key[1]);
					resp.setStatus(HTTPResponseStatus.S200);
					resp.setContent("<html><body><h1>Petición DELETE aceptada</h1><p>Borrado de pagina " + key[1]
							+ " con estado " + estado + "</p></body></html>");
					return resp;
				} else {
					resp.setContent("Error 404 - uuid no encontrada");
					resp.setStatus(HTTPResponseStatus.S404);
					return resp;
				}

			} else if (request.getResourceChain().contains("/xml?uuid=")) {
				DBDAO_XML db = null;
				try {
					db = new DBDAO_XML(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				String[] key = request.getResourceChain().split("=");
				if (db.exists(key[1])) {
					boolean estado = db.delete(key[1]);
					resp.setStatus(HTTPResponseStatus.S200);
					resp.setContent("<html><body><h1>Petición DELETE aceptada</h1><p>Borrado de pagina " + key[1]
							+ " con estado " + estado + "</p></body></html>");
					return resp;
				} else {
					resp.setContent("Error 404 - uuid no encontrada");
					resp.setStatus(HTTPResponseStatus.S404);
					return resp;
				}
			} else if (request.getResourceChain().contains("/xsd?uuid=")) {
				DBDAO_XSD db = null;
				try {
					db = new DBDAO_XSD(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				String[] key = request.getResourceChain().split("=");
				if (db.exists(key[1])) {
					boolean estado = db.delete(key[1]);
					resp.setStatus(HTTPResponseStatus.S200);
					resp.setContent("<html><body><h1>Petición DELETE aceptada</h1><p>Borrado de pagina " + key[1]
							+ " con estado " + estado + "</p></body></html>");
					return resp;
				} else {
					resp.setContent("Error 404 - uuid no encontrada");
					resp.setStatus(HTTPResponseStatus.S404);
					return resp;
				}
			} else if (request.getResourceChain().contains("/xslt?uuid=")) {
				DBDAO_XSLT db = null;
				try {
					db = new DBDAO_XSLT(connection);
				} catch (Exception e1) {
					e("No se pudo conectar a la DB");
					e1.printStackTrace();
					resp.setStatus(HTTPResponseStatus.S500);
					resp.setContent("Error 500 - No se pudo conectar a la DB");
					return resp;
				}
				String[] key = request.getResourceChain().split("=");
				if (db.exists(key[1])) {
					boolean estado = db.delete(key[1]);
					resp.setStatus(HTTPResponseStatus.S200);
					resp.setContent("<html><body><h1>Petición DELETE aceptada</h1><p>Borrado de pagina " + key[1]
							+ " con estado " + estado + "</p></body></html>");
					return resp;
				} else {
					resp.setContent("Error 404 - uuid no encontrada");
					resp.setStatus(HTTPResponseStatus.S404);
					return resp;
				}
			} else {
				resp.setContent("Error 400");
				resp.setStatus(HTTPResponseStatus.S400);
				return resp;
			}
		} else { // Peticiones malformadas
			resp = new HTTPResponse();
			resp.setContent("Error 400");
			resp.setStatus(HTTPResponseStatus.S400);
			resp.setVersion(version);
		}

		return resp;
	}

	public void p(String s) {
		System.out.println(s);
	}

	public void e(String s) {
		System.err.println(s);
	}
	
	private static class MyContentHandler extends DefaultHandler {

        private String element = "";

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {

            if(localName != null && !localName.isEmpty())
                element = localName;
            else
                element = qName;

        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            System.out.println(element + ": " + exception.getMessage());
            throw new SAXException();
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            System.out.println(element + ": " + exception.getMessage());
            throw new SAXException();
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            System.out.println(element + ": " + exception.getMessage());
            throw new SAXException();
        }

        public String getElement() {
            return element;
        }

    }
	

}
