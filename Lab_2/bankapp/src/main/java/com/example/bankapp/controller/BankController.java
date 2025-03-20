package com.example.bankapp.controller;

import com.example.bankapp.model.BankAccount;
import com.example.bankapp.service.BankService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class BankController {
    private final BankService service;

    public BankController(BankService service) {
        this.service = service;
    }

    @PostMapping
    public BankAccount createAccount(@RequestBody BankAccount account) {
        return service.createAccount(account.getId(), account.getName(), account.getBalance());
    }

    @GetMapping("/{id}")
    public BankAccount getAccount(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @PutMapping("/{id}/deposit")
    public String deposit(@PathVariable Long id, @RequestParam double amount) {
        service.deposit(id, amount);
        return "Deposit successful!";
    }

    @PutMapping("/{id}/withdraw")
    public String withdraw(@PathVariable Long id, @RequestParam double amount) {
        boolean success = service.withdraw(id, amount);
        return success ? "Withdrawal successful!" : "Insufficient balance!";
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        service.deleteAccount(id);
        return "Account deleted successfully!";
    }
}
