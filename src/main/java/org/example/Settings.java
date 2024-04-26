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

    public void setStartRow(String startRow) {
        properties.setProperty("start_row", startRow);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStartRow() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("start_row");
    }

    public void setColStatusOrder(String colStatusOrder) {
        properties.setProperty("col_status_order", colStatusOrder);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getColStatusOrder() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("col_status_order");
    }

    public void setColNameTool(String colNameTool) {
        properties.setProperty("col_name_tool", colNameTool);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getColNameTool() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("col_name_tool");
    }

    public void setPassBot(String passBot) {
        properties.setProperty("pass_bot", passBot);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPassBot() {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("pass_bot");
    }

}
