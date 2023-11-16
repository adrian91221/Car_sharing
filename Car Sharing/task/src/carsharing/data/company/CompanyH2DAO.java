package carsharing.data.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyH2DAO implements CompanyDAO {

    private final String DB_URL;

    public CompanyH2DAO(String dbFilename) {
        final String FILE_SEPARATOR = System.getProperty("file.separator");
        DB_URL = "jdbc:h2:." + FILE_SEPARATOR + "src"
                + FILE_SEPARATOR + "carsharing" + FILE_SEPARATOR + "db" + FILE_SEPARATOR + dbFilename;

    }


    @Override
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM COMPANY")) {
                    while (result.next()) {
                        companies.add(new Company(result.getInt("ID"), result.getString("NAME")));
                    }

                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return companies;
    }

    @Override
    public boolean add(Company company) {
        String insert = "INSERT INTO COMPANY (NAME) VALUES (?)";
        boolean added = false;

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);

            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setString(1, company.getName());
                added = statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return added;

    }


    @Override
    public Company get(int id) {
        Company company = null;
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM COMPANY WHERE ID=" + id)) {
                    while (result.next()) {
                        company = new Company(result.getInt("ID"), result.getString("NAME"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return company;
    }

}
