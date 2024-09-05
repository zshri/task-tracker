package org.example.tasktrackerbackend.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbackend.model.TaskStatus;

import java.time.Instant;


@Schema(description = "DTO для возврата задачи")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant createAt;
    private Instant updateAt;

}
