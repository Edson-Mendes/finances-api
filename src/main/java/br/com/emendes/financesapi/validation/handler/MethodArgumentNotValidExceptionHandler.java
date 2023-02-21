package br.com.emendes.financesapi.validation.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Problem> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining("; "));
    String messages = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));

    Problem problem = Problem.builder()
        .withType(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/invalid-field"))
        .withTitle("Invalid fields")
        .withDetail("Some fields are invalid")
        .withStatus(Status.BAD_REQUEST)
        .with("timestamp", LocalDateTime.now())
        .with("fields", fields)
        .with("messages", messages)
        .build();

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

}
