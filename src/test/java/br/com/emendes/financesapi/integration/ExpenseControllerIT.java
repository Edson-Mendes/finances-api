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

}
