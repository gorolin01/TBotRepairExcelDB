package org.example;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Interface extends JPanel {

    private JPanel panelSettings = new JPanel(new MigLayout("","[right][center][center]","[][][]"));    //панель с элементами настройки
    //private File DBExcel_file;  //записываем файл базы данных excel
    private Settings settings;
    Thread botThread;   //поток для бота
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
        JButton addExcelFile = new JButton("Выбрать файл");
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
        JButton addPhotoAddress = new JButton("Задать путь");
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
        botToken.setEditable(true);
        botToken.setText(settings.getBotToken()); //загружаем токен

        //имя бота
        final JTextField botName = new JTextField(40);
        JTextArea botName_description = new JTextArea("Имя бота: ");
        botName_description.setEditable(false);
        botName.setEditable(true);
        botName.setText(settings.getBotName()); //загружаем имя

        //строка начала заявок
        final JTextField startRow = new JTextField(10);
        JTextArea startRow_description = new JTextArea("Строка начала заявок: ");
        startRow_description.setEditable(false);
        startRow.setEnabled(true);
        startRow.setText(settings.getStartRow());

        //номер колонки со статусом заявки
        final JTextField colStatusOrder = new JTextField(10);
        JTextArea colStatusOrder_description = new JTextArea("Номер колонки со статусом заявки: ");
        colStatusOrder_description.setEditable(false);
        colStatusOrder.setEnabled(true);
        colStatusOrder.setText(settings.getColStatusOrder());

        //номер колонки с названием инструмента
        final JTextField colNameTool = new JTextField(10);
        JTextArea colNameTool_description = new JTextArea("Номер колонки с названием инструмента: ");
        colNameTool_description.setEditable(false);
        colNameTool.setEnabled(true);
        colNameTool.setText(settings.getColNameTool());

        //кнопка запуска бота
        final JButton startBot = new JButton("Запустить бота");
        startBot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botThread = new Thread(new BotThread());
                botThread.start();
                startBot.setEnabled(false);
            }
        });

        //кнопка остановки бота (НЕ МОГУ УБИТЬ ПОТОК И БОТА!)
        /*JButton stopBot = new JButton("Остановить бота");
        stopBot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botThread.stop();
            }
        });*/

        //окно ввода пароля для авторизации


        //кнопка сохранить настройки
        JButton saveSettings = new JButton("Сохранить настройки");
        saveSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //записываем все что есть в форме в файл проперти
                //ПОКА ЧТО НЕТ ОБРАБОТКИ НЕВЕРНЫХ ДАННЫХ.
                settings.setStartRow(startRow.getText());
                settings.setColStatusOrder(colStatusOrder.getText());
                settings.setColNameTool(colNameTool.getText());
                settings.setBotToken(botToken.getText());
                settings.setBotName(botName.getText());
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

        panelSettings.add(saveSettings);    //кнопка сохранения

        frame.add(panelSettings, BorderLayout.WEST);
        frame.setVisible(true);

    }

}
