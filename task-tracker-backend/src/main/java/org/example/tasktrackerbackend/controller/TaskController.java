package org.example.tasktrackerbackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbackend.exception.TaskNotFoundException;
import org.example.tasktrackerbackend.exception.UserNotFoundException;
import org.example.tasktrackerbackend.model.User;
import org.example.tasktrackerbackend.model.dto.TaskRequest;
import org.example.tasktrackerbackend.model.dto.TaskResponse;
import org.example.tasktrackerbackend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(@AuthenticationPrincipal User user) throws UserNotFoundException {
        return ResponseEntity.ok(taskService.getTaskList(user));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest,
                                                      @AuthenticationPrincipal User user)  {
        return ResponseEntity.ok(taskService.save(taskRequest, user));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId,
                                                   @Valid @RequestBody TaskRequest taskRequest,
                                                   @AuthenticationPrincipal User user) throws TaskNotFoundException {
        return ResponseEntity.ok(taskService.update(taskId, taskRequest, user));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId,
                                       @AuthenticationPrincipal User user) throws TaskNotFoundException {
        taskService.delete(taskId, user);
        return ResponseEntity.noContent().build();
    }
}
