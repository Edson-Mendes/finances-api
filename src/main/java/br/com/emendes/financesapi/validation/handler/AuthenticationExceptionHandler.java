package br.com.emendes.financesapi.validation.handler;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorDto> handle(AuthenticationException exception) {
    ErrorDto errorDto = new ErrorDto("Bad credentials", "Invalid email or password");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}
