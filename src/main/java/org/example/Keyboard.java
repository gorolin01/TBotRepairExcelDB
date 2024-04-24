package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class Keyboard extends ReplyKeyboardMarkup {

    ReplyKeyboardMarkup replyKeyboardMarkup;
    Keyboard() {
        //Создаем объект будущей клавиатуры и выставляем нужные настройки
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true); //подгоняем размер
        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования

        refreshKeyboard();

    }

    public ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        return replyKeyboardMarkup;
    }

    //обнуляет клавиатуру и добавляет кнопку "заявки"
    public void refreshKeyboard() {

        replyKeyboardMarkup = new ReplyKeyboardMarkup(new ArrayList<KeyboardRow>());
        replyKeyboardMarkup.getKeyboard().add(new KeyboardRow());   //строка для системных кнопок
        replyKeyboardMarkup.getKeyboard().get(0).add(new KeyboardButton("Заявки"));
        replyKeyboardMarkup.getKeyboard().add(new KeyboardRow());   //создаем первую строку для id заявок

    }

    public void addItemToKeyboard(int data) {

        //получаем последнюю строку на клавиатуре, если она длиннее положенного, то создаем новую строку
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>(replyKeyboardMarkup.getKeyboard());
        if (keyboardRows.get(keyboardRows.size() - 1).size() > 3) {
            keyboardRows.add((new KeyboardRow()));
            keyboardRows.get(keyboardRows.size() - 1).add(new KeyboardButton(Integer.toString(data)));
        }else {
            keyboardRows.get(keyboardRows.size() - 1).add(new KeyboardButton(Integer.toString(data)));
        }

        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

}
