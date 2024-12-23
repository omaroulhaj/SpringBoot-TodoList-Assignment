package com.project.todolist.service;

import com.project.todolist.dto.TaskDto;
import com.project.todolist.model.Task;
import com.project.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // 2. Get tasks from recent to old
    public List<Task> getUserTasksRecentToOld(String userId) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }


    // 3. Create a new task
    public Task createTask(TaskDto taskDto) {
        if (taskDto.getUserId() == null || taskDto.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }

        Task task = Task.builder()
                .userId(taskDto.getUserId())
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return taskRepository.save(task);
    }


    // 4. Get tasks by month and year
    public List<Task> getTasksByMonthAndYear(String userId, int year, int month) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        // Convertir l'année et le mois en une plage de dates
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Appel du repository avec la plage de dates
        return taskRepository.findByUserIdAndCreatedAtBetween(userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59));
    }


    // 5. Update a task
    public Task updateTask(Long id, String userId, TaskDto taskDto) {
        if (taskDto.getName() == null || taskDto.getName().isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or blank.");
        }

        Optional<Task> taskOptional = taskRepository.findByIdAndUserId(id, userId);
        if (taskOptional.isEmpty()) {
            throw new IllegalArgumentException("Task not found for the given ID and user.");
        }

        Task task = taskOptional.get();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setCompleted(taskDto.isCompleted());
        task.setUpdatedAt(now());

        return taskRepository.save(task);
    }


    // 6. Delete a task
    public boolean deleteTask(Long id, String userId) {
        Optional<Task> taskOptional = taskRepository.findByIdAndUserId(id, userId);
        if (taskOptional.isEmpty()) {
            return false; // La tâche n'existe pas pour cet utilisateur.
        }

        taskRepository.deleteById(id);
        return true; // Suppression réussie.
    }

}
