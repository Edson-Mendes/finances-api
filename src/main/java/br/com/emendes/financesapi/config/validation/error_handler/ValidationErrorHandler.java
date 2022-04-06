package br.com.emendes.financesapi.config.validation.error_handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;

@RestControllerAdvice
public class ValidationErrorHandler {

  @Autowired
  private MessageSource messageSource;
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<FormErrorDto>> handle(MethodArgumentNotValidException exception){

    List<FormErrorDto> listErrorDto = new ArrayList<>();
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    fieldErrors.forEach(e -> {
      String message = messageSource.getMessage(e, LocaleContextHolder.getLocale());
      FormErrorDto errorDto = new FormErrorDto(e.getField(), message);
      listErrorDto.add(errorDto);
    });
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(listErrorDto);
  }

}
