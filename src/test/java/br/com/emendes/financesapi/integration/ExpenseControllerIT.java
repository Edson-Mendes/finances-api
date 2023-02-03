package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.controller.form.LoginForm;
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

    HttpEntity<LoginForm> requestBody = new HttpEntity<>(new LoginForm(email, password));

    ResponseEntity<TokenDto> response = testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {});

    HEADERS.add("Authorization", "Bearer "+response.getBody().getToken());
  }

  @Test
  @DisplayName("create must returns status 201 and ExpenseDto when created successful")
  void create_ReturnsStatus201AndExpenseDto_WhenCreatedSuccessful(){
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();

    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseForm, HEADERS);

    ResponseEntity<ExpenseDto> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ExpenseDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isNotNull();
    Assertions.assertThat(responseBody.getDescription()).isEqualTo(expenseForm.getDescription());
    Assertions.assertThat(responseBody.getDate())
        .isEqualTo(LocalDate.parse(expenseForm.getDate(), Formatter.dateFormatter));
    Assertions.assertThat(responseBody.getDescription()).isEqualTo(expenseForm.getDescription());
  }

  @Test
  @DisplayName("create must returns status 401 when isnt authenticaded")
  void create_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, null, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("create must returns status 400 and List<FormErrorDto> when body is invalid")
  void create_ReturnsStatus400AndListFormErrorDto_WhenBodyIsInvalid(){
    ExpenseForm expenseForm = ExpenseFormCreator.withBlankDescription();

    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseForm, HEADERS);

    ResponseEntity<List<FormErrorDto>> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    List<FormErrorDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull().isNotEmpty().hasSize(1);
    Assertions.assertThat(responseBody.get(0).getField()).isEqualTo("description");
    Assertions.assertThat(responseBody.get(0).getError()).isEqualTo("não deve estar em branco");
  }

  @Test
  @DisplayName("create must returns status 409 and ErrorDto when has conflict")
  void create_ReturnsStatus409AndErrorDto_WhenHasConflict(){
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();

    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseForm, HEADERS);

    testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CONFLICT);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("CONFLICT");
    Assertions.assertThat(responseBody.getMessage()).contains("Uma despesa com essa descrição já existe em");
  }

  @Test
  @DisplayName("read must returns status 200 and Page<ExpenseDto> when readed successful")
  void read_ReturnsStatus200AndPageExpenseDto_WhenReadedSuccessful(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    expenseRepository.save(ExpenseCreator.withDescription("Internet"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<ExpenseDto>> response = testRestTemplate
        .exchange(BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<ExpenseDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isNotEmpty().hasSize(2);
    Assertions.assertThat(responseBody.getContent().get(0).getDescription()).isEqualTo("Mercado");
    Assertions.assertThat(responseBody.getContent().get(1).getDescription()).isEqualTo("Internet");
  }

  @Test
  @DisplayName("read must returns status 200 and empty Page<ExpenseDto> when read page one")
  void read_ReturnsStatus200AndEmptyPageExpenseDto_WhenReadPageOne(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    expenseRepository.save(ExpenseCreator.withDescription("Internet"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<ExpenseDto>> response = testRestTemplate
        .exchange(BASE_URI+"?page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<ExpenseDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isEmpty();
    Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(2L);
  }

  @Test
  @DisplayName("read must returns status 200 and empty Page<ExpenseDto> when read by description and page one")
  void read_ReturnsStatus200AndEmptyPageExpenseDto_WhenReadByDescriptionAndPageOne(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    expenseRepository.save(ExpenseCreator.withDescription("Intenet"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<ExpenseDto>> response = testRestTemplate
        .exchange(BASE_URI+"?description=mer&page=1",
            HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<ExpenseDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isEmpty();
    Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(1L);
  }

  @Test
  @DisplayName("read must returns status 401 when isn't authenticated")
  void read_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("read must returns status 404 when user hasn't expenses")
  void read_ReturnsStatus404AndErrorDto_WhenUserHasntExpenses(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("O usuário não possui despesas");
  }

  @Test
  @DisplayName("read must returns status 200 and Page<ExpenseDto> when read by description successfully")
  void read_ReturnsStatus200AndPageExpenseDto_WhenReadByDescriptionSuccessfully(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    expenseRepository.save(ExpenseCreator.withDescription("Internet"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<ExpenseDto>> response = testRestTemplate
        .exchange(BASE_URI+"?description=mer", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<ExpenseDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isNotEmpty().hasSize(1);
    Assertions.assertThat(responseBody.getContent().get(0).getDescription()).isEqualTo("Mercado");
  }

  @Test
  @DisplayName("read must returns status 404 when user hasn't expenses with given description")
  void read_ReturnsStatus404AndErrorDto_WhenUserHasntExpensesWithGivenDescription(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"?description=mer", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).contains("O usuário não possui despesas com descrição similar a ");
  }

  @Test
  @DisplayName("readById must returns status 200 and ExpenseDto when found successful")
  void readById_ReturnsStatus200AndExpenseDto_WhenFoundSuccessful(){
    expenseRepository.save(ExpenseCreator.withDescription("Farmácia"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ExpenseDto> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ExpenseDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(responseBody.getDescription()).isEqualTo("Farmácia");
  }

  @Test
  @DisplayName("readById must returns status 401 when isn't authenticated")
  void readById_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("readById must returns status 404 and ErrorDto when id not exists")
  void readById_ReturnsStatus404AndErrorDto_WhenIdNotExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/10000", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage())
        .isEqualTo("Nenhuma despesa com id = 10000 para esse usuário");
  }

  @Test
  @DisplayName("readByYearAndMonth returns status 200 and Page<ExpenseDto> when found successful")
  void readByYearAndMonth_ReturnsStatus200AndPageExpenseDto_WhenFoundSuccessful(){
    expenseRepository.save(ExpenseCreator.withDescription("Farmácia"));
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<PageableResponse<ExpenseDto>> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<ExpenseDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().hasSize(2);
    Assertions.assertThat(responseBody.getContent().get(1).getDescription()).isEqualTo("Mercado");
    Assertions.assertThat(responseBody.getContent().get(0).getDescription()).isEqualTo("Farmácia");
  }

  @Test
  @DisplayName("readByYearAndMonth returns status 200 and empty page when read by year and month and page one")
  void readByYearAndMonth_ReturnsStatus200AndEmptyPage_WhenReadByYearAndMonthAndPageOne(){
    expenseRepository.save(ExpenseCreator.withDescription("Farmácia"));
    expenseRepository.save(ExpenseCreator.withDescription("Transporte"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<PageableResponse<ExpenseDto>> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01?page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<ExpenseDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isEmpty();
    Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(2L);
  }

  @Test
  @DisplayName("readByYearAndMonth must returns status 401 when isn't authenticated")
  void readByYearAndMonth_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("readByYearAndMonth must returns status 404 and ErrorDto when id not exists")
  void readByYearAndMonth_ReturnsStatus404AndErrorDto_WhenIdNotExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("Não há despesas para o ano 2022 e mês 1");
  }

  @Test
  @DisplayName("readByYearAndMonth must returns status 400 and ErrorDto when month can't be parsed")
  void readByYearAndMonth_ReturnsStatus400AndErrorDto_WhenMonthCantBeParsed(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/2022/ll", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("typeMismatch");
    Assertions.assertThat(responseBody.getMessage())
        .contains("Failed to convert value of type 'java.lang.String' to required type 'int'");
  }

  @Test
  @DisplayName("update must returns status 200 and ExpenseDto when updated successful")
  void update_ReturnsStatus200AndExpenseDto_WhenUpdatedSuccessful(){
    expenseRepository.save(ExpenseCreator.withDescription("Mercado"));
    ExpenseForm expenseToBeUpdated = ExpenseFormCreator.withDescription("Mercado caro");

    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseToBeUpdated, HEADERS);

    ResponseEntity<ExpenseDto> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ExpenseDto responseBody = response.getBody();

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
    ExpenseForm expenseToBeUpdated = ExpenseFormCreator.withDescription("Mercado caro");
    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseToBeUpdated, HEADERS);

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
    ExpenseForm expenseForm = ExpenseFormCreator.withBlankDescription();
    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseForm, HEADERS);

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

    ExpenseForm expenseForm = ExpenseFormCreator.withDescription("Mercado");
    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(expenseForm, HEADERS);

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
    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ExpenseDto> response = testRestTemplate.exchange(
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
    HttpEntity<ExpenseForm> requestEntity = new HttpEntity<>(HEADERS);
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
