package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.util.Formatter;
import br.com.emendes.financesapi.util.creator.IncomeCreator;
import br.com.emendes.financesapi.util.creator.IncomeFormCreator;
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
@DisplayName("Integration tests for /receitas/**")
class IncomeControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private IncomeRepository incomeRepository;
  private final String BASE_URI = "/receitas";
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
  @DisplayName("create must returns status 201 and IncomeDto when created successful")
  void create_ReturnsStatus201AndIncomeDto_WhenCreatedSuccessful(){
    IncomeForm incomeForm = IncomeFormCreator.validIncomeForm();

    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeForm, HEADERS);

    ResponseEntity<IncomeDto> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    IncomeDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isNotNull();
    Assertions.assertThat(responseBody.getDescription()).isEqualTo(incomeForm.getDescription());
    Assertions.assertThat(responseBody.getDate())
        .isEqualTo(LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter));
    Assertions.assertThat(responseBody.getDescription()).isEqualTo(incomeForm.getDescription());
  }

  @Test
  @DisplayName("create must returns status 401 when isnt authenticated")
  void create_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, null, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("create must returns status 400 and List<FormErrorDto> when body is invalid")
  void create_ReturnsStatus400AndListFormErrorDto_WhenBodyIsInvalid(){
    IncomeForm incomeForm = IncomeFormCreator.withBlankDescription();

    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeForm, HEADERS);

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
    IncomeForm incomeForm = IncomeFormCreator.validIncomeForm();

    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeForm, HEADERS);

    testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CONFLICT);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("CONFLICT");
    Assertions.assertThat(responseBody.getMessage()).contains("Uma receita com essa descrição já existe em");
  }

  @Test
  @DisplayName("read must returns status 200 and Page<IncomeDto> when readed successful")
  void read_ReturnsStatus200AndPageIncomeDto_WhenReadedSuccessful(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    incomeRepository.save(IncomeCreator.withDescription("Venda Smartphone velho"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<IncomeDto>> response = testRestTemplate
        .exchange(BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isNotEmpty().hasSize(2);
    Assertions.assertThat(responseBody.getContent().get(0).getDescription()).isEqualTo("Salário");
    Assertions.assertThat(responseBody.getContent().get(1).getDescription()).isEqualTo("Venda Smartphone velho");
  }

  @Test
  @DisplayName("read must returns status 200 and empty Page<IncomeDto> when read page one")
  void read_ReturnsStatus200AndEmptyPageIncomeDto_WhenReadPageOne(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    incomeRepository.save(IncomeCreator.withDescription("Venda Smartphone velho"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<IncomeDto>> response = testRestTemplate
        .exchange(BASE_URI+"?page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isEmpty();
    Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(2L);
  }

  @Test
  @DisplayName("read must returns status 401 when isnt authenticated")
  void read_ReturnsStatus401_WhenIsntAuthenticated(){
    ResponseEntity<Void> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("read must returns status 404 when user hasn't incomes")
  void read_ReturnsStatus404AndErrorDto_WhenUserHasntIncomes(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("O usuário não possui receitas");
  }

  @Test
  @DisplayName("read must returns status 200 and Page<IncomeDto> when read by description successfully")
  void read_ReturnsStatus200AndPageIncomeDto_WhenReadByDescriptionSuccessfully(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    incomeRepository.save(IncomeCreator.withDescription("Venda Smartphone velho"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<IncomeDto>> response = testRestTemplate
        .exchange(BASE_URI+"?description=sal", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isNotEmpty().hasSize(1);
    Assertions.assertThat(responseBody.getContent().get(0).getDescription()).isEqualTo("Salário");
  }

  @Test
  @DisplayName("read must returns status 200 and empty Page<IncomeDto> when read by description and page one")
  void read_ReturnsStatus200AndEmptyPageIncomeDto_WhenReadByDescriptionAndPageOne(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    incomeRepository.save(IncomeCreator.withDescription("Venda Smartphone velho"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<PageableResponse<IncomeDto>> response = testRestTemplate
        .exchange(BASE_URI+"?description=sal&page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isEmpty();
    Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(1L);
  }

  @Test
  @DisplayName("read must returns status 404 when user hasn't incomes with given description")
  void read_ReturnsStatus404AndErrorDto_WhenUserHasntIncomesWithGivenDescription(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"?description=sal", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).contains("O usuário não possui receitas com descrição similar a ");
  }

  @Test
  @DisplayName("readById must returns status 200 and IncomeDto when found successful")
  void readById_ReturnsStatus200AndIncomeDto_WhenFoundSuccessful(){
    incomeRepository.save(IncomeCreator.withDescription("Venda Halteres"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<IncomeDto> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    IncomeDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(responseBody.getDescription()).isEqualTo("Venda Halteres");
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
  @DisplayName("readById must returns status 404 and ErrorDto when id don't exists")
  void readById_ReturnsStatus404AndErrorDto_WhenIdDontExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/10000", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage())
        .isEqualTo("Nenhuma receita com id = 10000 para esse usuário");
  }

  @Test
  @DisplayName("readByYearAndMonth returns status 200 and Page<IncomeDto> when found successful")
  void readByYearAndMonth_ReturnsStatus200AndPageIncomeDto_WhenFoundSuccessful(){
    incomeRepository.save(IncomeCreator.withDescription("Venda Halteres"));
    incomeRepository.save(IncomeCreator.withDescription("Salário"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<PageableResponse<IncomeDto>> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().hasSize(2);
    Assertions.assertThat(responseBody.getContent().get(0).getDescription()).isEqualTo("Venda Halteres");
    Assertions.assertThat(responseBody.getContent().get(1).getDescription()).isEqualTo("Salário");
  }

  @Test
  @DisplayName("readByYearAndMonth returns status 200 and empty page when read by year and month and page one")
  void readByYearAndMonth_ReturnsStatus200AndEmptyPage_WhenReadByYearAndMonthAndPageOne(){
    incomeRepository.save(IncomeCreator.withDescription("Venda Halteres"));
    incomeRepository.save(IncomeCreator.withDescription("Salário"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<PageableResponse<IncomeDto>> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01?page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

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
  @DisplayName("readByYearAndMonth must returns status 404 and ErrorDto when id don't exists")
  void readByYearAndMonth_ReturnsStatus404AndErrorDto_WhenIdDontExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/2022/01", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("Não há receitas para o ano 2022 e mês 1");
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
  @DisplayName("update must returns status 200 and IncomeDto when updated successful")
  void update_ReturnsStatus200AndIncomeDto_WhenUpdatedSuccessful(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    IncomeForm incomeToBeUpdated = IncomeFormCreator.withDescription("Salário baixo e sofrido");

    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeToBeUpdated, HEADERS);

    ResponseEntity<IncomeDto> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    IncomeDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(responseBody.getDescription()).isEqualTo("Salário baixo e sofrido");
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
  @DisplayName("update must returns status 404 and ErrorDto when id don't exists")
  void update_ReturnsStatus404AndErrorDto_WhenIdDontExists(){
    IncomeForm incomeToBeUpdated = IncomeFormCreator.withDescription("Salário baixo e sofrido");
    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeToBeUpdated, HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/10000", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage())
        .isEqualTo("Nenhuma receita com id = 10000 para esse usuário");
  }

  @Test
  @DisplayName("update must returns status 400 and List<FormErrorDto> when body is invalid")
  void update_ReturnsStatus400AndListFormErrorDto_WhenBodyIsInvalid(){
    IncomeForm incomeForm = IncomeFormCreator.withBlankDescription();
    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeForm, HEADERS);

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
  @DisplayName("update must returns status 409 and ErrorDto when has conflict between descriptions")
  void update_ReturnsStatus409AndErrorDto_WhenHasConflictBetweenDescriptions(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    incomeRepository.save(IncomeCreator.withDescription("Loteria"));

    IncomeForm incomeForm = IncomeFormCreator.withDescription("Salário");
    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(incomeForm, HEADERS);

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/2", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CONFLICT);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("CONFLICT");
    Assertions.assertThat(responseBody.getMessage()).contains("Outra receita com essa descrição já existe em ");
  }

  @Test
  @DisplayName("delete must returns status 204 when deleted successful")
  void delete_ReturnsStatus204_WhenDeletedSuccessful(){
    incomeRepository.save(IncomeCreator.withDescription("Salário"));
    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(HEADERS);

    ResponseEntity<IncomeDto> response = testRestTemplate.exchange(
        BASE_URI+"/1", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();

    Optional<Income> optionalIncome = incomeRepository.findById(1L);

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(optionalIncome).isEmpty();
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
  @DisplayName("delete must returns status 404 and ErrorDto when id don't exists")
  void delete_ReturnsStatus404AndErrorDto_WhenIdDontExists(){
    HttpEntity<IncomeForm> requestEntity = new HttpEntity<>(HEADERS);
    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI+"/10000", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage())
        .isEqualTo("Nenhuma receita com id = 10000 para esse usuário");
  }
}
