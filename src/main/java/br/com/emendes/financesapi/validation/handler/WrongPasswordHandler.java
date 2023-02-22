package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.validation.exception.WrongPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class WrongPasswordHandler {

  @ExceptionHandler(WrongPasswordException.class)
  public ResponseEntity<ProblemDetail> handle(WrongPasswordException exception) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ProblemDetail problem = ProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/wrong-password"))
        .title("Wrong password")
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
