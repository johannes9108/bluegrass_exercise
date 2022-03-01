package com.jh.dangerzone.service;

import com.jh.dangerzone.doa.ResponseDataRepository;
import com.jh.dangerzone.domain.Config;
import com.jh.dangerzone.domain.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.transaction.Transactional;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZoneOffset;

@Service
public class ServiceDispatcher {


    private Config config;

    @Autowired
    OpenDataMetobsReader openDataMetobsReader;

    @Autowired
    ResponseDataRepository repository;

    public void handleRequest(Config config) {
        this.config = config;
        try {
            ResponseData data = openDataMetobsReader.callAPI(config.getStationId(), "latest-hour");
            createXMLDocumentFromData(data);
            storeInDB(data);
        } catch (IOException e) {
            throw new RuntimeException("Application encountered an error: " + e.getLocalizedMessage());
        }


    }

    private void createDirectory(String directoryLocation) throws IOException {
        File file = new File(directoryLocation);
        if (!file.exists() && !file.mkdir()) {
            throw new IOException("Directory couldn't be found/created");
        }
    }

    /**
     * Creating a document model from the parsed JSON response
     *
     * @param data - The converted response
     */
    private void createXMLDocumentFromData(ResponseData data) {


        DocumentBuilderFactory dbFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            createDirectory(config.getDirectoryLocation());
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Setting
            Element rootElement = doc.createElement("WeatherData");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            doc.appendChild(rootElement);

            Element reading = doc.createElement("Reading");
            rootElement.appendChild(reading);

            //Setting Metadata

            Element stationID = doc.createElement("StationId");
            stationID.appendChild(doc.createTextNode("" + data.getStationID()));
            Element stationName = doc.createElement("StationName");
            stationName.appendChild(doc.createTextNode("" + data.getStationName()));
            Element timeStamp = doc.createElement("Timestamp");
            timeStamp.appendChild(doc.createTextNode("" + (data.getTimeStamp().toInstant(ZoneOffset.UTC))));
            reading.appendChild(stationID);
            reading.appendChild(stationName);
            reading.appendChild(timeStamp);

            // Setting the Parameters
            Element parameter = doc.createElement("Parameter");

            Element name = doc.createElement("Name");
            name.appendChild(doc.createTextNode("Temp"));
            Element value = doc.createElement("Value");
            value.appendChild(doc.createTextNode("" + data.getTemperature()));
            parameter.appendChild(name);
            parameter.appendChild(value);
            reading.appendChild(parameter);

            parameter = doc.createElement("Parameter");
            name = doc.createElement("Name");
            name.appendChild(doc.createTextNode("WindDirection"));
            value = doc.createElement("Value");
            value.appendChild(doc.createTextNode("" + data.getWindDirection()));
            parameter.appendChild(name);
            parameter.appendChild(value);
            reading.appendChild(parameter);

            parameter = doc.createElement("Parameter");
            name = doc.createElement("Name");
            name.appendChild(doc.createTextNode("WindSpeed"));
            value = doc.createElement("Value");
            value.appendChild(doc.createTextNode("" + data.getWindSpeed()));
            parameter.appendChild(name);
            parameter.appendChild(value);
            reading.appendChild(parameter);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "8");
            DOMSource source = new DOMSource(doc);
            if (validateXMLSchema(new File("src/main/resources/weather_data.xsd"), source)) {
                StreamResult result = new StreamResult(new File(
                        config.getDirectoryLocation() + "station-" + config.getStationId() + "_timestamp-" + data.getTimeStamp().toInstant(ZoneOffset.UTC).toString().replace(":", "-")).getPath());
                transformer.transform(source, result);

                // Output to console for testing
                StreamResult consoleResult = new StreamResult(System.out);
                transformer.transform(source, consoleResult);
            } else {
                throw new RuntimeException("The generated XML couldn't be validated against weather_data.xsd");
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (TransformerException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * Compares the generated XML-object against the specified template in weather_data.xsd
     *
     * @param xsdFile
     * @param xmlRoot
     * @return
     */
    private boolean validateXMLSchema(File xsdFile, Source xmlRoot) {
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlRoot);
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        } catch (SAXException e1) {
            System.out.println("SAX Exception: " + e1.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Stores the acquired data into a SQL database
     *
     * @param data - The converted response
     */
    @Transactional
    private void storeInDB(ResponseData data) {
        repository.saveAndFlush(data);
    }
}
