package br.com.emendes.financesapi.validation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class TypeMismatchHandler {

  // TODO: Pensar/Pesquisar uma maneira de melhorar essa mensagem!
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Problem> handle(MethodArgumentTypeMismatchException exception) {
    Problem problem = Problem.builder()
        .withType(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/type-mismatch"))
        .withTitle("Type mismatch")
        .withDetail("An error occurred trying to cast String to Number")
        .withStatus(Status.BAD_REQUEST)
        .with("timestamp", LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
