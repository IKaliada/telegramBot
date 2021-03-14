package com.gmail.iikaliada;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.gmail.iikaliada.constant.Constant.CONFIG_PROPERTIES;

public class PropUtil {
    private static Properties properties;
    private static PropUtil instance = null;
    private PropUtil() {
    }
    public static PropUtil getInstance(){
        if (instance == null){
            instance = new PropUtil();
            properties = new Properties();
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(CONFIG_PROPERTIES);
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    public String getProperties(String name){
        return properties.getProperty(name);
    }


}
