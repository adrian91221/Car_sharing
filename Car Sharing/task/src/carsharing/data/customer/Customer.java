package carsharing.data.customer;

import carsharing.data.car.Car;

public class Customer {
    private int id;
    private final String name;
    private Car rentedCar;

    public Customer(String name) {
        this.name = name;
    }

    public Customer(int id, String name, Car rentedCar) {
        this.id = id;
        this.name = name;
        this.rentedCar = rentedCar;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Car getRentedCar() {
        return rentedCar;
    }
}
