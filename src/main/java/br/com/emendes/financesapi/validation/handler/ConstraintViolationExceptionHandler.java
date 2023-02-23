package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.net.URI;
import java.time.LocalDateTime;

// Acredito que não está sendo mais usado
@Deprecated(since = "2023-02-21", forRemoval = true)
@RestControllerAdvice
public class ConstraintViolationExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handle(ConstraintViolationException exception) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/invalid-field"))
        .title("Invalid arguments")
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
