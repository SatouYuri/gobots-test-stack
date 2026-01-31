package dev.quatern.receiver.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public record HandledExceptionDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
    ) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HandledExceptionDTO> handleConstraintValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        HandledExceptionDTO error = new HandledExceptionDTO(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Validation error(s): " +
                String.join("; ",
                    Stream.concat(
                        e.getBindingResult().getFieldErrors().stream()
                            .map(err -> err.getField() + " - " + err.getDefaultMessage()),
                        e.getBindingResult().getGlobalErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                    ).toList()
                ),
            request.getRequestURI(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<HandledExceptionDTO> handleResponseStatus(ResponseStatusException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        HandledExceptionDTO error = new HandledExceptionDTO(
            e.getStatusCode().value(),
            ((HttpStatus) e.getStatusCode()).getReasonPhrase(),
            e.getReason(),
            request.getRequestURI(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HandledExceptionDTO> handleGeneric(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        HandledExceptionDTO error = new HandledExceptionDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Unexpected error occurred",
            request.getRequestURI(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
