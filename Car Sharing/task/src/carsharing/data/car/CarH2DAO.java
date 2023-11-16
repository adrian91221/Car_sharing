package carsharing.data.car;

import carsharing.data.company.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarH2DAO implements CarDAO {
    private final String DB_URL;


    public CarH2DAO(String dbFilename) {
        final String FILE_SEPARATOR = System.getProperty("file.separator");
        DB_URL = "jdbc:h2:." + FILE_SEPARATOR + "src"
                + FILE_SEPARATOR + "carsharing" + FILE_SEPARATOR + "db" + FILE_SEPARATOR + dbFilename;
    }


    @Override
    public List<Car> findAll() {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM CAR")) {
                    while (result.next()) {
                        cars.add(new Car(result.getInt("ID"), result.getString("NAME"), result.getInt("COMPANY_ID")));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return cars;
    }

    public List<Car> findAll(Company company) {
        return findAll(company, false);
    }
    public List<Car> findAll(Company company, boolean availableOnly) {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                String select;
                if(availableOnly){
                    select = "SELECT * FROM CAR WHERE ID NOT IN(SELECT RENTED_CAR_ID FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL) AND COMPANY_ID=" + company.getId();
                } else {
                    select = "SELECT * FROM CAR WHERE COMPANY_ID=" + company.getId();
                }
                try (ResultSet result = statement.executeQuery(select)) {
                    while (result.next()) {
                        cars.add(new Car(result.getInt("ID"), result.getString("NAME"), result.getInt("COMPANY_ID")));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return cars;
    }

    @Override
    public boolean add(Car car) {
        String insert = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)";
        boolean added = false;

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);

            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, car.getName());
                statement.setInt(2, car.getCompanyId());
                added = statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return added;

    }

    @Override
    public Car get(int id) {
        Car car = null;
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM CAR WHERE ID=" + id)) {
                    while (result.next()) {
                        car = new Car(result.getInt("ID"), result.getString("NAME"), result.getInt("COMPANY_ID"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return car;
    }
}
