package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.model.User;
import com.example.todo.service.TaskService;
import com.example.todo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // Helper method to get User from userId header
    private User getUserFromHeader(Long userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    // GET /tasks - Get all tasks accessible to the user
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@RequestHeader("userId") Long userId) {
        try {
            User user = getUserFromHeader(userId);
            List<Task> tasks = taskService.getTasksForUser(user);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET /tasks/{id} - Get a specific task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@RequestHeader("userId") Long userId,
                                           @PathVariable Long id) {
        try {
            User user = getUserFromHeader(userId);
            Task task = taskService.getTasksForUser(user).stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST /tasks - Create a new task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestHeader("userId") Long userId,
                                           @RequestBody Task task) {
        try {
            User user = getUserFromHeader(userId);
            Task createdTask = taskService.createTask(task, user);
            return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /tasks/{id} - Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@RequestHeader("userId") Long userId,
                                           @PathVariable Long id,
                                           @RequestBody Task task) {
        try {
            User user = getUserFromHeader(userId);
            Task updatedTask = taskService.updateTask(id, task, user);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /tasks/{id} - Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@RequestHeader("userId") Long userId,
                                           @PathVariable Long id) {
        try {
            User user = getUserFromHeader(userId);
            taskService.deleteTask(id, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Exception Handlers
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
