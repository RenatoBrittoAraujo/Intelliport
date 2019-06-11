package com.example.intelliport;

public class User {

    private String name;
    private int number_of_vehicles;
    private int number_of_drivers;
    private int number_of_deliveries;

    private double balance;
    private double expected_profit;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public User(String name) {
        this.name = name;
        number_of_deliveries = 0;
        number_of_drivers = 0;
        number_of_vehicles = 0;
        balance = 0.0;
        expected_profit = 0.0;
    }


}
