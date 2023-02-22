package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException exception) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/bad-credentials"))
        .title("Bad credentials")
        .detail("Invalid email or password")
        .status(status.value())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
