package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    //создаем две константы, присваиваем им значения токена и имя бота соответсвтенно
    //вместо звездочек подставляйте свои данные
    final private String BOT_TOKEN = "7071384156:AAH1uOOPc98nGuO8ODPPGRBSMDvH61ucoyw";
    final private String BOT_NAME = "RepairDBPhoto_bot";
    ReplyKeyboardMarkup replyKeyboardMarkup;
    Excel RepaerFile = new Excel();
    private String mod;
    private int ID_order;   //id заявки для которой нужно сохранить фото

    // НАСТРОЙКИ
    private String PHOTO_DIR = "I:\\WorkSpace\\TBotRepairExcelDB\\res\\";   //директория, где буду сохраняться фото
    private String EXCEL_DB_DIR_AND_NAME = "I:\\WorkSpace\\TBotRepairExcelDB\\РЕМОНТ.xlsm"; //полный путь с именем к файлу excel с заявками
    private int START_ROW = 1;   //строка начала заявок
    private int STATUS_COLUMN = 1;   //номер колонки со статусом заявки
    private int NAME_OF_THE_TOOL_COLUMN = 2;    //номер колонки с названием инструмента

    Bot() {
        initKeyboard();
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {

            if (update.getMessage().hasPhoto()) {

                String response;

                if (ID_order != 0) {    //проверка на не заданный id заявки

                    //создаем папку (если ее еще нет)(название должно быть максимально статично) для текущей заявки, и сохраняем туда фото
                    //загрузка и сохранение в файл фото
                    InputStream inputStream = downloadImg("https://api.telegram.org/file/bot" + getBotToken() + "/" + getFilePath(update.getMessage().getPhoto().get(3).getFileId()));
                    new File(PHOTO_DIR + ID_order).mkdirs();
                    OutputStream outputStream = new FileOutputStream(PHOTO_DIR + ID_order + "\\" + update.getMessage().getPhoto().get(3).getFileUniqueId() + ".jpg");
                    byte[] buffer = new byte[2048];

                    int length = 0;

                    while ((length = inputStream.read(buffer)) != -1) {
                        System.out.println("Buffer Read of length: " + length);
                        outputStream.write(buffer, 0, length);
                    }

                    inputStream.close();
                    outputStream.close();

                    response = "Фото получено!";
                    //end
                } else {response = "Не был задан номер заявки!";}

                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();

                SendMessage outMess = new SendMessage();

                outMess.setChatId(chatId);
                outMess.setText(response);
                outMess.setReplyMarkup(replyKeyboardMarkup);

                execute(outMess);

            }

            if (update.hasMessage() && update.getMessage().hasText()) {

                //Извлекаем из объекта сообщение пользователя
                Message inMess = update.getMessage();
                //Достаем из inMess id чата пользователя
                String chatId = inMess.getChatId().toString();
                //Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                String response = parseMessage(inMess.getText());

                //Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();

                //Добавляем в наше сообщение id чата а также наш ответ
                outMess.setChatId(chatId);
                outMess.setText(response);
                outMess.setReplyMarkup(replyKeyboardMarkup);

                //Отправка в чат
                execute(outMess);

            }
        } catch (TelegramApiException | IOException e) {
            //Обработка ошибки связанной с достижением лимита одного сообщения
            if (e.getLocalizedMessage().equals("Error sending message: [400] Bad Request: message is too long")) {

                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();
                String response = "Слишком много совпадений. Пожалуйста укажите более подробную фразу поиска.";

                SendMessage outMess = new SendMessage();

                outMess.setChatId(chatId);
                outMess.setText(response);
                outMess.setReplyMarkup(replyKeyboardMarkup);

                //Отправка в чат
                try {
                    execute(outMess);
                } catch (TelegramApiException telegramApiException) {
                    telegramApiException.printStackTrace();
                }

            }
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    //ищет активные заявки
    private String getActiveOrders(String response) {

        for (int row = START_ROW; row <= RepaerFile.getLastRowNum(); row++){
            if (!RepaerFile.getCell(row, STATUS_COLUMN).toString().equals("Закрыт")){
                response += "\n" + (row + 1) + ". " + RepaerFile.getCell(row, NAME_OF_THE_TOOL_COLUMN).toString();
            }
        }
        return response;
    }

    public String parseMessage(String textMsg) {
        String response = null;

        //Сравниваем текст пользователя с нашими командами, на основе этого формируем ответ

        switch (textMsg.toLowerCase()){
            case "/start" : response = "Чат-бот магазина 'Инструмент и крепежПриветствую'. Бот ищет цену товара по названию.";
                System.out.println(response);
                break;
            case "заявки" : response = "Активные заявки:";
                RepaerFile.createExcel(EXCEL_DB_DIR_AND_NAME, 0); //переоткрывает книгу после каждого запроса (книгу после ввода новых данных нужно сохранять)
                System.out.println(response);
                mod = "заявки";
                response = getActiveOrders(response);
                System.out.println(response);
                break;
            case "/shop all" : response = "Установлена зона поиска: Все магазины.";
                mod = "all";
                break;
            default : if(isInteger(textMsg)){ID_order = Integer.parseInt(textMsg); response = "Установлен заказ №" + ID_order + "\n";} //если прислали число, то записываем его как id заявки
        }

        return response;
    }

    public InputStream downloadImg(String name) throws IOException {

        URL url = new URL(name);
        URLConnection c = url.openConnection();
        return (c.getInputStream());

    }

    public String getFilePath(String FileID){

        String text = null;
        try (Scanner scanner = new Scanner(downloadImg("https://api.telegram.org/bot" + getBotToken() + "/getfile?file_id=" + FileID), StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.substring(text.indexOf("file_path") + "file_path".length() + 2).replaceAll("}", "").replaceAll("\"", "");
    }

    //клавиатура
    void initKeyboard()
    {
        //Создаем объект будущей клавиатуры и выставляем нужные настройки
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true); //подгоняем размер
        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования

        //Создаем список с рядами кнопок
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
        //Создаем один ряд кнопок и добавляем его в список
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRows.add(keyboardRow);
        //Добавляем одну кнопку с текстом "Просвяти" наш ряд
        keyboardRow.add(new KeyboardButton("Заявки"));
        //добавляем лист с одним рядом кнопок в главный объект
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

    //проверка на int
    public boolean isInteger(String s) {
        if (s == null || s.isEmpty()) return false;

        for (int i = (s.charAt(0) == '-') ? 1 : 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                // В строке найден недопустимый символ.
                return false;
            }
        }

        return true;
    }

}
