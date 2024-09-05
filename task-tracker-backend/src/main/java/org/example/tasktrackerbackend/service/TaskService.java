package org.example.tasktrackerbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbackend.exception.TaskNotFoundException;
import org.example.tasktrackerbackend.model.Task;
import org.example.tasktrackerbackend.model.User;
import org.example.tasktrackerbackend.model.dto.TaskRequest;
import org.example.tasktrackerbackend.model.dto.TaskResponse;
import org.example.tasktrackerbackend.repository.TaskRepository;
import org.example.tasktrackerbackend.util.TaskMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


    public List<TaskResponse> getTaskList(User user) {
        return taskRepository.findAllByAuthor(user).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse save(TaskRequest taskRequest, User user) {

        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatus())
                .author(user)
                .build();

        Task save = taskRepository.save(task);
        log.info("Task with id {} save", save.getId());

        return taskMapper.toDto(save);
    }

    public TaskResponse update(Long taskId, TaskRequest taskRequest, User user) throws TaskNotFoundException {
        Task task = taskRepository.findByIdAndAuthor(taskId, user)
                .orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));
        
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    public void delete(Long taskId, User user) throws TaskNotFoundException {
        Task task = taskRepository.findByIdAndAuthor(taskId, user)
                .orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));
        taskRepository.delete(task);
        log.info("Task with id {} deleted", taskId);
    }
}
