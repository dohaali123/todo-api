package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.model.User;
import com.example.todo.model.UserType;
import com.example.todo.service.TaskService;
import com.example.todo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskControllerTests {

    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @Autowired
    private TaskController taskController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Helper class to handle exceptions globally in tests
    static class GlobalExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchElementException.class)
        public org.springframework.http.ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex) {
            return new org.springframework.http.ResponseEntity<>(ex.getMessage(), org.springframework.http.HttpStatus.NOT_FOUND);
        }

        @org.springframework.web.bind.annotation.ExceptionHandler(SecurityException.class)
        public org.springframework.http.ResponseEntity<String> handleSecurityException(SecurityException ex) {
            return new org.springframework.http.ResponseEntity<>(ex.getMessage(), org.springframework.http.HttpStatus.FORBIDDEN);
        }
    }

    @Test
    public void testGetTasksAsStandardUser() throws Exception {
        Long userId = 3L; // Assume userId 3 is a STANDARD user
        User standardUser = new User(userId, "Standard User A1", UserType.STANDARD, 1L);
        Task task = new Task(3L, "Task 3 for Standard User A1", false, userId);

        when(userService.getUserById(userId)).thenReturn(Optional.of(standardUser));
        when(taskService.getTasksForUser(standardUser)).thenReturn(Arrays.asList(task));

        mockMvc.perform(get("/tasks")
                .header("userId", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTasksAsCompanyAdmin() throws Exception {
        Long userId = 2L; // Assume userId 2 is a COMPANY_ADMIN
        User adminUser = new User(userId, "Admin A", UserType.COMPANY_ADMIN, 1L);
        Task task1 = new Task(2L, "Task 2 for Admin A", false, userId);
        Task task2 = new Task(3L, "Task 3 for Standard User A1", false, 3L);

        when(userService.getUserById(userId)).thenReturn(Optional.of(adminUser));
        when(taskService.getTasksForUser(adminUser)).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks")
                .header("userId", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTasksAsSuperUser() throws Exception {
        Long userId = 1L; // Assume userId 1 is a SUPER_USER
        User superUser = new User(userId, "Super User", UserType.SUPER_USER, null);
        Task task1 = new Task(1L, "Task 1 for Super User", false, userId);
        Task task2 = new Task(2L, "Task 2 for Admin A", false, 2L);
        Task task3 = new Task(3L, "Task 3 for Standard User A1", false, 3L);

        when(userService.getUserById(userId)).thenReturn(Optional.of(superUser));
        when(taskService.getTasksForUser(superUser)).thenReturn(Arrays.asList(task1, task2, task3));

        mockMvc.perform(get("/tasks")
                .header("userId", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateTaskAsStandardUser() throws Exception {
        Long userId = 3L; // STANDARD user
        User standardUser = new User(userId, "Standard User A1", UserType.STANDARD, 1L);
        Task newTask = new Task(null, "New Task for Standard User A1", false, userId);
        Task createdTask = new Task(7L, "New Task for Standard User A1", false, userId);

        when(userService.getUserById(userId)).thenReturn(Optional.of(standardUser));
        when(taskService.createTask(any(Task.class), eq(standardUser))).thenReturn(createdTask);

        mockMvc.perform(post("/tasks")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateTaskAsAdminUser() throws Exception {
        Long userId = 2L; // COMPANY_ADMIN
        Long taskId = 2L;
        User adminUser = new User(userId, "Admin A", UserType.COMPANY_ADMIN, 1L);
        Task updatedTask = new Task(taskId, "Updated Task Description", true, userId);

        when(userService.getUserById(userId)).thenReturn(Optional.of(adminUser));
        when(taskService.updateTask(eq(taskId), any(Task.class), eq(adminUser))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/{id}", taskId)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTaskAsStandardUserForbidden() throws Exception {
        Long userId = 3L; // STANDARD user
        Long taskId = 2L; // Task owned by Admin A

        User standardUser = new User(userId, "Standard User A1", UserType.STANDARD, 1L);

        when(userService.getUserById(userId)).thenReturn(Optional.of(standardUser));
        when(taskService.deleteTask(taskId, standardUser)).thenThrow(new SecurityException("Access denied"));

        mockMvc.perform(delete("/tasks/{id}", taskId)
                .header("userId", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteTaskNotFound() throws Exception {
        Long userId = 1L; // SUPER_USER
        Long taskId = 999L; // Non-existent task

        User superUser = new User(userId, "Super User", UserType.SUPER_USER, null);

        when(userService.getUserById(userId)).thenReturn(Optional.of(superUser));
        when(taskService.deleteTask(taskId, superUser)).thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(delete("/tasks/{id}", taskId)
                .header("userId", userId))
                .andExpect(status().isNotFound());
    }
}
