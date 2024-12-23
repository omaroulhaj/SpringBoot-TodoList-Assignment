package com.project.todolist.controller;

import com.project.todolist.dto.TaskDto;
import com.project.todolist.model.Task;
import com.project.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tasks")
public class TaskController {
    @Autowired
    private TaskService taskService; // bean
    @GetMapping("/recent-to-old")
    public ResponseEntity<List<Task>> getUserTasksRecentToOld(@RequestBody String userId) {
        try {
            List<Task> tasks = taskService.getUserTasksRecentToOld(userId);
            return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 3. Create a new task
    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody TaskDto taskDto) {
        try {
            Task createdTask = taskService.createTask(taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 4. Get tasks by month and year
    @GetMapping("/by-month-year")
    public ResponseEntity<List<Task>> getTasksByMonthAndYear(
            @RequestParam String userId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<Task> tasks = taskService.getTasksByMonthAndYear(userId, year, month);
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 5. Update a task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestParam String userId,
            @RequestBody TaskDto taskDto) {
        try {
            Task updatedTask = taskService.updateTask(id, userId, taskDto);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 6. Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestParam String userId) {
        try {
            boolean isDeleted = taskService.deleteTask(id, userId);
            if (isDeleted) {
                return ResponseEntity.noContent().build(); // 204 - Suppression réussie.
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 - Tâche introuvable.
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 - Erreur serveur.
        }
    }

}
