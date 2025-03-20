package com.example.bankapp.service;

import com.example.bankapp.model.BankAccount;
import com.example.bankapp.repository.BankRepository;
import org.springframework.stereotype.Service;

@Service
public class BankService {
    private final BankRepository repository;

    public BankService(BankRepository repository) {
        this.repository = repository;
    }

    public synchronized BankAccount createAccount(Long id, String name, double balance) {
        BankAccount account = new BankAccount(id, name, balance);
        repository.save(account);
        return account;
    }

    public synchronized BankAccount getAccount(Long id) {
        return repository.findById(id);
    }

    public synchronized void deposit(Long id, double amount) {
        BankAccount account = repository.findById(id);
        if (account != null) {
            account.setBalance(account.getBalance() + amount);
            repository.save(account);
        }
    }

    public synchronized boolean withdraw(Long id, double amount) {
        BankAccount account = repository.findById(id);
        if (account != null && account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            repository.save(account);
            return true;
        }
        return false;
    }

    public synchronized void deleteAccount(Long id) {
        repository.delete(id);
    }
}
