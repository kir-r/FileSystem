package com.epam.filesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Определить полный путь заданного файла
 * Подсчитать количество файлов в заданном каталоге, включая вложенные файлы и каталоги
 * Подсчитать место, занимаое на диске содержимым заданного каталога
 * Найти в базе файлы по заданной маске с выдачей полного пути
 * Удалить файлы и каталоги заданного каталога
 * Переместить файлы и каталоги из одного в другой
 */

public class Controller {
    public static void operate() {
        try  {
            Main.logger.info("Чтобы определить полный путь заданного файла, нажмите 1\n" +
                    "Чтобы подсчитать количество файлов в заданном каталоге, включая вложенные файлы и каталоги, нажмите 2\n" +
                    "Чтобы подсчитать место, занимаемое на диске содержимым заданного каталога, нажмите 3\n" +
                    "Чтобы найти в базе файлы по заданной маске с выдачей полного пути, нажмите 4\n" +
                    "Чтобы удалить файлы и каталоги заданного каталога, нажмите 5\n" +
                    "Чтобы переместить файлы и каталоги из одного в другой, нажмите 6\n" +
                    "Для выхода нажмите 0");
            String userRequest;
            while (!(userRequest = Main.bufferedReader.readLine()).equals("0")) {
                switch (userRequest) {
                    case "1":
                        DAO.determineFullPathOfFile();
                        break;
                    case "2":
                        DAO.countNumberFilesInDirectory();
                        break;
                    case "3":
                        DAO.calculateDiskSpaceOccupiedByContentsOfDirectory();
                        break;
                    case "4":
                        DAO.findFilesInDatabaseByMask();
                        break;
                    case "5":
                        DAO.deleteFilesDirectoriesOfDirectory();
                        break;
                    case "6":
                        DAO.moveFilesDirectoriesFromOneToAnother();
                        break;
                }
            }
        } catch (IOException e) {
            Main.logger.error(e.getMessage());
        }
    }
}
