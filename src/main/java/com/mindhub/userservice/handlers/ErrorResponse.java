package com.mindhub.userservice.handlers;

import org.springframework.http.HttpStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
}