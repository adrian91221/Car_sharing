package carsharing.data.customer;

import carsharing.data.car.Car;

import java.util.List;

public interface CustomerDAO {
    List<Customer> findAll();

    boolean add(Customer customer);

    boolean setRentedCar(Customer customer, Car car);

    Customer get(int id);
}
