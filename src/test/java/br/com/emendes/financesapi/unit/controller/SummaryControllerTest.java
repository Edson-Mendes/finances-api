package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.SummaryController;
import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.SummaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = SummaryController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DisplayName("Tests for SummaryController")
class SummaryControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private SummaryService summaryServiceMock;

  private final String SUMMARY_BASE_URI = "/api/summaries";

  @Nested
  @DisplayName("Tests for monthSummary endpoint")
  class monthSummaryEndpoint {

    @Test
    @DisplayName("monthSummary must return SummaryResponse when year 2023 and month is 2 successfully")
    void monthSummary_MustReturnSummaryResponse_WhenYearIs2023AndMonthIs2Successfully() throws Exception {
      List<ValueByCategoryResponse> valueByCategoryResponseList =
          List.of(ValueByCategoryResponse.builder().category(Category.MORADIA).value(new BigDecimal("1500.00")).build());

      SummaryResponse summaryResponse = SummaryResponse.builder()
          .incomeTotalValue(new BigDecimal("2500.00"))
          .expenseTotalValue(new BigDecimal("1500.00"))
          .finalBalance(new BigDecimal("1000.00"))
          .valuesByCategory(valueByCategoryResponseList)
          .build();

      BDDMockito.when(summaryServiceMock.monthSummary(2023, 2))
          .thenReturn(summaryResponse);

      mockMvc.perform(get(SUMMARY_BASE_URI + "/2023/2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.incomeTotalValue").value(2500.00))
          .andExpect(jsonPath("$.expenseTotalValue").value(1500.00))
          .andExpect(jsonPath("$.finalBalance").value(1000.00))
          .andExpect(jsonPath("$.valuesByCategory").isArray())
          .andExpect(jsonPath("$.valuesByCategory[0].category").value("MORADIA"))
          .andExpect(jsonPath("$.valuesByCategory[0].value").value(1500.00));
    }

    @Test
    @DisplayName("monthSummary must return ProblemDetail when user has no expenses or incomes for year 2023 and month 3")
    void monthSummary_MustReturnProblemDetail_WhenUserHasNoExpensesOrIncomesForYear2023AndMonth3() throws Exception {
      BDDMockito.given(summaryServiceMock.monthSummary(2023, 3))
          .willThrow(new EntityNotFoundException("Has no expenses or incomes for MARCH 2023"));

      mockMvc.perform(get(SUMMARY_BASE_URI + "/2023/3"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("Has no expenses or incomes for MARCH 2023"));
    }

    @Test
    @DisplayName("monthSummary must return ProblemDetail when year is invalid")
    void monthSummary_MustReturnProblemDetail_WhenYearIsInvalid() throws Exception {
      mockMvc.perform(get(SUMMARY_BASE_URI + "/2o23/3"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

    @Test
    @DisplayName("monthSummary must return ProblemDetail when month is invalid")
    void monthSummary_MustReturnProblemDetail_WhenMonthIsInvalid() throws Exception {
      mockMvc.perform(get(SUMMARY_BASE_URI + "/2023/1o"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

  }

}
