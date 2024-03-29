package br.com.emendes.financesapi.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class TypeMismatchHandler {

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handle(MethodArgumentTypeMismatchException exception) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/type-mismatch"))
        .title("Type mismatch")
        .detail("An error occurred trying to cast String to Number")
        .status(status.value())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
