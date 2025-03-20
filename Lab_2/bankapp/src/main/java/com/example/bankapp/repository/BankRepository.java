package com.example.bankapp.repository;

import com.example.bankapp.model.BankAccount;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class BankRepository {
    private final Map<Long, BankAccount> accounts = new HashMap<>();

    public BankAccount findById(Long id) {
        return accounts.get(id);
    }

    public void save(BankAccount account) {
        accounts.put(account.getId(), account);
    }

    public void delete(Long id) {
        accounts.remove(id);
    }
}
