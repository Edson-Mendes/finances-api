package br.com.emendes.financesapi.config.validation.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.config.validation.exception.DataConflictException;

@RestControllerAdvice
public class ConflictErrorHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorDto> handle(ResponseStatusException exception) {
    ErrorDto errorDto = new ErrorDto(exception.getStatus().name(), exception.getReason());
    return conflictReturn(errorDto);
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ErrorDto> handle(DataConflictException exception) {
    ErrorDto errorDto = new ErrorDto("CONFLICT", exception.getMessage());
    return conflictReturn(errorDto);
  }

  private ResponseEntity<ErrorDto> conflictReturn(ErrorDto errorDto){
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }
}