package org.example.tasktrackerbackend.repository;

import org.example.tasktrackerbackend.model.Task;
import org.example.tasktrackerbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>  {

    List<Task> findAllByAuthor(User user);

    Optional<Task> findByIdAndAuthor(Long id, User author);
}
