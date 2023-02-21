package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.validation.exception.PasswordsDoNotMatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class PasswordsDoNotMatchExceptionHandler {

  @ExceptionHandler(PasswordsDoNotMatchException.class)
  public ResponseEntity<Problem> handlePasswordsDoNotMatchException(PasswordsDoNotMatchException exception) {
    Problem problem = Problem.builder()
        .withType(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/passwords-do-not-match"))
        .withTitle("Passwords do not match")
        .withDetail(exception.getMessage())
        .withStatus(Status.BAD_REQUEST)
        .with("timestamp", LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }
}
