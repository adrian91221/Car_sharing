package carsharing.data;

import carsharing.data.car.CarDAO;
import carsharing.data.car.CarH2DAO;
import carsharing.data.company.CompanyDAO;
import carsharing.data.company.CompanyH2DAO;
import carsharing.data.customer.CustomerDAO;
import carsharing.data.customer.CustomerH2DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class H2DB {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String JDBC_DRIVER = "org.h2.Driver";

    public static CarDAO carDAO;
    public static CompanyDAO companyDAO;
    public static CustomerDAO customerDAO;

    private H2DB() {
    }

    public static void init(final String DB_FILENAME) {
        final String DB_URL = "jdbc:h2:." + FILE_SEPARATOR + "src"
                + FILE_SEPARATOR + "carsharing" + FILE_SEPARATOR + "db" + FILE_SEPARATOR + DB_FILENAME;

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS COMPANY" +
                        " (ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY, NAME VARCHAR(30) NOT NULL UNIQUE)");

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS CAR" +
                        " (ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY, NAME VARCHAR(30) NOT NULL UNIQUE, COMPANY_ID INT NOT NULL, FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))");

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS CUSTOMER" +
                        " (ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY, NAME VARCHAR(30) NOT NULL UNIQUE, RENTED_CAR_ID INT, FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))");

            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        carDAO = new CarH2DAO(DB_FILENAME);
        companyDAO = new CompanyH2DAO(DB_FILENAME);
        customerDAO = new CustomerH2DAO(DB_FILENAME);
    }
}
