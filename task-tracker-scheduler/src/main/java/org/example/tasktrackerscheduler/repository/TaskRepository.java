package org.example.tasktrackerscheduler.repository;


import org.example.tasktrackerscheduler.model.Task;
import org.example.tasktrackerscheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAuthor(User user);

}
