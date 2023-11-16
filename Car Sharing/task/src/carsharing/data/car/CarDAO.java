package carsharing.data.car;

import carsharing.data.company.Company;

import java.util.List;

public interface CarDAO {
    List<Car> findAll();
    List<Car> findAll(Company company);
    List<Car> findAll(Company company, boolean availableOnly);
    Car get(int id);
    boolean add(Car car);
}
