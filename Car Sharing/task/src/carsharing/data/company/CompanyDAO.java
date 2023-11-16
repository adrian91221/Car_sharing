package carsharing.data.company;

import java.util.List;

public interface CompanyDAO {
    List<Company> findAll();

    boolean add(Company company);

    Company get(int id);
}
