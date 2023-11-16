package carsharing;

import carsharing.data.*;
import carsharing.data.car.*;
import carsharing.data.company.*;
import carsharing.data.customer.Customer;
import carsharing.data.customer.CustomerDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner keyboard = new Scanner(System.in);

    private static CarDAO carDAO;
    private static CompanyDAO companyDAO;
    private static CustomerDAO customerDAO;

    public static void main(String[] args) {
        String DB_FILENAME;
        if (args.length > 1) {
            if (args[0].equals("-databaseFileName")) {
                DB_FILENAME = args[1];
            } else {
                DB_FILENAME = "carsharing.mv.db";
            }
        } else {
            DB_FILENAME = "carsharing.mv.db";
        }

        dbInit(DB_FILENAME);

        menu();
    }

    private static void dbInit(String dbFilename) {
        H2DB.init(dbFilename);
        carDAO = H2DB.carDAO;
        companyDAO = H2DB.companyDAO;
        customerDAO = H2DB.customerDAO;
    }

    private static void menu() {
        while (true) {
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            System.out.println("0. Exit");
            System.out.print("> ");

            int in = keyboard.nextInt();
            System.out.println();

            if (in == 0) {
                break;
            } else if (in == 1) {
                managerMenu();
            } else if (in == 2) {
                Customer customer = selectCustomer();
                if (customer != null) {
                    customerMenu(customer);
                }
            } else if (in == 3) {
                createCustomer();
            }
        }
    }

    private static Customer selectCustomer() {
        List<Customer> customers = customerDAO.findAll();

        if (customers.isEmpty()) {
            System.out.printf("The customer list is empty!%n%n");
        } else {
            System.out.println("Choose a customer:");
            for (int i = 0; i < customers.size(); i++) {
                System.out.println(i + 1 + ". " + customers.get(i).getName());
            }
            printBackAndPrompt();


            int in = keyboard.nextInt();
            System.out.println();
            if (in > 0 && in <= customers.size()) {
                return customers.get(in - 1);
            }
        }

        return null;
    }

    private static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            printBackAndPrompt();
            int in = keyboard.nextInt();
            if (in == 0) {
                return;
            } else if (in == 1) {
                rentCar(customer);
            } else if (in == 2) {
                returnRentedCar(customer);
            } else if (in == 3) {
                printRentedCarData(customer);
            }
            customer = customerDAO.get(customer.getId());
        }
    }

    private static void printRentedCarData(Customer customer) {
        Car car = customer.getRentedCar();
        if (car == null) {
            System.out.printf("%nYou didn't rent a car!%n%n");
            return;
        }

        System.out.println("Your rented car:");
        System.out.println(car.getName());
        System.out.println("Company:");
        System.out.println(companyDAO.get(car.getCompanyId()).getName());
        System.out.println();
    }

    private static void returnRentedCar(Customer customer) {
        if (customer.getRentedCar() == null) {
            System.out.printf("%nYou didn't rent a car!%n%n");
            return;
        }

        boolean success = customerDAO.setRentedCar(customer, null);
        if (success) {
            System.out.print("%nYou've returned a rented car!%n%n");
        }
    }

    private static void rentCar(Customer customer) {
        if (customer.getRentedCar() != null) {
            System.out.printf("%nYou've already rented a car!%n%n");
            return;
        }

        Company company = selectCompany();
        if (company == null) return;
        List<Car> cars = carDAO.findAll(company, true);
        if (cars.isEmpty()) {
            System.out.println("No available cars in the 'Company name' company");
        }

        Car car = selectCar(company);
        if (car == null) return;
        boolean success = customerDAO.setRentedCar(customer, car);
        if (success) {
            System.out.printf("You rented '" + car + "'%n");
        }
    }

    private static void createCustomer() {
        System.out.println("Enter the customer name:");
        System.out.print("> ");

        String name = readLine();
        if (name != null) {
            if (customerDAO.add(new Customer(name))) {
                System.out.printf("The customer was added!%n%n");
            }
        }
    }


    private static void managerMenu() {
        while (true) {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            printBackAndPrompt();

            int in = keyboard.nextInt();
            System.out.println();

            if (in == 0) {
                return;
            } else if (in == 1) {
                Company company = selectCompany();
                if (company != null) {
                    System.out.println("'" + company.getName() + "'" + " company");
                    companyMenu(company);
                }
            } else if (in == 2) {
                createCompany();
            }
        }
    }

    private static void createCompany() {
        System.out.println("Enter the company name:");
        System.out.print("> ");

        String name = readLine();
        if (name != null) {
            if (companyDAO.add(new Company(name))) {
                System.out.printf("The company was created!%n%n");
            }
        }
    }


    private static Company selectCompany() {
        List<Company> companies = companyDAO.findAll();

        if (companies.isEmpty()) {
            System.out.printf("The company list is empty!%n%n");
        } else {
            System.out.println("Choose a company:");
            for (Company company : companies) {
                System.out.println(company.getId() + ". " + company.getName());
            }
            printBackAndPrompt();


            int in = keyboard.nextInt();
            System.out.println();
            if (in > 0 && in <= companies.size()) {
                return companies.get(in - 1);
            }
        }
        return null;
    }

    private static void companyMenu(Company company) {
        while (true) {
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            printBackAndPrompt();
            int in = keyboard.nextInt();
            if (in == 0) {
                return;
            } else if (in == 1) {
                listCars(company);
                System.out.println();
            } else if (in == 2) {
                createCar(company);
            }
        }
    }

    private static void listCars(Company company) {
        List<Car> cars = carDAO.findAll(company);
        listCars(cars);
    }

    private static void listCars(List<Car> cars) {
        if (cars.isEmpty()) {
            System.out.printf("%nThe car list is empty!%n");
        } else {
            System.out.printf("%nCar list:%n");
            for (int i = 0; i < cars.size(); i++) {
                System.out.println(i + 1 + ". " + cars.get(i).getName());
            }
        }
    }

    private static Car selectCar(Company company) {
        List<Car> cars = carDAO.findAll(company, true);

        listCars(cars);
        printBackAndPrompt();

        int in = keyboard.nextInt();
        System.out.println();
        if (in > 0 && in <= cars.size()) {
            return cars.get(in - 1);
        }

        return null;
    }

    private static void createCar(Company company) {
        System.out.println("Enter the car name:");
        System.out.print("> ");

        String name = readLine();
        if (name != null) {
            if (carDAO.add(new Car(name, company.getId()))) {
                System.out.printf("The car was added!%n%n");
            }
        }
    }

    private static void printBackAndPrompt() {
        System.out.println("0. Back");
        System.out.print("> ");
    }

    private static String readLine() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String name = null;
        try {
            name = reader.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return name;
    }
}