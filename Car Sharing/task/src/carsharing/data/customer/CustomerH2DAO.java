package carsharing.data.customer;

import carsharing.data.H2DB;
import carsharing.data.car.Car;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerH2DAO implements CustomerDAO {

    private final String DB_URL;

    public CustomerH2DAO(String dbFilename) {
        final String FILE_SEPARATOR = System.getProperty("file.separator");
        DB_URL = "jdbc:h2:." + FILE_SEPARATOR + "src"
                + FILE_SEPARATOR + "carsharing" + FILE_SEPARATOR + "db" + FILE_SEPARATOR + dbFilename;

    }


    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM CUSTOMER")) {
                    while (result.next()) {
                        Car rentedCar = null;
                        int carId = result.getInt("RENTED_CAR_ID");
                        if (!result.wasNull()) {
                            rentedCar = H2DB.carDAO.get(carId);
                        }
                        customers.add(new Customer(result.getInt("ID"), result.getString("NAME"), rentedCar));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return customers;
    }

    @Override
    public boolean add(Customer customer) {
        String insert = "INSERT INTO CUSTOMER (NAME) VALUES (?)";
        boolean added = false;

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);

            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, customer.getName());
                added = statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return added;

    }

    @Override
    public Customer get(int id) {
        Customer customer = null;
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM CUSTOMER WHERE ID=" + id)) {
                    result.next();
                    Car rentedCar = null;

                    int carId = result.getInt("RENTED_CAR_ID");
                    if (!result.wasNull()) {
                        rentedCar = H2DB.carDAO.get(carId);
                    }

                    return new Customer(result.getInt("ID"), result.getString("NAME"), rentedCar);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return customer;
    }

    @Override
    public boolean setRentedCar(Customer customer, Car car) {
        String update = "UPDATE CUSTOMER SET RENTED_CAR_ID=? WHERE ID=?";
        boolean added = false;

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);

            try (PreparedStatement statement = connection.prepareStatement(update)) {
                if (car == null) {
                    statement.setNull(1, Types.INTEGER);
                } else {
                    statement.setInt(1, car.getId());
                }

                statement.setInt(2, customer.getId());
                added = statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return added;
    }
}
