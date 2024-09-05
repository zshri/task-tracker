package org.example.tasktrackerbackend.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbackend.model.TaskStatus;


@Schema(description = "DTO для создания и обновления задачи")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 50, message = "Content must be between 1 and 50 characters")
    @Schema(description = "Заголовок задачи", example = "Задача 1", required = true)
    private String title;

//    @NotBlank(message = "Description cannot be blank")
//    @Size(min = 1, max = 1000, message = "Content must be between 10 and 1000 characters")
//    @Schema(description = "Описание задачи", example = "Это описание задачи 1", required = true)
    private String description;

    @NotNull(message = "Status cannot be null")
    @Schema(description = "Статус задачи", example = "PENDING", required = true)
    private TaskStatus status;

}
