package config;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ConfigWriter {
	private String configFile;

    public void setFile(String configFile) {
            this.configFile = configFile;
    }

    public void saveConfig(Config cfg) throws Exception {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                            .createXMLEventWriter(new FileOutputStream(configFile));
            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);

            // create config open tag
            StartElement configStartElement = eventFactory.createStartElement("",
                            "", "config");
            eventWriter.add(configStartElement);
            eventWriter.add(end);
            // Write the different nodes
            createNode(eventWriter, ConfigLoader.NUMOFBATCH, cfg.numOfBatch.toString());
            createNode(eventWriter, ConfigLoader.CANVAS, toString(cfg.cnv));
//            createNode(eventWriter, "current", "0");
//            createNode(eventWriter, "interactive", "0");

            eventWriter.add(eventFactory.createEndElement("", "", "config"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
    }

    private void createNode(XMLEventWriter eventWriter, String name,
                    String value) throws XMLStreamException {

            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent tab = eventFactory.createDTD("\t");
            // create Start node
            StartElement sElement = eventFactory.createStartElement("", "", name);
            eventWriter.add(tab);
            eventWriter.add(sElement);
            // create Content
            Characters characters = eventFactory.createCharacters(value);
            eventWriter.add(characters);
            // create End node
            EndElement eElement = eventFactory.createEndElement("", "", name);
            eventWriter.add(eElement);
            eventWriter.add(end);

    }
    

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
}
