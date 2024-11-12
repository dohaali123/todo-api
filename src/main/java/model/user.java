package com.example.todo.model;

public class User {
    private Long id;
    private String name;
    private UserType userType; // ENUM: STANDARD, COMPANY_ADMIN, SUPER_USER
    private Long companyId;     // References a Company

    // Constructors
    public User() {
    }

    public User(Long id, String name, UserType userType, Long companyId) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.companyId = companyId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserType getUserType() {
        return userType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
