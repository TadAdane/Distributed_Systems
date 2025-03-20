package com.example.bankapp.model;

public class BankAccount {
    private Long id;
    private String name;
    private double balance;

    // Constructors
    public BankAccount() {}

    public BankAccount(Long id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
