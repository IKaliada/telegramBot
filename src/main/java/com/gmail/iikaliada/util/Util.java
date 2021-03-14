package com.gmail.iikaliada.util;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.constant.Constant;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Util {
    public static Document getDocument() throws IOException, ParserConfigurationException, SAXException {
        PropUtil propUtil = PropUtil.getInstance();
        String currencyUrl = propUtil.getProperties(Constant.CURRENCY_URL);
        URL url = new URL(currencyUrl);
        URLConnection conn = url.openConnection();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(conn.getInputStream());
    }

    public static Document getCashedDocument(String currencyFilePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(currencyFilePath);
        if (!file.exists()) {
            return null;
        }
        InputStream inputStream = new FileInputStream(file);
        return builder.parse(inputStream);
    }

    public static void saveCurrencyList(String currencyFilePath) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        DOMSource domSource = new DOMSource(Util.getDocument());
        File currencyFile = new File(currencyFilePath);
        if (currencyFile.exists()) {
            currencyFile.delete();
        }
        currencyFile.createNewFile();

        StreamResult file = new StreamResult(currencyFile);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, file);
    }

}
