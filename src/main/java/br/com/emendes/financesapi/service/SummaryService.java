package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.SummaryResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SummaryService {

  SummaryResponse monthSummary(
      @Min(value = 1970, message = "year must be equals or greater than {value}")
      @Max(value = 2099, message = "year must be equals or less than {value}") int year,
      @Min(value = 1, message = "month must be equals or greater than {value}")
      @Max(value = 12, message = "month must be equals or less than {value}") int month);

}
