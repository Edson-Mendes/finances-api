package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.validation.exception.WrongPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WrongPasswordHandler {

  @ExceptionHandler(WrongPasswordException.class)
  public ResponseEntity<ErrorDto> handle(WrongPasswordException exception){
    ErrorDto errorDto = new ErrorDto("Bad Request", exception.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}
