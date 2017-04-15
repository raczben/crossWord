package config;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import main.Canvas;

public class ConfigLoader {

	static final String CANVAS = "canvas";
//	static final String XDIM = "xdim";
//	static final String YDIM = "ydim";
	static final String NUMOFBATCH = "numOfBatch";
	Config cfg;

	

	public Config readConfig(String configFile) {
		try {
			return readConfig( new FileInputStream(configFile) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
//	@SuppressWarnings({ "unchecked", "null" })
	public Config readConfig(InputStream configFileStream) {
		cfg = new Config();
		try {
			// First, create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
//			InputStream in = new FileInputStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(configFileStream);
			// read the XML document
//			Item item = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// If we have an item element, we create a new item
					String startElemName = startElement.getName().getLocalPart();
					switch (startElemName) {
					case CANVAS:
						try {
							cfg.cnv = loadCanvas(eventReader);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case NUMOFBATCH:
						event = eventReader.nextEvent();
						cfg.numOfBatch = Integer.valueOf(event.asCharacters().getData());
						break;

					default:
						break;
					}
				}

			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return cfg;
	}
	

    private Canvas loadCanvas(XMLEventReader eventReader) throws XMLStreamException, ClassNotFoundException, IOException {
		XMLEvent event = eventReader.nextEvent();
		String cnvSerial = event.asCharacters().getData();
		return (Canvas) fromString(cnvSerial);
	}


	/** Read the object from Base64 string. */
   private static Object fromString( String s ) throws IOException ,
                                                       ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
   }

}
