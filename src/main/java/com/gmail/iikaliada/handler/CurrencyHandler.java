package com.gmail.iikaliada.handler;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.constant.Constant;
import com.gmail.iikaliada.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gmail.iikaliada.constant.Constant.CURRENCY_FILE_PATH;

public class CurrencyHandler {

    private final PropUtil propUtil = PropUtil.getInstance();

    public String getCurrency(String currency) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String currencyFilePath = propUtil.getProperties(CURRENCY_FILE_PATH);
        Document doc = Util.getCashedDocument(currencyFilePath);
        if (doc == null || !isDateValid()) {
            Util.saveCurrencyList(currencyFilePath);
            doc = Util.getCashedDocument(currencyFilePath);
        }
        if (doc != null) {
            NodeList charCode = doc.getElementsByTagName("CharCode");
            for (int i = 0; i < charCode.getLength(); i++) {
                String curr = charCode.item(i).getFirstChild().getNodeValue();
                if (curr.equals(currency)) {
                    return doc.getElementsByTagName("Scale")
                            .item(i).getFirstChild().getNodeValue()
                            + " " + currency + " = "
                            + doc.getElementsByTagName("Rate")
                            .item(i).getFirstChild().getNodeValue() + " BYN";
                }
            }
        }
        return "No data found for currency " + currency;
    }

    private boolean isDateValid() {
        try {
            Document document = Util.getCashedDocument(propUtil.getProperties(CURRENCY_FILE_PATH));
            if (document == null) {
                return false;
            }
            String dateInDoc = document.getElementsByTagName("DailyExRates")
                    .item(0).getAttributes().getNamedItem("Date").getNodeValue();
            if (dateInDoc.equals(dateToString(new Date()))) {
                return true;
            }
        } catch (IOException
                | ParserConfigurationException
                | SAXException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println(format.format(date));
        return format.format(date);
    }
}
