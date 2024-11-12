package com.example.todo.service;

import com.example.todo.model.Task;
import com.example.todo.model.User;
import com.example.todo.model.UserType;
import com.example.todo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Get tasks based on user role
    public List<Task> getTasksForUser(User user) {
        List<Task> allTasks = taskRepository.findAll();
        switch (user.getUserType()) {
            case SUPER_USER:
                return allTasks;
            case COMPANY_ADMIN:
                return allTasks.stream()
                        .filter(task -> task.getUserId().equals(user.getId())
                                || (user.getCompanyId() != null && task.getUserId().equals(user.getCompanyId())))
                        .collect(Collectors.toList());
            case STANDARD:
                return allTasks.stream()
                        .filter(task -> task.getUserId().equals(user.getId()))
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedOperationException("Unsupported user type: " + user.getUserType());
        }
    }

    // Create a new task
    public Task createTask(Task task, User user) {
        task.setUserId(user.getId());
        return taskRepository.save(task);
    }

    // Update an existing task
    public Task updateTask(Long id, Task updatedTask, User user) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));

        // Check access
        if (!canAccessTask(user, existingTask)) {
            throw new SecurityException("Access denied to update task with id: " + id);
        }

        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());
        return taskRepository.save(existingTask);
    }

    // Delete a task
    public void deleteTask(Long id, User user) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));

        // Check access
        if (!canAccessTask(user, existingTask)) {
            throw new SecurityException("Access denied to delete task with id: " + id);
        }

        taskRepository.deleteById(id);
    }

    // Helper method to check if a user can access a task
    private boolean canAccessTask(User user, Task task) {
        switch (user.getUserType()) {
            case SUPER_USER:
                return true;
            case COMPANY_ADMIN:
                // Assuming companyId represents the company, tasks belong to users in the same company
                return user.getCompanyId() != null && user.getCompanyId().equals(getUserCompanyId(task.getUserId()));
            case STANDARD:
                return task.getUserId().equals(user.getId());
            default:
                return false;
        }
    }

    // Placeholder for getting user's company ID
    // In a real application, you would fetch the user's company from UserRepository or similar
    private Long getUserCompanyId(Long userId) {
        // For simplicity, assuming user's companyId is the same as userId
        // Replace with actual logic
        return userId;
    }
}
