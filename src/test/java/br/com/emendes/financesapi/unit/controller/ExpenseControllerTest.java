package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.ExpenseController;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.ExpenseService;
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
@WebMvcTest(value = ExpenseController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DisplayName("Tests for ExpenseController")
class ExpenseControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ExpenseService expenseServiceMock;

  private final String EXPENSE_BASE_URI = "/api/expenses";
  private final String CONTENT_TYPE = "application/json;charset=UTF-8";
  private static final ExpenseResponse EXPENSE_RESPONSE = ExpenseResponse.builder()
      .id(50000L)
      .description("expense xpto")
      .date(LocalDate.parse("2023-02-05"))
      .category(Category.ALIMENTACAO)
      .value(new BigDecimal("100.99"))
      .build();

  @Nested
  @DisplayName("Tests for Create endpoint")
  class CreateEndpoint {

    @Test
    @DisplayName("create must return ExpenseResponse when create successfully")
    void create_MustReturnExpenseResponse_WhenCreateSuccessfully() throws Exception {
      BDDMockito.when(expenseServiceMock.create(any()))
          .thenReturn(EXPENSE_RESPONSE);

      String requestBody = """
          {
            "description" : "expense xpto",
            "value" : 100.99,
            "date" : "2023-02-05",
            "category" : "ALIMENTACAO"
          }
          """;

      mockMvc.perform(post(EXPENSE_BASE_URI).contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(50000L))
          .andExpect(jsonPath("$.description").value("expense xpto"))
          .andExpect(jsonPath("$.date").value("2023-02-05"))
          .andExpect(jsonPath("$.value").value("100.99"))
          .andExpect(jsonPath("$.category").value("ALIMENTACAO"));
    }

    @Test
    @DisplayName("create must return List<FormErrorDto> when request body is invalid")
    void create_MustReturnListFormErrorDto_WhenRequestBodyIsInvalid() throws Exception {
      String requestBody = """
          {
            "description" : "",
            "date" : "2023-02-05",
            "category" : "ALIMENTACAO"
          }
          """;

      // TODO: O response body desse tipo de erro será alterado!
      mockMvc.perform(post(EXPENSE_BASE_URI).contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest());
    }

  }

  @Nested
  @DisplayName("Tests for Read endpoint")
  class ReadEndpoint {

    @Test
    @DisplayName("read must return Page<ExpenseResponse> when read successfully")
    void read_MustReturnPageExpenseResponse_WhenReadSuccessfully() throws Exception {
      BDDMockito.when(expenseServiceMock.readAllByUser(any()))
          .thenReturn(new PageImpl<>(List.of(EXPENSE_RESPONSE)));


      mockMvc.perform(get(EXPENSE_BASE_URI))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(50000L))
          .andExpect(jsonPath("$.content[0].description").value("expense xpto"))
          .andExpect(jsonPath("$.content[0].date").value("2023-02-05"))
          .andExpect(jsonPath("$.content[0].value").value("100.99"))
          .andExpect(jsonPath("$.content[0].category").value("ALIMENTACAO"));
    }

    @Test
    @DisplayName("read must return ErrorDto when user has no expenses")
    void read_MustReturnErrorDto_WhenUserHasNoExpenses() throws Exception {
      BDDMockito.given(expenseServiceMock.readAllByUser(any()))
          .willThrow(new NoResultException("The user has no expenses"));

      mockMvc.perform(get(EXPENSE_BASE_URI))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("read by description must return Page<ExpenseResponse> when read successfully")
    void readByDescription_MustReturnPageExpenseResponse_WhenReadSuccessfully() throws Exception {
      BDDMockito.when(expenseServiceMock.readByDescriptionAndUser(eq("xpto"), any()))
          .thenReturn(new PageImpl<>(List.of(EXPENSE_RESPONSE)));

      mockMvc.perform(get(EXPENSE_BASE_URI + "?description=xpto"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(50000L))
          .andExpect(jsonPath("$.content[0].description").value("expense xpto"))
          .andExpect(jsonPath("$.content[0].date").value("2023-02-05"))
          .andExpect(jsonPath("$.content[0].value").value("100.99"))
          .andExpect(jsonPath("$.content[0].category").value("ALIMENTACAO"));
    }

    @Test
    @DisplayName("read by description must return ErrorDto when user has no expenses with description \"xxxx\"")
    void readByDescription_MustReturnErrorDto_WhenUserHasNoExpensesWithDescriptionXxxx() throws Exception {
      BDDMockito.given(expenseServiceMock.readByDescriptionAndUser(eq("xxxx"), any()))
          .willThrow(new NoResultException("The user has no expenses with a description similar to xxxx"));

      mockMvc.perform(get(EXPENSE_BASE_URI + "?description=xxxx"))
          .andExpect(status().isNotFound());
    }

  }

  @Nested
  @DisplayName("Tests for readById endpoint")
  class ReadByIdEndpoint {

    @Test
    @DisplayName("readById must return ExpenseResponse when read by id successfully")
    void readById_MustReturnExpenseResponse_WhenReadByIdSuccessfully() throws Exception {
      BDDMockito.when(expenseServiceMock.readByIdAndUser(50000L))
          .thenReturn(EXPENSE_RESPONSE);

      mockMvc.perform(get(EXPENSE_BASE_URI + "/50000"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(50000L))
          .andExpect(jsonPath("$.description").value("expense xpto"))
          .andExpect(jsonPath("$.date").value("2023-02-05"))
          .andExpect(jsonPath("$.value").value("100.99"))
          .andExpect(jsonPath("$.category").value("ALIMENTACAO"));

    }

    @Test
    @DisplayName("readById must return ErrorDto when user has no expense with id 99999")
    void readById_MustReturnErrorDto_WhenUserHasNoExpenseWithId99999() throws Exception {
      BDDMockito.given(expenseServiceMock.readByIdAndUser(99999L))
          .willThrow(new NoResultException("Expense not found"));

      mockMvc.perform(get(EXPENSE_BASE_URI + "/99999"))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("readById must return ErrorDto when id is invalid")
    void readById_MustReturnErrorDto_WhenIdIsInvalid() throws Exception {
      mockMvc.perform(get(EXPENSE_BASE_URI + "/99asdf"))
          .andExpect(status().isBadRequest());
    }

  }

  @Nested
  @DisplayName("Tests for readByYearAndMonth endpoint")
  class ReadByYearAndMonth {

    @Test
    @DisplayName("readByYearAndMonth must return Page<ExpenseResponse> when read by year and month successfully")
    void readByYearAndMonth_MustReturnPageExpenseResponse_WhenReadByYearAndMonthSuccessfully() throws Exception {
      BDDMockito.when(expenseServiceMock.readByYearAndMonthAndUser(eq(2023), eq(2), any()))
          .thenReturn(new PageImpl<>(List.of(EXPENSE_RESPONSE)));

      mockMvc.perform(get(EXPENSE_BASE_URI + "/2023/2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(50000L))
          .andExpect(jsonPath("$.content[0].description").value("expense xpto"))
          .andExpect(jsonPath("$.content[0].date").value("2023-02-05"))
          .andExpect(jsonPath("$.content[0].value").value("100.99"))
          .andExpect(jsonPath("$.content[0].category").value("ALIMENTACAO"));
    }

    @Test
    @DisplayName("readByYearAndMonth must return ErrorDto when year is invalid")
    void readByYearAndMonth_MustReturnErrorDto_WhenYearIsInvalid() throws Exception {
      mockMvc.perform(get(EXPENSE_BASE_URI + "/2o23/2"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("readByYearAndMonth must return ErrorDto when month is invalid")
    void readByYearAndMonth_MustReturnErrorDto_WhenMonthIsInvalid() throws Exception {
      mockMvc.perform(get(EXPENSE_BASE_URI + "/2023/1o"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("readByYearAndMonth must return ErrorDto when user has not expenses for year and month")
    void readByYearAndMonth_MustReturnErrorDto_WhenUserHasNotExpensesForYearAndMonth() throws Exception {
      BDDMockito.given(expenseServiceMock.readByYearAndMonthAndUser(eq(2023), eq(3), any()))
          .willThrow(new NoResultException("Has no expenses for year 2023 and month 3"));

      mockMvc.perform(get(EXPENSE_BASE_URI + "/2023/03"))
          .andExpect(status().isNotFound());
    }

  }

  @Nested
  @DisplayName("Tests for update endpoint")
  class UpdateEndpoint {

    @Test
    @DisplayName("update must return ExpenseResponse when update successfully")
    void update_MustReturnExpenseResponse_WhenUpdateSuccessfully() throws Exception {
      ExpenseResponse expenseResponse = ExpenseResponse.builder()
          .id(50000L)
          .description("expense xpto updated")
          .date(LocalDate.parse("2023-02-05"))
          .category(Category.ALIMENTACAO)
          .value(new BigDecimal("149.99"))
          .build();

      BDDMockito.when(expenseServiceMock.update(eq(50000L), any()))
          .thenReturn(expenseResponse);

      String requestBody = """
          {
            "description" : "expense xpto updated",
            "value" : 149.99,
            "date" : "2023-02-05",
            "category" : "ALIMENTACAO"
          }
          """;

      mockMvc.perform(put(EXPENSE_BASE_URI + "/50000").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(50000L))
          .andExpect(jsonPath("$.description").value("expense xpto updated"))
          .andExpect(jsonPath("$.date").value("2023-02-05"))
          .andExpect(jsonPath("$.value").value("149.99"))
          .andExpect(jsonPath("$.category").value("ALIMENTACAO"));

    }

    @Test
    @DisplayName("update must return ErrorDto when user has no expenses with id 99999")
    void update_MustReturnErrorDto_WhenUserHasNoExpensesWithId99999() throws Exception {
      BDDMockito.given(expenseServiceMock.update(eq(99999L), any()))
          .willThrow(new NoResultException("Expense not found"));

      String requestBody = """
          {
            "description" : "expense xpto updated",
            "value" : 149.99,
            "date" : "2023-02-05",
            "category" : "ALIMENTACAO"
          }
          """;

      mockMvc.perform(put(EXPENSE_BASE_URI + "/99999").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("update must return ErrorDto when request body is invalid")
    void update_MustReturnErrorDto_WhenRequestBodyIsInvalid() throws Exception {
      String requestBody = """
          {
            "description" : "",
            "date" : "2023-02-05",
            "category" : "ALIMENTACAO"
          }
          """;

      mockMvc.perform(put(EXPENSE_BASE_URI + "/50000").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update must return ErrorDto when id is invalid")
    void update_MustReturnErrorDto_WhenIdIsInvalid() throws Exception {
      String requestBody = """
          {
            "description" : "expense xpto updated",
            "value" : 149.99,
            "date" : "2023-02-05",
            "category" : "ALIMENTACAO"
          }
          """;

      mockMvc.perform(put(EXPENSE_BASE_URI + "/50o00").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest());
    }

  }

  @Nested
  @DisplayName("Tests for delete endpoint")
  class DeleteEndpoint {

    @Test
    @DisplayName("delete must return status 204 when delete successfully")
    void delete_MustReturnStatus204_WhenDeleteSuccessfully() throws Exception {
      BDDMockito.doNothing().when(expenseServiceMock).deleteById(50000L);

      mockMvc.perform(delete(EXPENSE_BASE_URI + "/50000"))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete must return ErrorDto when not found expense with id 99999")
    void delete_MustReturnErrorDto_WhenNotFoundExpenseWithId99999() throws Exception {
      BDDMockito.willThrow(new NoResultException("Expense not found"))
          .given(expenseServiceMock).deleteById(99999L);

      mockMvc.perform(delete(EXPENSE_BASE_URI + "/99999"))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete must return ErrorDto when id is invalid")
    void delete_MustReturnErrorDto_WhenIdIsInvalid() throws Exception {
      mockMvc.perform(delete(EXPENSE_BASE_URI + "/50oo0"))
          .andExpect(status().isBadRequest());
    }

  }

}
