package es.uvigo.esei.dai.hybridserver.http;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class HandlerValidacion extends DefaultHandler {

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