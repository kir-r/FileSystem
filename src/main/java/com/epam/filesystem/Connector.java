package com.epam.filesystem;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class Connector {
    static Properties properties = new Properties();
    static String URL;
    static String USER;
    static String PASSWORD;
    static Connection connection;

    public static void readProperties() {
        try (FileReader fileReader = new FileReader("src\\main\\resources\\configuration.properties")) {
            properties.load(fileReader);
            URL = properties.getProperty("URL");
            USER = properties.getProperty("USER");
            PASSWORD = properties.getProperty("PASSWORD");
        } catch (IOException e) {
            Main.logger.error(e.getMessage());
        }
    }

    public static void connect() {
        readProperties();
        Driver driver = new org.h2.Driver();
        try {
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            Main.logger.error(e.getMessage());
        }
    }

    public static void createDB() {
        Statement statement;
        try {
            statement = connection.createStatement();

            String sqlCatalogue = "CREATE TABLE Catalogue (\n" +
                    "    ID int,\n" +
                    "    ParentCatalogueID int,\n" +
                    "    Name varchar(255)\n" +
                    ");";
            String sqlFiles = "CREATE TABLE Files (\n" +
                    "    ID int,\n" +
                    "    ParentCatalogueID int,\n" +
                    "    Name varchar(255),\n" +
                    "    Size int\n" +
                    ");";

            statement.execute(sqlCatalogue);
            statement.execute(sqlFiles);

            statement = connection.createStatement();

            String valuesCatalogue = "INSERT INTO CATALOGUE VALUES (111, 222, 'catalogue')";
            String valuesFiles = "INSERT INTO FILES VALUES (333, 111, 'file1', 123)";
            String valuesFiles2 = "INSERT INTO FILES VALUES (444, 111, 'file2', 322)";

            statement.execute(valuesCatalogue);
            statement.execute(valuesFiles);
            statement.execute(valuesFiles2);

            statement = connection.createStatement();
            String resultSQL = "SELECT * FROM CATALOGUE";
            ResultSet resultSet = statement.executeQuery(resultSQL);

            while (resultSet.next()) {
                System.out.println(resultSet.getInt("ID") + ", "
                        + resultSet.getInt("ParentCatalogueID")
                        + ", " + resultSet.getString("Name"));
            }

            statement = connection.createStatement();
            String resultSQL2 = "SELECT * FROM FILES";
            ResultSet resultSet2 = statement.executeQuery(resultSQL2);

            while (resultSet2.next()) {
                System.out.println(resultSet2.getInt("ID") + ", "
                        + resultSet2.getInt("ParentCatalogueID")
                        + ", " + resultSet2.getString("Name"));
            }
        } catch (SQLException e) {
            Main.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            Main.logger.error(e.getMessage());
        }
    }
}
