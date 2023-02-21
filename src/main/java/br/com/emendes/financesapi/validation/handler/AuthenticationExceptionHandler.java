package br.com.emendes.financesapi.validation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Problem> handleAuthenticationException(AuthenticationException exception) {
    Problem problem = Problem.builder()
        .withType(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/bad-credentials"))
        .withTitle("Bad credentials")
        .withDetail("Invalid email or password")
        .withStatus(Status.BAD_REQUEST)
        .with("timestamp", LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
