package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {

    Settings settings = new Settings();
    //создаем две константы, присваиваем им значения токена и имя бота соответсвтенно
    //вместо звездочек подставляйте свои данные
    final private String BOT_TOKEN = settings.getBotToken();
    final private String BOT_NAME = settings.getBotName();
    Keyboard keyboard;
    Excel RepaerFile = new Excel();
    private String mod;
    private int ID_order;   //id заявки для которой нужно сохранить фото
    private Authorization authorization = new Authorization();
    private Logger logger = new Logger();
    BotDefaultMsg botDefaultMsg = new BotDefaultMsg();

    // НАСТРОЙКИ
    private String PHOTO_DIR = settings.getAddressPhotoDir() + "\\";   //директория, где буду сохраняться фото
    private String EXCEL_DB_DIR_AND_NAME = settings.getAddressFileExcelDB(); //полный путь с именем к файлу excel с заявками
    private int START_ROW = Integer.parseInt(settings.getStartRow());   //строка начала заявок
    private int STATUS_COLUMN = Integer.parseInt(settings.getColStatusOrder());   //номер колонки со статусом заявки
    private int NAME_OF_THE_TOOL_COLUMN = Integer.parseInt(settings.getColNameTool());    //номер колонки с названием инструмента

    Bot() {
        keyboard = new Keyboard();
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
            logger.saveLogMessage(update);  //вызов логгера

            //авторизация
            if (!authorization.isAuthorized(update.getMessage().getFrom().getId())) {

                //проверка пароля
                if (update.hasMessage() && update.getMessage().hasText()) {
                    if (!authorization.checkPass(update.getMessage().getText())) {
                        delLastMsg(update); //удаляем сообщение(неправильный пароль)
                        sendMsg(update, botDefaultMsg.getBotMsg(0));
                    } else {
                        authorization.addUser(update.getMessage().getFrom().getId());
                        delLastMsg(update); //удаляем сообщение(пароль)
                        sendMsg(update,botDefaultMsg.getBotMsg(1));
                        return;
                    }
                }

                //отправка сообщения
                sendMsg(update,botDefaultMsg.getBotMsg(2));
                //outMess.setReplyMarkup(keyboard.getReplyKeyboardMarkup());

                return;
            }

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

                    response = botDefaultMsg.getBotMsg(3);
                    //end
                } else {response = botDefaultMsg.getBotMsg(4);}

                sendMsg(update, response, keyboard);

            }

            if (update.hasMessage() && update.getMessage().hasText()) {

                sendMsg(update, parseMessage(update.getMessage()), keyboard);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ищет активные заявки
    private String getActiveOrders(String response) {

        keyboard.refreshKeyboard();

        for (int row = START_ROW; row <= RepaerFile.getLastRowNum(); row++){
            if (!RepaerFile.getCell(row, STATUS_COLUMN).toString().equals("Закрыт")){
                //System.out.println(RepaerFile.getCell(row, STATUS_COLUMN).getCellStyle().getFillForegroundColor()); //нет возможности получить цвет ячейки (методы сломаны в библиотеке)
                response += "\n" + (row + 1) + ". " + RepaerFile.getCell(row, NAME_OF_THE_TOOL_COLUMN).toString();
                //добавить номер заявки на клавиатуру
                keyboard.addItemToKeyboard(row + 1);
            }
        }
        return response;
    }

    //проверяет есть ли активные заявки true - есть
    private boolean isOrders() {

        for (int row = START_ROW; row <= RepaerFile.getLastRowNum(); row++){
            if (!RepaerFile.getCell(row, STATUS_COLUMN).toString().equals("Закрыт")){
                return true;
            }
        }
        return false;
    }

    public String parseMessage(Message Msg) {
        String response = null;
        Long userID = Msg.getFrom().getId();    //записываем ID пользователя
        //System.out.println("Имя: " + Msg.getFrom().getFirstName() + " " + Msg.getFrom().getLastName());

        //свитч внешний определяет права доступа к командам разным группам пользователей
        //!!!пока оставлю такую реализацию, но нужно сделать через перечень команд в проперти файле
        switch (authorization.getUserPermission(userID)) {
            case "admin":
                response = adminsCommands(Msg);
                break;
            case "user":
                response = usersCommands(Msg);
                break;
            default:
        }

        return response;
    }

    //команды пользователя со статусом "user"
    private String usersCommands(Message Msg) {
        String response = null;
        //Сравниваем текст пользователя с нашими командами, на основе этого формируем ответ
        switch (Msg.getText().toLowerCase()){
            case "/start" : response = botDefaultMsg.getBotMsg(5);
                System.out.println(response);
                break;
            case "заявки" : response = botDefaultMsg.getBotMsg(6);
                RepaerFile.createExcel(EXCEL_DB_DIR_AND_NAME, 0); //переоткрывает книгу после каждого запроса (книгу после ввода новых данных нужно сохранять)
                System.out.println(response);
                if (isOrders()) {
                    response = getActiveOrders(response);
                } else {response = botDefaultMsg.getBotMsg(7);}
                System.out.println(response);
                break;
            case "/exit" : response = botDefaultMsg.getBotMsg(8);
                authorization.removeUser(Msg.getFrom().getId());
                break;
            default : if(isInteger(Msg.getText())){ID_order = Integer.parseInt(Msg.getText()); response = botDefaultMsg.getBotMsg(9) + ID_order + "\n";} //если прислали число, то записываем его как id заявки
        }
        return response;
    }

    //команды пользователя со статусом "admin"
    private String adminsCommands(Message Msg) {
        String response = null;
        response = usersCommands(Msg);   //администратору доступны все команды пользователя

        //Сравниваем текст пользователя с нашими командами, на основе этого формируем ответ
        switch (Msg.getText().toLowerCase()){
            case "/shot down" : response = botDefaultMsg.getBotMsg(10);   //выключение сервера
                System.out.println(response);
                sendMsg(Msg, response);
                System.exit(0);
                break;
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

    //удаляет последнее сообщение
    public void delLastMsg (Update update) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(update.getMessage().getChatId());
            deleteMessage.setMessageId(update.getMessage().getMessageId());
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //отправка сообщения
    public void sendMsg (Update update, String response) {
        try {
            SendMessage outMess = new SendMessage();
            outMess.setChatId(update.getMessage().getChatId().toString());
            outMess.setText(response);
            execute(outMess);
        } catch (TelegramApiException e) {

            //Обработка ошибки связанной с достижением лимита одного сообщения
            if (e.getLocalizedMessage().equals("Error sending message: [400] Bad Request: message is too long")) {
                sendMsg(update, botDefaultMsg.getBotMsg(11));
            }
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void sendMsg (Message Msg, String response) {
        try {
            SendMessage outMess = new SendMessage();
            outMess.setChatId(Msg.getChatId().toString());
            outMess.setText(response);
            execute(outMess);
        } catch (TelegramApiException e) {

            //Обработка ошибки связанной с достижением лимита одного сообщения
            if (e.getLocalizedMessage().equals("Error sending message: [400] Bad Request: message is too long")) {
                sendMsg(Msg, botDefaultMsg.getBotMsg(11));
            }
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void sendMsg (Update update, String response, Keyboard keyboard) {
        try {
            SendMessage outMess = new SendMessage();
            outMess.setChatId(update.getMessage().getChatId().toString());
            outMess.setText(response);
            outMess.setReplyMarkup(keyboard.getReplyKeyboardMarkup());

            //InlineKeyboardMarkup пока что на реализации
           /* InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton("Update message text");
            inlineKeyboardButton.setCallbackData("update_msg_text");
            rowInline.add(inlineKeyboardButton);
            // Set the keyboard to the markup
            rowsInline.add(rowInline);
            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            outMess.setReplyMarkup(markupInline);*/
            //InlineKeyboardMarkup (для обработки ответа от инлайн клавиатуры нужно использовать условие update.hasCallbackQuery() в главном методе)
            //https://github.com/MonsterDeveloper/java-telegram-bot-tutorial/blob/master/lesson-6.-inline-keyboards-and-editing-messages-text.md

            execute(outMess);
        } catch (TelegramApiException e) {

            //Обработка ошибки связанной с достижением лимита одного сообщения
            if (e.getLocalizedMessage().equals("Error sending message: [400] Bad Request: message is too long")) {
                sendMsg(update, botDefaultMsg.getBotMsg(11), keyboard);
            }
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
