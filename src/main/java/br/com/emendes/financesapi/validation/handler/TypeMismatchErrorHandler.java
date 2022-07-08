package br.com.emendes.financesapi.validation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;

@RestControllerAdvice
public class TypeMismatchErrorHandler {

// TODO: Pensar/Pesquisar uma maneira de melhorar essa mensagem!
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorDto> handle(MethodArgumentTypeMismatchException exception) {
    ErrorDto errorDto = new ErrorDto(exception.getErrorCode(), exception.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}
