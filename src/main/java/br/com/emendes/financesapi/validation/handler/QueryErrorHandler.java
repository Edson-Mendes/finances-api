package br.com.emendes.financesapi.validation.handler;

import org.hibernate.QueryException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;

@RestControllerAdvice
public class QueryErrorHandler {

  @ExceptionHandler(QueryException.class)
  public ResponseEntity<ErrorDto> handle(QueryException exception) {
    ErrorDto errorDto = new ErrorDto("Bad Request", "Requisição inválida");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorDto> handle(PropertyReferenceException exception) {
    ErrorDto errorDto = new ErrorDto("Bad Request", "Propriedade da requisição inválida");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}
