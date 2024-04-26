package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class Authorization {

    Properties properties;
    File file;

    Authorization () {
        properties = new Properties();
        file = new File("user_list.properties");
    }

    //проверяет зарегистрирован ли user
    public boolean isAuthorized (Long userId) {
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (properties.getProperty(userId.toString(), "false").equals("false")) {
            return false;
        } else {
            return true;
        }
    }

    //зарегистрировать пользователя
    public void addUser (Long userId) {
        properties.setProperty(userId.toString(), "users"); //пока что система иерархии не реализованна (все пользователи имеют статус users)
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //проверка пароля
    public boolean checkPass(String pass) {
        Settings settings = new Settings();

        if (pass.equals(settings.getPassBot())) {
            return true;
        } else {
            return false;
        }
    }

    //очистить список пользователей
    public void removeAllUsers () {
        properties.clear();
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //удалить пользователя
    public void removeUser (Object userId) {
        properties.remove(userId.toString());
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
