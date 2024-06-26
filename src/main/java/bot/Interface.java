package bot;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Interface extends JPanel {

    private JPanel panelSettings = new JPanel(new MigLayout("","[right][left][center]","[][][]"));    //панель с элементами настройки
    //private File DBExcel_file;  //записываем файл базы данных excel
    private Settings settings;
    Thread botThread;   //поток для бота
    private Authorization authorization = new Authorization();
    Interface() {
        settings = new Settings();
    }

    public void createAndShowGUI() {

        JFrame frame = new JFrame("ServicePhotoSaver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        //путь к файлу базы данных
        final JTextField excelAddress = new JTextField(40);
        excelAddress.setEditable(false);
        excelAddress.setText(settings.getAddressFileExcelDB()); //загружаем путь
        final JButton addExcelFile = new JButton("Выбрать файл");
        addExcelFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser DBExcel_fileChooser = new JFileChooser();
                int returnValue = DBExcel_fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    //DBExcel_file = DBExcel_fileChooser.getSelectedFile();
                    excelAddress.setText(DBExcel_fileChooser.getSelectedFile().getAbsolutePath());
                    settings.setAddressFileExcelDB(DBExcel_fileChooser.getSelectedFile().getAbsolutePath());    //записываем путь
                }
            }
        });

        //путь сохранения фото
        final JTextField photoAddress = new JTextField(40);
        photoAddress.setEditable(false);
        photoAddress.setText(settings.getAddressPhotoDir()); //загружаем путь
        final JButton addPhotoAddress = new JButton("Задать путь");
        addPhotoAddress.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser PhotoAddress_fileChooser = new JFileChooser();
                PhotoAddress_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = PhotoAddress_fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    //DBExcel_file = PhotoAddress_fileChooser.getSelectedFile();
                    photoAddress.setText(PhotoAddress_fileChooser.getSelectedFile().getAbsolutePath());
                    settings.setAddressPhotoDir(PhotoAddress_fileChooser.getSelectedFile().getAbsolutePath());    //записываем путь
                }
            }
        });

        //бот токен
        final JTextField botToken = new JTextField(40);
        JTextArea botToken_description = new JTextArea("Токен: ");
        botToken_description.setEditable(false);
        botToken_description.setBackground(new Color(238,238,238));
        botToken.setEditable(true);
        botToken.setText(settings.getBotToken()); //загружаем токен

        //имя бота
        final JTextField botName = new JTextField(40);
        JTextArea botName_description = new JTextArea("Имя бота: ");
        botName_description.setEditable(false);
        botName_description.setBackground(new Color(238,238,238));
        botName.setEditable(true);
        botName.setText(settings.getBotName()); //загружаем имя

        //строка начала заявок
        final JTextField startRow = new JTextField(4);
        JTextArea startRow_description = new JTextArea("Строка начала заявок: ");
        startRow_description.setEditable(false);
        startRow_description.setBackground(new Color(238,238,238));
        startRow.setEnabled(true);
        startRow.setText(settings.getStartRow());

        //номер колонки со статусом заявки
        final JTextField colStatusOrder = new JTextField(4);
        JTextArea colStatusOrder_description = new JTextArea("Номер колонки со статусом заявки: ");
        colStatusOrder_description.setEditable(false);
        colStatusOrder_description.setBackground(new Color(238,238,238));
        colStatusOrder.setEnabled(true);
        colStatusOrder.setText(settings.getColStatusOrder());

        //номер колонки с названием инструмента
        final JTextField colNameTool = new JTextField(4);
        JTextArea colNameTool_description = new JTextArea("Номер колонки с названием инструмента: ");
        colNameTool_description.setEditable(false);
        colNameTool_description.setBackground(new Color(238,238,238));
        colNameTool.setEnabled(true);
        colNameTool.setText(settings.getColNameTool());

        //кнопка остановки бота (НЕ МОГУ УБИТЬ ПОТОК И БОТА!)
        /*JButton stopBot = new JButton("Остановить бота");
        stopBot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botThread.stop();
            }
        });*/

        //окно ввода пароля для авторизации
        final JTextField passField = new JTextField(10);
        JTextArea passArea = new JTextArea("Пароль доступа к боту: ");
        passField.setEnabled(true);
        passArea.setEditable(false);
        passArea.setBackground(new Color(238,238,238));
        passField.setText(settings.getPassBot());

        //кнопка сохранить настройки
        final JButton saveSettings = new JButton("Сохранить настройки");
        saveSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!settings.getPassBot().equals(passField.getText())) {authorization.removeAllUsers();}   //если пароль сменили, то разлогинит всех пользователей
                //записываем все что есть в форме в файл проперти
                //ПОКА ЧТО НЕТ ОБРАБОТКИ НЕВЕРНЫХ ДАННЫХ.
                settings.setStartRow(startRow.getText());
                settings.setColStatusOrder(colStatusOrder.getText());
                settings.setColNameTool(colNameTool.getText());
                settings.setBotToken(botToken.getText());
                settings.setBotName(botName.getText());
                settings.setPassBot(passField.getText());
            }
        });

        //кнопка запуска бота
        final JButton startBot = new JButton("Запустить бота");
        startBot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botThread = new Thread(new BotThread());
                botThread.start();
                disableBtn(new ArrayList<>(Arrays.asList(addExcelFile,addPhotoAddress, saveSettings, startBot)));   //после запуска бота заблокировать все кнопки
                disableTextField(new ArrayList<>(Arrays.asList(botToken, botName, startRow, colStatusOrder, colNameTool, passField))); //после запуска бота заблокировать все поля ввода
            }
        });

        panelSettings.add(addExcelFile);
        panelSettings.add(excelAddress, "wrap");
        panelSettings.add(addPhotoAddress);
        panelSettings.add(photoAddress, "wrap");
        panelSettings.add(botToken_description);
        panelSettings.add(botToken, "wrap");
        panelSettings.add(botName_description);
        panelSettings.add(botName, "wrap");
        panelSettings.add(startRow_description);
        panelSettings.add(startRow, "wrap");
        panelSettings.add(colStatusOrder_description);
        panelSettings.add(colStatusOrder, "wrap");
        panelSettings.add(colNameTool_description);
        panelSettings.add(colNameTool, "wrap");
        panelSettings.add(startBot, "wrap");
        //panelSettings.add(stopBot, "wrap");
        panelSettings.add(passArea);
        panelSettings.add(passField, "wrap");

        panelSettings.add(saveSettings);    //кнопка сохранения

        frame.add(panelSettings, BorderLayout.WEST);
        frame.setVisible(true);

    }

    private void disableBtn (ArrayList<JButton> buttonArrayList) {
        for (JButton btn : buttonArrayList) {
            btn.setEnabled(false);
        }
    }

    private void disableTextField (ArrayList<JTextField> textFieldsArrayList) {
        for (JTextField textField : textFieldsArrayList) {
            textField.setEditable(false);
        }
    }

}
