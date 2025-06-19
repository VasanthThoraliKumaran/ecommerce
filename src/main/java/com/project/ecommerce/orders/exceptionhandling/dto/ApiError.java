package com.project.ecommerce.orders.exceptionhandling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private HttpStatus status;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String path; // optional: API path where error occurred

    public ApiError(HttpStatus status, String message, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}
