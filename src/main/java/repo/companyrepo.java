package com.example.todo.repository;

import com.example.todo.model.Company;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CompanyRepository {
    private final Map<Long, Company> companies = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // CRUD Methods
    public List<Company> findAll() {
        return new ArrayList<>(companies.values());
    }

    public Optional<Company> findById(Long id) {
        return Optional.ofNullable(companies.get(id));
    }

    public Company save(Company company) {
        if (company.getId() == null) {
            company.setId(idGenerator.getAndIncrement());
        }
        companies.put(company.getId(), company);
        return company;
    }

    public void deleteById(Long id) {
        companies.remove(id);
    }
}
