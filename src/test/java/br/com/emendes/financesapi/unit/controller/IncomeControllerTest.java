package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.IncomeController;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.service.IncomeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = IncomeController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DisplayName("Tests for IncomeController")
class IncomeControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private IncomeService incomeServiceMock;

  private final String INCOME_BASE_URI = "/api/incomes";
  private final String CONTENT_TYPE = "application/json;charset=UTF-8";
  private static final IncomeResponse INCOME_RESPONSE = IncomeResponse.builder()
      .id(50000L)
      .description("income xpto")
      .date(LocalDate.parse("2023-02-05"))
      .value(new BigDecimal("2492.83"))
      .build();

  @Nested
  @DisplayName("Tests for Create endpoint")
  class CreateEndpoint {

    @Test
    @DisplayName("create must return IncomeResponse when create successfully")
    void create_MustReturnIncomeResponse_WhenCreateSuccessfully() throws Exception {
      BDDMockito.when(incomeServiceMock.create(any()))
          .thenReturn(INCOME_RESPONSE);

      String requestBody = """
          {
            "description" : "income xpto",
            "value" : 2492.83,
            "date" : "2023-02-05"
          }
          """;

      mockMvc.perform(post(INCOME_BASE_URI).contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(50000L))
          .andExpect(jsonPath("$.description").value("income xpto"))
          .andExpect(jsonPath("$.date").value("2023-02-05"))
          .andExpect(jsonPath("$.value").value("2492.83"));
    }

    @Test
    @DisplayName("create must return ValidationProblemDetail when request body is invalid")
    void create_MustReturnValidationProblemDetail_WhenRequestBodyIsInvalid() throws Exception {
      String requestBody = """
          {
            "description" : "",
            "date" : "2023-02-05"
          }
          """;

      mockMvc.perform(post(INCOME_BASE_URI).contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Invalid fields"))
          .andExpect(jsonPath("$.detail").value("Some fields are invalid"))
          .andExpect(jsonPath("$.fields").isString())
          .andExpect(jsonPath("$.messages").isString());
    }

  }

  @Nested
  @DisplayName("Tests for Read endpoint")
  class ReadEndpoint {

    @Test
    @DisplayName("read must return Page<IncomeResponse> when read successfully")
    void read_MustReturnPageIncomeResponse_WhenReadSuccessfully() throws Exception {
      BDDMockito.when(incomeServiceMock.readAllByUser(any()))
          .thenReturn(new PageImpl<>(List.of(INCOME_RESPONSE)));


      mockMvc.perform(get(INCOME_BASE_URI))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(50000L))
          .andExpect(jsonPath("$.content[0].description").value("income xpto"))
          .andExpect(jsonPath("$.content[0].date").value("2023-02-05"))
          .andExpect(jsonPath("$.content[0].value").value("2492.83"));
    }

    @Test
    @DisplayName("read must return ProblemDetail when user has no incomes")
    void read_MustReturnProblemDetail_WhenUserHasNoIncomes() throws Exception {
      BDDMockito.given(incomeServiceMock.readAllByUser(any()))
          .willThrow(new NoResultException("The user has no incomes"));

      mockMvc.perform(get(INCOME_BASE_URI))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("The user has no incomes"));
    }

    @Test
    @DisplayName("read by description must return Page<IncomeResponse> when read successfully")
    void readByDescription_MustReturnPageIncomeResponse_WhenReadSuccessfully() throws Exception {
      BDDMockito.when(incomeServiceMock.readByDescriptionAndUser(eq("xpto"), any()))
          .thenReturn(new PageImpl<>(List.of(INCOME_RESPONSE)));

      mockMvc.perform(get(INCOME_BASE_URI + "?description=xpto"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(50000L))
          .andExpect(jsonPath("$.content[0].description").value("income xpto"))
          .andExpect(jsonPath("$.content[0].date").value("2023-02-05"))
          .andExpect(jsonPath("$.content[0].value").value("2492.83"));
    }

    @Test
    @DisplayName("read by description must return ProblemDetail when user has no incomes with description \"xxxx\"")
    void readByDescription_MustReturnProblemDetail_WhenUserHasNoIncomesWithDescriptionXxxx() throws Exception {
      BDDMockito.given(incomeServiceMock.readByDescriptionAndUser(eq("xxxx"), any()))
          .willThrow(new NoResultException("The user has no incomes with a description similar to xxxx"));

      mockMvc.perform(get(INCOME_BASE_URI + "?description=xxxx"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("The user has no incomes with a description similar to xxxx"));
    }

  }

  @Nested
  @DisplayName("Tests for readById endpoint")
  class ReadByIdEndpoint {

    @Test
    @DisplayName("readById must return IncomeResponse when read by id successfully")
    void readById_MustReturnIncomeResponse_WhenReadByIdSuccessfully() throws Exception {
      BDDMockito.when(incomeServiceMock.readByIdAndUser(50000L))
          .thenReturn(INCOME_RESPONSE);

      mockMvc.perform(get(INCOME_BASE_URI + "/50000"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(50000L))
          .andExpect(jsonPath("$.description").value("income xpto"))
          .andExpect(jsonPath("$.date").value("2023-02-05"))
          .andExpect(jsonPath("$.value").value("2492.83"));
    }

    @Test
    @DisplayName("readById must return ProblemDetail when user has no income with id 99999")
    void readById_MustReturnProblemDetail_WhenUserHasNoIncomeWithId99999() throws Exception {
      BDDMockito.given(incomeServiceMock.readByIdAndUser(99999L))
          .willThrow(new NoResultException("Income not found"));

      mockMvc.perform(get(INCOME_BASE_URI + "/99999"))
          .andExpect(status().isNotFound())

          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("Income not found"));
    }

    @Test
    @DisplayName("readById must return ProblemDetail when id is invalid")
    void readById_MustReturnProblemDetail_WhenIdIsInvalid() throws Exception {
      mockMvc.perform(get(INCOME_BASE_URI + "/99asdf"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

  }

  @Nested
  @DisplayName("Tests for readByYearAndMonth endpoint")
  class ReadByYearAndMonth {

    @Test
    @DisplayName("readByYearAndMonth must return Page<IncomeResponse> when read by year and month successfully")
    void readByYearAndMonth_MustReturnPageIncomeResponse_WhenReadByYearAndMonthSuccessfully() throws Exception {
      BDDMockito.when(incomeServiceMock.readByYearAndMonthAndUser(eq(2023), eq(2), any()))
          .thenReturn(new PageImpl<>(List.of(INCOME_RESPONSE)));

      mockMvc.perform(get(INCOME_BASE_URI + "/2023/2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(50000L))
          .andExpect(jsonPath("$.content[0].description").value("income xpto"))
          .andExpect(jsonPath("$.content[0].date").value("2023-02-05"))
          .andExpect(jsonPath("$.content[0].value").value("2492.83"));
    }

    @Test
    @DisplayName("readByYearAndMonth must return ProblemDetail when year is invalid")
    void readByYearAndMonth_MustReturnProblemDetail_WhenYearIsInvalid() throws Exception {
      mockMvc.perform(get(INCOME_BASE_URI + "/2o23/2"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

    @Test
    @DisplayName("readByYearAndMonth must return ProblemDetail when month is invalid")
    void readByYearAndMonth_MustReturnProblemDetail_WhenMonthIsInvalid() throws Exception {
      mockMvc.perform(get(INCOME_BASE_URI + "/2023/1o"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

    @Test
    @DisplayName("readByYearAndMonth must return ProblemDetail when user has not incomes for year and month")
    void readByYearAndMonth_MustReturnProblemDetail_WhenUserHasNotIncomesForYearAndMonth() throws Exception {
      BDDMockito.given(incomeServiceMock.readByYearAndMonthAndUser(eq(2023), eq(3), any()))
          .willThrow(new NoResultException("Has no incomes for year 2023 and month MARCH"));

      mockMvc.perform(get(INCOME_BASE_URI + "/2023/03"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("Has no incomes for year 2023 and month MARCH"));
    }

  }

  @Nested
  @DisplayName("Tests for update endpoint")
  class UpdateEndpoint {

    @Test
    @DisplayName("update must return IncomeResponse when update successfully")
    void update_MustReturnIncomeResponse_WhenUpdateSuccessfully() throws Exception {
      IncomeResponse incomeResponse = IncomeResponse.builder()
          .id(50000L)
          .description("income xpto updated")
          .date(LocalDate.parse("2023-02-05"))
          .value(new BigDecimal("2149.99"))
          .build();

      BDDMockito.when(incomeServiceMock.update(eq(50000L), any()))
          .thenReturn(incomeResponse);

      String requestBody = """
          {
            "description" : "income xpto updated",
            "value" : 2149.99,
            "date" : "2023-02-05"
          }
          """;

      mockMvc.perform(put(INCOME_BASE_URI + "/50000").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(50000L))
          .andExpect(jsonPath("$.description").value("income xpto updated"))
          .andExpect(jsonPath("$.date").value("2023-02-05"))
          .andExpect(jsonPath("$.value").value("2149.99"));
    }

    @Test
    @DisplayName("update must return ProblemDetail when user has no incomes with id 99999")
    void update_MustReturnProblemDetail_WhenUserHasNoIncomesWithId99999() throws Exception {
      BDDMockito.given(incomeServiceMock.update(eq(99999L), any()))
          .willThrow(new NoResultException("Income not found"));

      String requestBody = """
          {
            "description" : "income xpto updated",
            "value" : 2149.99,
            "date" : "2023-02-05"
          }
          """;

      mockMvc.perform(put(INCOME_BASE_URI + "/99999").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("Income not found"));
    }

    @Test
    @DisplayName("update must return ValidationProblemDetail when request body is invalid")
    void update_MustReturnValidationProblemDetail_WhenRequestBodyIsInvalid() throws Exception {
      String requestBody = """
          {
            "description" : "",
            "date" : "2023-02-05"
          }
          """;

      mockMvc.perform(put(INCOME_BASE_URI + "/50000").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Invalid fields"))
          .andExpect(jsonPath("$.detail").value("Some fields are invalid"))
          .andExpect(jsonPath("$.fields").isString())
          .andExpect(jsonPath("$.messages").isString());
    }

    @Test
    @DisplayName("update must return ProblemDetail when id is invalid")
    void update_MustReturnProblemDetail_WhenIdIsInvalid() throws Exception {
      String requestBody = """
          {
            "description" : "income xpto updated",
            "value" : 2149.99,
            "date" : "2023-02-05"
          }
          """;

      mockMvc.perform(put(INCOME_BASE_URI + "/50o00").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

  }

  @Nested
  @DisplayName("Tests for delete endpoint")
  class DeleteEndpoint {

    @Test
    @DisplayName("delete must return status 204 when delete successfully")
    void delete_MustReturnStatus204_WhenDeleteSuccessfully() throws Exception {
      BDDMockito.doNothing().when(incomeServiceMock).deleteById(50000L);

      mockMvc.perform(delete(INCOME_BASE_URI + "/50000"))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete must return ProblemDetail when not found income with id 99999")
    void delete_MustReturnProblemDetail_WhenNotFoundIncomeWithId99999() throws Exception {
      BDDMockito.willThrow(new NoResultException("Income not found"))
          .given(incomeServiceMock).deleteById(99999L);

      mockMvc.perform(delete(INCOME_BASE_URI + "/99999"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("Income not found"));
    }

    @Test
    @DisplayName("delete must return ProblemDetail when id is invalid")
    void delete_MustReturnProblemDetail_WhenIdIsInvalid() throws Exception {
      mockMvc.perform(delete(INCOME_BASE_URI + "/50oo0"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

  }

}
