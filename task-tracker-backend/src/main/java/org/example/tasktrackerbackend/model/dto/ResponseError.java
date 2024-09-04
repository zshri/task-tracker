package org.example.tasktrackerbackend.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO для возврата ошибки")
@Data
public class ResponseError {
    private int status;
    private String message;
    private String error;
    private String timestamp;
}