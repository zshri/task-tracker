package org.example.tasktrackerscheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.tasktrackerscheduler.component.EmailTaskProducer;
import org.example.tasktrackerscheduler.model.EmailTask;
import org.example.tasktrackerscheduler.model.Task;
import org.example.tasktrackerscheduler.model.User;
import org.example.tasktrackerscheduler.model.TaskStatus;
import org.example.tasktrackerscheduler.repository.TaskRepository;
import org.example.tasktrackerscheduler.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final EmailTaskProducer emailTaskProducer;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private static final int MAX_TASKS_TO_DISPLAY = 5;

    private Instant startOfDay;
    private Instant endOfDay;

     @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 */5 * * * *")
    public void generateAndSendReports() {
        startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
        endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        List<User> users = userRepository.findAll();
        users.forEach(this::processUserReports);
    }

    private void processUserReports(User user) {
        List<Task> tasks = taskRepository.findAllByAuthor(user);
        List<Task> incompleteTasks = filterTasksByStatus(tasks, TaskStatus.PENDING);
        List<Task> completedTodayTasks = filterTasksCompletedToday(tasks);

        EmailTask emailTask = createEmailTask(user, incompleteTasks, completedTodayTasks);
        if (emailTask != null) {
            emailTaskProducer.sendEmailTask(emailTask);
        }
    }

    private List<Task> filterTasksByStatus(List<Task> tasks, TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    private List<Task> filterTasksCompletedToday(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED 
                        && task.getUpdateAt() != null 
                        && task.getUpdateAt().isAfter(startOfDay) 
                        && task.getUpdateAt().isBefore(endOfDay))
                .collect(Collectors.toList());
    }

    private EmailTask createEmailTask(User user, List<Task> incompleteTasks, List<Task> completedTasks) {
        String subject = null;
        String body = null;

        if (!incompleteTasks.isEmpty() && !completedTasks.isEmpty()) {
            subject = "У вас осталось " + incompleteTasks.size() + " несделанных задач и вы выполнили " + completedTasks.size() + " задач";
            body = "Выполненные задачи:\n" + formatTasks(completedTasks) + "\n\nНесделанные задачи:\n" + formatTasks(incompleteTasks);
        } else if (!incompleteTasks.isEmpty()) {
            subject = "У вас осталось " + incompleteTasks.size() + " несделанных задач";
            body = "Несделанные задачи:\n" + formatTasks(incompleteTasks);
        } else if (!completedTasks.isEmpty()) {
            subject = "За сегодня вы выполнили " + completedTasks.size() + " задач";
            body = "Выполненные задачи:\n" + formatTasks(completedTasks);
        }

        return (subject != null && body != null) ? new EmailTask(user.getEmail(), subject, body) : null;
    }

    private String formatTasks(List<Task> tasks) {
        return tasks.stream()
                .limit(MAX_TASKS_TO_DISPLAY)
                .map(Task::getTitle)
                .collect(Collectors.joining("\n"));
    }
}
