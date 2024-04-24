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
        botToken.setEditable(true);
        botToken.setText(settings.getBotToken()); //загружаем токен
        JButton saveBotToken = new JButton("Сохранить токен");
        saveBotToken.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settings.setBotToken(botToken.getText());    //записываем токен
            }
        });

        //имя бота
        final JTextField botName = new JTextField(40);
        botName.setEditable(true);
        botName.setText(settings.getBotName()); //загружаем имя
        JButton saveBotName = new JButton("Сохранить имя");
        saveBotName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settings.setBotName(botName.getText());    //записываем имя
            }
        });


        panelSettings.add(addExcelFile);
        panelSettings.add(excelAddress, "wrap");
        panelSettings.add(addPhotoAddress);
        panelSettings.add(photoAddress, "wrap");
        panelSettings.add(saveBotToken);
        panelSettings.add(botToken, "wrap");
        panelSettings.add(saveBotName);
        panelSettings.add(botName, "wrap");

        frame.add(panelSettings, BorderLayout.WEST);
        frame.setVisible(true);

    }

}
