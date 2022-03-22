package br.com.emendes.financesapi.config.validation.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;

@RestControllerAdvice
public class ConflictErrorHandler {

  @ResponseStatus(code = HttpStatus.CONFLICT)
  @ExceptionHandler(ResponseStatusException.class)
  public ErrorDto handle(ResponseStatusException exception) {
    ErrorDto errorDto = new ErrorDto(exception.getStatus().name(), exception.getReason());
    return errorDto;
  }
}