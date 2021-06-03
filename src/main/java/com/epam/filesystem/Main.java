package com.epam.filesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**Практика
 * Установить H2 DataBase использовать in file вариант
 * https://www.h2database.com/html/main.html
 * Создать базу данных, SQL скрипты должны быть в ресурсах проекта, для обновления необходимо использовать flyway
 * https://flywaydb.org/
 * Создать объектную модель
 * Создать бизнес логику и консольный интерфейс
 * Задача Файловая система:
 *
 * В БД хранится информация о дереве каталогов файловой систесы - каталоги, подкаталоги, файлы.
 * Для каталогов необходимо хранить:
 * родительский каталог
 * название
 * Для файлов необходимо хранить:
 * родительский каталог
 * название
 * место, занимаемое на диске
 *
 * Методы:
 * Определить полный путь заданного файла
 * Подсчитать количество файлов в заданном каталоге, включая вложенные файлы и каталоги
 * Подсчитать место, занимаое на диске содержимым заданного каталога
 * Найти в базе файлы по заданной маске с выдачей полного пути
 * Удалить файлы и каталоги заданного каталога
 * Переместить файлы и каталоги из одного в другой*/

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        Connector.connect();
        Connector.createDB();
        Controller.operate();
        bufferedReader.close();
        Connector.closeConnection();
    }
}



