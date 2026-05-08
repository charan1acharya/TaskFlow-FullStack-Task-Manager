package com.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

@RestController
@RequestMapping("/tasks")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService service;

    @GetMapping
    public List<Task> getTasks() {
        return service.getAllTasks();
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        return service.saveTask(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
    }
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask){

        Task existingTask = service.getTaskById(id);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());

        return service.saveTask(existingTask);
    }
    
}