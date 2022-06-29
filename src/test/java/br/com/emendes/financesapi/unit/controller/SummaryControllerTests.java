package br.com.emendes.financesapi.unit.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import br.com.emendes.financesapi.controller.SummaryController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.util.creator.SummaryDtoCreator;
import br.com.emendes.financesapi.service.SummaryService;
import br.com.emendes.financesapi.service.TokenService;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for SummaryController")
public class SummaryControllerTests {

  @InjectMocks
  private SummaryController summaryController;

  @Mock
  private SummaryService summaryServiceMock;

  @Mock
  private TokenService tokenServiceMock;

  private final HttpServletRequest REQUEST_MOCK = Mockito.mock(HttpServletRequest.class);

  @BeforeEach
  public void setUp() {
    SummaryDto summaryDto = SummaryDtoCreator.simpleSummaryDto();

    BDDMockito.when(tokenServiceMock.getUserId(ArgumentMatchers.any(HttpServletRequest.class)))
        .thenReturn(100l);

    BDDMockito.when(summaryServiceMock.monthSummary(2022, 1, 100l))
        .thenReturn(summaryDto);
  }

  @Test
  @DisplayName("monthSummary returns ResponseEntity<SummaryDto> when successful")
  void monthSummary_returnResponseEntitySummaryDto_WhenSuccessful() {
    Integer year = 2022;
    Integer month = 1;

    ResponseEntity<SummaryDto> response = summaryController.monthSummary(year, month, REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody().getFinalBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
    Assertions.assertThat(response.getBody().getIncomeTotalValue()).isEqualByComparingTo(new BigDecimal("2500.00"));
    Assertions.assertThat(response.getBody().getExpenseTotalValue()).isEqualByComparingTo(new BigDecimal("2000.00"));
  }

}
