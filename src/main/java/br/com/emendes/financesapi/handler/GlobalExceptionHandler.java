package br.com.emendes.financesapi.handler;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
import br.com.emendes.financesapi.exception.DataConflictException;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.exception.WrongPasswordException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.QueryException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável por lidar com algumas exceptions que ocorrerem a partir da controller.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String PROBLEM_DETAIL_URI = "https://github.com/Edson-Mendes/finances-api";

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpHeaders headers,
      HttpStatusCode httpStatusCode,
      WebRequest request) {
    log.info("Method Argument Not Valid - message: {}", exception.getMessage());

    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining("; "));
    String messages = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));

    ValidationProblemDetail problem = ValidationProblemDetail.builder()
        .type(URI.create("https://github.com/Edson-Mendes/finances-api/problem-details/invalid-field"))
        .title("Invalid fields")
        .detail("Some fields are invalid")
        .status(status.value())
        .timestamp(LocalDateTime.now())
        .fields(fields)
        .messages(messages)
        .build();

    return ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(problem);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException exception) {
    log.info("Constraint Violation - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;

    String messages = exception.getConstraintViolations()
        .stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));

    return createResponseEntity(
        status,
        createProblemDetail("Invalid arguments", messages, status.value())
    );
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException exception) {
    log.info("Authentication Exception - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;

    return createResponseEntity(
        status,
        createProblemDetail("Bad credentials", "Invalid email or password", status.value())
    );
  }

  @ExceptionHandler(PasswordsDoNotMatchException.class)
  public ResponseEntity<ProblemDetail> handlePasswordsDoNotMatch(PasswordsDoNotMatchException exception) {
    log.info("Passwords Do Not Match - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;

    return createResponseEntity(
        status,
        createProblemDetail("Passwords do not match", exception.getMessage(), status.value())
    );
  }

  @ExceptionHandler(WrongPasswordException.class)
  public ResponseEntity<ProblemDetail> handleWrongPassword(WrongPasswordException exception) {
    log.info("Wrong Password - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;

    return createResponseEntity(
        status,
        createProblemDetail("Wrong password", exception.getMessage(), status.value())
    );
  }

  @ExceptionHandler(QueryException.class)
  public ResponseEntity<ProblemDetail> handleQueryException(QueryException exception) {
    log.info("Query Exception - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;

    String message = exception.getMessage().substring(0, exception.getMessage().indexOf(" of:", 26));

    return createResponseEntity(
        status,
        createProblemDetail("Something went wrong", message, status.value())
    );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
    log.info("Method Argument Type Mismatch - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;

    return createResponseEntity(
        status,
        createProblemDetail("Type mismatch", "An error occurred trying to cast String to Number", status.value())
    );
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleEntityNotFound(EntityNotFoundException exception) {
    log.info("Entity not found - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.NOT_FOUND;

    return createResponseEntity(
        status,
        createProblemDetail("Entity not found", exception.getMessage(), status.value())
    );
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ProblemDetail> handleDataConflict(DataConflictException exception) {
    log.info("Data conflict - message: {}", exception.getMessage());
    HttpStatus status = HttpStatus.CONFLICT;

    return createResponseEntity(
        status,
        createProblemDetail("Data conflict", exception.getMessage(), status.value())
    );
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException exception) {
    log.error("Internal server error", exception);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    return createResponseEntity(
        status,
        createProblemDetail("Something went wrong", exception.getMessage(), status.value())
    );
  }

  /**
   * Cria uma instância de {@link ResponseEntity} a partir de um objeto {@link ProblemDetail} que representa o
   * body da resposta, o status da resposta corresponde ao campo status do ProblemDetail.
   *
   * @param body objeto que será o corpo da resposta.
   * @return {@code ResponseEntity<ProblemDetail>}
   */
  private static ResponseEntity<ProblemDetail> createResponseEntity(HttpStatus status, ProblemDetail body) {
    return ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json;charset=UTF-8")
        .body(body);
  }

  /**
   * Gera uma instância de {@link ProblemDetail} a partir dos parâmetros {@code title}, {@code detail} e
   * {@code statusCode}, o campo type é padrão {@code https://github.com/Edson-Mendes/your-review-api}.
   *
   * @param title      campo {@code title} do corpo da resposta.
   * @param detail     campo {@code title} do corpo da resposta.
   * @param statusCode campo {@code status} do corpo da resposta.
   * @return objeto ProblemDetail
   */
  private ProblemDetail createProblemDetail(String title, String detail, int statusCode) {
    return ProblemDetail.builder()
        .type(URI.create(PROBLEM_DETAIL_URI))
        .title(title)
        .detail(detail)
        .status(statusCode)
        .timestamp(LocalDateTime.now())
        .build();
  }

}
