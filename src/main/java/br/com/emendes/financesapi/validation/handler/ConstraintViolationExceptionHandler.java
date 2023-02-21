package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

// Acredito que não está sendo mais usado
@Deprecated(since = "2023-02-21", forRemoval = true)
@RestControllerAdvice
public class ConstraintViolationExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorDto> handle(ConstraintViolationException exception){
    ErrorDto errorDto = new ErrorDto("Bad Request", exception.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}
