package br.com.emendes.financesapi.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.exception.DataConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ConflictExceptionHandler {

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ProblemDetail> handleDataConflictException(DataConflictException exception) {
    HttpStatus status = HttpStatus.CONFLICT;

    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/data-conflict"))
        .title("Data conflict")
        .detail(exception.getMessage())
        .status(status.value())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }
}