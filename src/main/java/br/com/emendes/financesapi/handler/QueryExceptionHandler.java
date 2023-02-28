package br.com.emendes.financesapi.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import org.hibernate.QueryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class QueryExceptionHandler {

  @ExceptionHandler(QueryException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(QueryException exception) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    String message = exception.getMessage().substring(0, exception.getMessage().indexOf(" of:", 26));


    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/something-went-wrong"))
        .title("Something went wrong")
        .detail(message)
        .status(status.value())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
