package com.example.todo;

import com.example.todo.model.Company;
import com.example.todo.model.Task;
import com.example.todo.model.User;
import com.example.todo.model.UserType;
import com.example.todo.repository.CompanyRepository;
import com.example.todo.repository.TaskRepository;
import com.example.todo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TodoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    // Preload some data for testing
    @Bean
    CommandLineRunner loadData(UserRepository userRepository,
                               CompanyRepository companyRepository,
                               TaskRepository taskRepository) {
        return args -> {
            // Create Companies
            Company companyA = new Company(null, "Company A");
            Company companyB = new Company(null, "Company B");
            companyRepository.save(companyA);
            companyRepository.save(companyB);

            // Create Users
            User superUser = new User(null, "Super User", UserType.SUPER_USER, null);
            User adminA = new User(null, "Admin A", UserType.COMPANY_ADMIN, companyA.getId());
            User standardUserA1 = new User(null, "Standard User A1", UserType.STANDARD, companyA.getId());
            User standardUserA2 = new User(null, "Standard User A2", UserType.STANDARD, companyA.getId());
            User adminB = new User(null, "Admin B", UserType.COMPANY_ADMIN, companyB.getId());
            User standardUserB1 = new User(null, "Standard User B1", UserType.STANDARD, companyB.getId());

            userRepository.save(superUser);
            userRepository.save(adminA);
            userRepository.save(standardUserA1);
            userRepository.save(standardUserA2);
            userRepository.save(adminB);
            userRepository.save(standardUserB1);

            // Create Tasks
            Task task1 = new Task(null, "Task 1 for Super User", false, superUser.getId());
            Task task2 = new Task(null, "Task 2 for Admin A", false, adminA.getId());
            Task task3 = new Task(null, "Task 3 for Standard User A1", false, standardUserA1.getId());
            Task task4 = new Task(null, "Task 4 for Standard User A2", true, standardUserA2.getId());
            Task task5 = new Task(null, "Task 5 for Admin B", false, adminB.getId());
            Task task6 = new Task(null, "Task 6 for Standard User B1", true, standardUserB1.getId());

            taskRepository.save(task1);
            taskRepository.save(task2);
            taskRepository.save(task3);
            taskRepository.save(task4);
            taskRepository.save(task5);
            taskRepository.save(task6);
        };
    }
}
