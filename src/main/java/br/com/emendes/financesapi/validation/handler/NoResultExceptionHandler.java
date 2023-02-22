package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class NoResultExceptionHandler {

  @ExceptionHandler(NoResultException.class)
  public ResponseEntity<ProblemDetail> handleNoResultException(NoResultException exception) {
    HttpStatus status = HttpStatus.NOT_FOUND;

    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/entity-not-found"))
        .title("Entity not found")
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
