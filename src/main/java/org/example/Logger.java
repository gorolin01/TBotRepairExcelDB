package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Logger {

    File file = new File("log.txt");

    public void saveLogMessage (Update update) {
        try {
            String result = "";

            //считываем все из файла
            FileReader fileReader = new FileReader(file);
            int c;
            while((c = fileReader.read()) != -1){
                result += (char)c;
            }

            //запись логов
            result += update.getMessage().getFrom().getId() + " | ";
            result += update.getMessage().getFrom().getFirstName() + " | ";
            result += update.getMessage().getFrom().getLastName() + " | ";
            result += update.getMessage().getText() + " | ";
            result += update.getMessage().getMessageId() + " | ";
            //result += update.getMessage().getAudio().getFileId() + " | ";
            //result += update.getMessage().getVideo().getFileId() + " | ";
            //result += update.getChatMember().getFrom().getId() + " | ";
            result += update.getMessage().getDate() + " | ";
            Calendar calendar = new GregorianCalendar(); result += calendar.getTime() + "\n";

            //запись в файл
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(result);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
