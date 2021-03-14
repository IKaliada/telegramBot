package com.gmail.iikaliada.handler;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.constant.Constant;
import com.gmail.iikaliada.util.Util;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gmail.iikaliada.constant.Constant.CURRENCY_FILE_PATH;

public class KeyboardHandler {

    private final PropUtil propUtil = PropUtil.getInstance();

    public ReplyKeyboardMarkup getKeyboard() throws IOException,
            ParserConfigurationException, SAXException {
        Document doc = Util.getCashedDocument(propUtil.getProperties(CURRENCY_FILE_PATH));
        if (doc == null) {
            doc = Util.getDocument();
        }
        NodeList charCode = doc.getElementsByTagName("CharCode");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        String curr = "";
        for (int i = 1; i <= charCode.getLength(); i++) {
            curr = charCode.item(i - 1).getFirstChild().getNodeValue();
            row.add(curr);
            if (i % 5 == 0) {
                keyboard.add(row);
                row = new KeyboardRow();
            }
            System.out.print(curr + " ");
        }
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        return keyboardMarkup;
    }
}
