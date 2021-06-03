package com.epam.filesystem;

import java.io.IOException;
import java.sql.*;
import java.util.Stack;

/** Определить полный путь заданного файла
 * Подсчитать количество файлов в заданном каталоге, включая вложенные файлы и каталоги
 * Подсчитать место, занимаемое на диске содержимым заданного каталога
 * Найти в базе файлы по заданной маске с выдачей полного пути
 * Удалить файлы и каталоги заданного каталога
 * Переместить файлы и каталоги из одного в другой
 */

public class DAO {

    public static void determineFullPathOfFile() { //1
        try {
            Main.logger.info("Введите имя файла");
            String fileName = Main.bufferedReader.readLine();
            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            Stack<String> stack = new Stack<>();
            StringBuilder absolutePath = new StringBuilder();

            ResultSet resultSet = statement.executeQuery("SELECT ParentCatalogueID, Name FROM Files WHERE Name LIKE '"
                    + fileName + "'");
            if (resultSet.next()) {
                stack.add(resultSet.getString(1));
                stack.add(resultSet.getString(2));
                resultSet.previous();
                while (resultSet.next()) {
                    String currentCatalog = resultSet.getString(1);
                    System.out.println("currentCatalog " + currentCatalog);
                    stack.add(currentCatalog);
                    resultSet = statement.executeQuery("SELECT ParentCatalogueID, Name FROM Catalogue WHERE Name = '"
                            + currentCatalog + "'");
                }
                stack.pop();
                while (!stack.isEmpty()) {
                    absolutePath.append(stack.pop()).append("\\");
                }

                System.out.println("absolutePath " + absolutePath.toString());
            } else System.out.println("There is no data");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void countNumberFilesInDirectory() { //2
        int numberOfFilesInDirectory = 0;
        int ID = 0;
        Main.logger.info("Введите имя каталога");
        try {
            String directoryName = Main.bufferedReader.readLine();
            ID = getCatalogueIDByName(directoryName);

            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Files WHERE ParentCatalogueID=" + ID);
            while (resultSet.next()) {
                System.out.println("Файл: " + resultSet.getString("Name"));
                numberOfFilesInDirectory++;
            }
            System.out.println("Количество файлов: " + numberOfFilesInDirectory);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void calculateDiskSpaceOccupiedByContentsOfDirectory() { //3
        int spaceOccupiedByContentsOfDirectory = 0;
        int ID = 0;
        Main.logger.info("Введите имя каталога");
        try {
            String directoryName = Main.bufferedReader.readLine();
            ID = getCatalogueIDByName(directoryName);

            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT SUM(Size) FROM Files WHERE ParentCatalogueID=" + "'" + ID + "'");
            resultSet.next();
            spaceOccupiedByContentsOfDirectory = resultSet.getInt(1);
            System.out.println("Место, занимаемое на диске: " + spaceOccupiedByContentsOfDirectory);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void findFilesInDatabaseByMask() { //4
        Main.logger.info("Введите имя файла");
        try {
            String fileName = Main.bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder(fileName);
            stringBuilder.setCharAt(4, '*');
            fileName = stringBuilder.toString();

            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            Stack<String> stack = new Stack<>();
            StringBuilder absolutePath = new StringBuilder();

            ResultSet resultSet = statement.executeQuery("SELECT ParentCatalogueID, Name FROM Files WHERE Name REGEXP '"
                    + fileName + "'");
            if (resultSet.next()) {
                stack.add(resultSet.getString(1));
                stack.add(resultSet.getString(2));
                resultSet.previous();
                while (resultSet.next()) {
                    String currentCatalog = resultSet.getString(1);
                    System.out.println("currentCatalog " + currentCatalog);
                    stack.add(currentCatalog);
                    resultSet = statement.executeQuery("SELECT ParentCatalogueID, Name FROM Catalogue WHERE Name = '"
                            + currentCatalog + "'");
                }
                stack.pop();
                while (!stack.isEmpty()) {
                    absolutePath.append(stack.pop()).append("\\");
                }

                System.out.println("absolutePath " + absolutePath.toString());
            } else System.out.println("There is no data");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFilesDirectoriesOfDirectory() { //5
        int ID = 0;
        Main.logger.info("Введите имя каталога");
        try {
            String directoryName = Main.bufferedReader.readLine();
            ID = getCatalogueIDByName(directoryName);

            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int amountDeletedFiles = statement.executeUpdate("DELETE FROM Files WHERE ParentCatalogueID=" + "'" + ID + "'");
            System.out.println("Удалено файлов: " + amountDeletedFiles);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void moveFilesDirectoriesFromOneToAnother() { //6
        try {
            Main.logger.info("Введите имя первого каталога");
            String firstCatalogue = Main.bufferedReader.readLine();
            Main.logger.info("Введите имя второго каталога");
            String secondCatalogue = Main.bufferedReader.readLine();
            String temporaryName = "Temporary";

            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.executeUpdate("ALTER TABLE " + firstCatalogue +
                    " RENAME TO " + temporaryName);
            statement.executeUpdate("ALTER TABLE " + secondCatalogue +
                    " RENAME TO " + firstCatalogue);
            statement.executeUpdate("ALTER TABLE " + temporaryName +
                    " RENAME TO " + secondCatalogue);

            statement.executeUpdate("UPDATE " + firstCatalogue +
                    " SET Name = '" + firstCatalogue + "'");
            statement.executeUpdate("UPDATE " + secondCatalogue +
                    " SET Name = '" + secondCatalogue + "'");


            statement = Connector.connection.createStatement();
            String resultSQL = "SELECT * FROM " + firstCatalogue;
            ResultSet resultSet = statement.executeQuery(resultSQL);

            while (resultSet.next()) {
                System.out.println(resultSet.getInt("ID") + ", "
                        + resultSet.getInt("ParentCatalogueID")
                        + ", " + resultSet.getString("Name"));
            }

            statement = Connector.connection.createStatement();
            String resultSQL2 = "SELECT * FROM " + secondCatalogue;
            ResultSet resultSet2 = statement.executeQuery(resultSQL2);

            while (resultSet2.next()) {
                System.out.println(resultSet2.getInt("ID") + ", "
                        + resultSet2.getInt("ParentCatalogueID")
                        + ", " + resultSet2.getString("Name"));
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getCatalogueIDByName(String directoryName) {
        int ID = 0;
        try {
            Statement statement = Connector.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Catalogue WHERE Name=" + "'" + directoryName + "'");
            while (resultSet.next()) {
                ID = resultSet.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ID;
    }
}

















