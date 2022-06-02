package br.com.emendes.financesapi.validation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;

@RestControllerAdvice
public class AuthenticationErrorHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorDto> handle(AuthenticationException exception) {
    ErrorDto errorDto = new ErrorDto("Bad credentials", "Email ou password inválidos");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}