package com.example.todo.repository;

import com.example.todo.model.Task;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // CRUD Methods
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public void deleteById(Long id) {
        tasks.remove(id);
    }
}
