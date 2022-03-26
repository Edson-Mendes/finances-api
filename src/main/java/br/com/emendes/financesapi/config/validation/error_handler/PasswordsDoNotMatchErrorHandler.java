package br.com.emendes.financesapi.config.validation.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.config.validation.exception.PasswordsDoNotMatchException;

@RestControllerAdvice
public class PasswordsDoNotMatchErrorHandler {
  
  @ExceptionHandler(PasswordsDoNotMatchException.class)
  public ResponseEntity<ErrorDto> handle(PasswordsDoNotMatchException exception){
    ErrorDto errorDto = new ErrorDto("BAD REQUEST", exception.getMessage());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }
}
