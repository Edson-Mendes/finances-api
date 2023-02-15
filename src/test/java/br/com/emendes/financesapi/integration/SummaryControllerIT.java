package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.util.creator.ExpenseCreator;
import br.com.emendes.financesapi.util.creator.IncomeCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration tests for /resumo/**")
class SummaryControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private ExpenseRepository expenseRepository;
  @Autowired
  private IncomeRepository incomeRepository;

  private final String BASE_URI = "/resumo";
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
  @DisplayName("monthSummary must returns status 200 and SummaryDto when successful")
  void monthSummary_ReturnsStatus200AndSummaryDto_WhenSuccessful(){
    List<Income> incomes = List.of(
        IncomeCreator.withDescriptionAndValue("Salário", new BigDecimal("2500.00")),
        IncomeCreator.withDescriptionAndValue("Venda Livros", new BigDecimal("150.00"))
    );
    List<Expense> expenses = List.of(
        ExpenseCreator.withDescriptionAndValue("Gasolina", new BigDecimal("300.00")),
        ExpenseCreator.withDescriptionAndValue("Aluguel", new BigDecimal("800.00")),
        ExpenseCreator.withDescriptionAndValue("Mercado", new BigDecimal("700.00")),
        ExpenseCreator.withDescriptionAndValue("Internet", new BigDecimal("150.00"))
    );

    incomeRepository.saveAll(incomes);
    expenseRepository.saveAll(expenses);

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<SummaryResponse> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    SummaryResponse responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getIncomeTotalValue()).isEqualByComparingTo(new BigDecimal("2650.00"));
    Assertions.assertThat(responseBody.getExpenseTotalValue()).isEqualByComparingTo(new BigDecimal("1950.00"));
    Assertions.assertThat(responseBody.getFinalBalance()).isEqualByComparingTo(new BigDecimal("700.00"));
  }

  @Test
  @DisplayName("monthSummary must returns status 404 and ErrorDto when summary not exists")
  void monthSummary_ReturnsStatus404AndErrorDto_WhenSummaryNotExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/2022/08", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("Não há receitas e despesas para AUGUST 2022");
  }

  @Test
  @DisplayName("monthSummary must returns status 401 when isnt authenticaded")
  void monthSummary_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("monthSummary must returns 400 and ErrorDto when can't parse year")
  void monthSummary_Returns400AndErrorDto_WhenCantParseYear(){
    String year = "2O22";
    String month = "01";

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        String.format("%s/%s/%s", BASE_URI, year, month),
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("typeMismatch");
    Assertions.assertThat(responseBody.getMessage()).contains("Failed to convert value of type");
  }

  @Test
  @DisplayName("monthSummary must returns 400 and ErrorDto when year is out of range")
  void monthSummary_Returns400AndErrorDto_WhenYearIsOutOfRange(){
    String year = "1969";
    String month = "01";

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        String.format("%s/%s/%s", BASE_URI, year, month),
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Bad Request");
    Assertions.assertThat(responseBody.getMessage()).contains("deve ser maior que ou igual à 1970");
  }

  @Test
  @DisplayName("monthSummary must returns 400 and ErrorDto when month is out of range")
  void monthSummary_Returns400AndErrorDto_WhenMonthIsOutOfRange(){
    String year = "2022";
    String month = "13";

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        String.format("%s/%s/%s", BASE_URI, year, month),
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Bad Request");
    Assertions.assertThat(responseBody.getMessage()).contains("deve ser menor que ou igual à 12");
  }

}
