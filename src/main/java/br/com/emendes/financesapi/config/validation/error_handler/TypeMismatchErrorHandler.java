package br.com.emendes.financesapi.config.validation.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;

@RestControllerAdvice
public class TypeMismatchErrorHandler {

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ErrorDto handle(MethodArgumentTypeMismatchException exception) {

    ErrorDto errorDto = new ErrorDto(exception.getErrorCode(), exception.getMessage());
    return errorDto;
  }

}
