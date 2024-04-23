package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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

    Bot() {
        RepaerFile.createExcel("C:\\Users\\Cashless\\Desktop\\Проекты\\DataForTelegramBot\\TBotRepairExcelDB\\РЕМОНТ.xlsm", 0);
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

                BufferedImage imgB = null;
                //если не будет работать корректно(устанавливать соединение),
                // значит нужно делать запрос с fileId(https://api.telegram.org/bot<token>/getfile?file_id={the file_id of the photo you want to download}),
                // получать filepath, и уже его вставлять в форму https://api.telegram.org/file/bot<token>/<file_path> СДЕЛАНО!!!
                imgB = ImageIO.read(downloadImg("https://api.telegram.org/file/bot" + getBotToken() + "/" + getFilePath(update.getMessage().getPhoto().get(3).getFileId())));

                //создаем папку (если ее еще нет)(название должно быть максимально статично) для текущей заявки, и сохраняем туда фото


                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();
                //ищем сначало продукт по штрих коду в одной базе, потом в другой ищем цену и артикул по названию

                SendMessage outMess = new SendMessage();

                outMess.setChatId(chatId);
                outMess.setText("Фото получено!");
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

                response = getActiveOrders(response);

                //Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();

                //Добавляем в наше сообщение id чата а также наш ответ
                outMess.setChatId(chatId);
                //outMess.setText(response);
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

        for (int row = 1; row <= RepaerFile.getLastRowNum(); row++){
            if (!RepaerFile.getCell(row, 2).toString().equals("Закрыт")){
                response += "\n" + row + ". " + RepaerFile.getCell(row, 3).toString();
            }
        }
        return response;
    }

    public String parseMessage(String textMsg) {
        String response;

        //Сравниваем текст пользователя с нашими командами, на основе этого формируем ответ

        switch (textMsg){
            case "/start" : response = "Чат-бот магазина 'Инструмент и крепежПриветствую'. Бот ищет цену товара по названию.";
                System.out.println(response);
                break;
            case "заявки" : response = "Активные заявки:";
                System.out.println(response);
                mod = "заявки";
                break;
            case "/shop all" : response = "Установлена зона поиска: Все магазины.";
                mod = "all";
                break;
            default : if(isInteger(textMsg)){ID_order = Integer.parseInt(textMsg);} //если прислали число, то записываем его как id заявки
        }

        /*if (textMsg.equals("/start")) {
            response = "Чат-бот магазина 'Инструмент и крепежПриветствую'. Бот ищет цену товара по названию.";
        }
        else{
            //response = "Сообщение не распознано";
            response = findProduct(textMsg);
        }*/

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
        //System.out.println(text);
        //System.out.println(text.substring(text.indexOf("file_path") + "file_path".length() + 2).replaceAll("}", "").replaceAll("\"", ""));

        return text.substring(text.indexOf("file_path") + "file_path".length() + 2).replaceAll("}", "").replaceAll("\"", "");
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