package br.com.emendes.financesapi.validation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.persistence.NoResultException;
import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class NoResultExceptionHandler {

  @ExceptionHandler(NoResultException.class)
  public ResponseEntity<Problem> handleNoResultException(NoResultException exception) {
    Problem problem = Problem.builder()
        .withType(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/entity-not-found"))
        .withTitle("Entity not found")
        .withDetail(exception.getMessage())
        .withStatus(Status.NOT_FOUND)
        .with("timestamp", LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
