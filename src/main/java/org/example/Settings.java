package org.example;

import java.io.*;
import java.util.Properties;

public class Settings {
    private Properties properties;
    private File file;
    Settings() {

        properties = new Properties();
        file = new File("settings.properties");

    }

    public void setAddressFileExcelDB(String addressFileExcelDB) {
        properties.setProperty("address", addressFileExcelDB);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddressFileExcelDB() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("address");
    }

    public void setAddressPhotoDir(String addressPhotoDir) {
        properties.setProperty("address_photo", addressPhotoDir);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddressPhotoDir() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("address_photo");
    }

    public void setBotToken(String botToken) {
        properties.setProperty("bot_token", botToken);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBotToken() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("bot_token");
    }

    public void setBotName(String botName) {
        properties.setProperty("bot_name", botName);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBotName() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("bot_name");
    }

}
