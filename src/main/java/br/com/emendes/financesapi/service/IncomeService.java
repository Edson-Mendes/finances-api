package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
public interface IncomeService {

  IncomeResponse create(IncomeRequest incomeRequest);

  Page<IncomeResponse> readAllByUser(Pageable pageable);

  Page<IncomeResponse> readByDescriptionAndUser(String description, Pageable pageable);

  IncomeResponse readByIdAndUser(Long incomeId);

  Page<IncomeResponse> readByYearAndMonthAndUser(
      @Min(value = 1970, message = "year must be equals or greater than {value}")
      @Max(value = 2099, message = "year must be equals or less than {value}") int year,
      @Min(value = 1, message = "month must be equals or greater than {value}")
      @Max(value = 12, message = "month must be equals or less than {value}") int month,
      Pageable pageable);

  IncomeResponse update(Long id, IncomeRequest incomeRequest);

  void deleteById(Long id);

  BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month);

}
