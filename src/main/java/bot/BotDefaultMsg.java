package bot;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BotDefaultMsg {

    List<String> lines;
    BotDefaultMsg () {
        try {
            lines = Files.readAllLines(Paths.get("botMessage.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBotMsg (int num_lines) {
        if (num_lines > (lines.size() - 1)) {
            System.out.println("Ошибка! Строки " + num_lines + " нет в файле botMessage.txt");
            return null;
        } else {
            return lines.get(num_lines) ;
        }
    }

}
