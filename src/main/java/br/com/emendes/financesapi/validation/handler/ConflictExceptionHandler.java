package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.validation.exception.DataConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ConflictExceptionHandler {

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<Problem> handleDataConflictException(DataConflictException exception) {
    Problem problem = Problem.builder()
        .withType(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/data-conflict"))
        .withTitle("Data conflict")
        .withDetail(exception.getMessage())
        .withStatus(Status.CONFLICT)
        .with("timestamp", LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }
}