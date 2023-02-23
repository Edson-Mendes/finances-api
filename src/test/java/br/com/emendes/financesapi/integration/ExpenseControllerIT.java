package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.util.Formatter;
import br.com.emendes.financesapi.util.creator.ExpenseCreator;
import br.com.emendes.financesapi.util.creator.ExpenseFormCreator;
import br.com.emendes.financesapi.util.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration tests for /despesas/**")
class ExpenseControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private ExpenseRepository expenseRepository;

  private final String BASE_URI = "/despesas";
  private final HttpHeaders HEADERS = new HttpHeaders();

  @BeforeAll
  public void singInAndAddAuthorizationHeader(){
    String email = "user@email.com";
    String password = "123456";

    HttpEntity<SignInRequest> requestBody = new HttpEntity<>(new SignInRequest(email, password));

    ResponseEntity<TokenResponse> response = testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {});

    HEADERS.add("Authorization", "Bearer "+response.getBody().getToken());
  }

  @Test
  @DisplayName("update must returns status 200 and ExpenseDto when updated successful")
  void update_ReturnsStatus200AndExpenseDto_WhenUpdatedSuccessful(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    ExpenseRequest expenseToBeUpdated = ExpenseFormCreator.withDescription("Mercado caro");

    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(expenseToBeUpdated, HEADERS);

    ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ExpenseResponse responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(responseBody.getDescription()).isEqualTo("Mercado caro");
  }

  @Test
  @DisplayName("update must returns status 401 when isn't authenticated")
  void update_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.PUT, null, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("update must returns status 404 and ErrorDto when id not exists")
  void update_ReturnsStatus404AndErrorDto_WhenIdNotExists(){
    ExpenseRequest expenseToBeUpdated = ExpenseFormCreator.withDescription("Mercado caro");
    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(expenseToBeUpdated, HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/10000", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage())
        .isEqualTo("Nenhuma despesa com id = 10000 para esse usuário");
  }

  @Test
  @DisplayName("update must returns status 400 and List<FormErrorDto> when body is invalid")
  void update_ReturnsStatus400AndListFormErrorDto_WhenBodyIsInvalid(){
    ExpenseRequest expenseRequest = ExpenseFormCreator.withBlankDescription();
    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(expenseRequest, HEADERS);

    ResponseEntity<List<FormErrorDto>> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    List<FormErrorDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.get(0).getField()).isEqualTo("description");
    Assertions.assertThat(responseBody.get(0).getError()).isEqualTo("não deve estar em branco");
  }

  @Test
  @DisplayName("update must returns status 409 and ErrorDto when there is conflict between descriptions")
  void update_ReturnsStatus409AndErrorDto_WhenThereIsConflictBetweenDescriptions(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    expenseRepository.save(ExpenseCreator.withDescription("Transporte"));

    ExpenseRequest expenseRequest = ExpenseFormCreator.withDescription("Mercado");
    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(expenseRequest, HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/2", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CONFLICT);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("CONFLICT");
    Assertions.assertThat(responseBody.getMessage()).contains("Outra despesa com essa descrição já existe em ");
  }

  @Test
  @DisplayName("delete must returns status 204 when deleted successful")
  void delete_ReturnsStatus204_WhenDeletedSuccessful(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();

    Optional<Expense> optionalExpense = expenseRepository.findById(1L);

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(optionalExpense).isEmpty();
  }

  @Test
  @DisplayName("delete must returns status 401 when isn't authenticated")
  void delete_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("delete must returns status 404 and ErrorDto when id not exists")
  void delete_ReturnsStatus404AndErrorDto_WhenIdNotExists(){
    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/10000", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage())
        .isEqualTo("Nenhuma despesa com id = 10000 para esse usuário");
  }

}
