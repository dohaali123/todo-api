package com.example.todo.service;

import com.example.todo.model.Company;
import com.example.todo.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // CRUD Operations
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Long id, Company updatedCompany) {
        Optional<Company> optionalCompany = companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            Company existingCompany = optionalCompany.get();
            existingCompany.setName(updatedCompany.getName());
            return companyRepository.save(existingCompany);
        } else {
            throw new NoSuchElementException("Company not found with id: " + id);
        }
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
