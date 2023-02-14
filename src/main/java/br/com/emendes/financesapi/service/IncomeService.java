package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.dto.request.IncomeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface IncomeService {

  IncomeResponse create(IncomeRequest incomeRequest);

  Page<IncomeResponse> readAllByUser(Pageable pageable);

  Page<IncomeResponse> readByDescriptionAndUser(String description, Pageable pageable);

  IncomeResponse readByIdAndUser(Long incomeId);

  Page<IncomeResponse> readByYearAndMonthAndUser(int year, int month, Pageable pageable);

  IncomeResponse update(Long id, IncomeRequest incomeRequest);

  void deleteById(Long id);

  BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month);

}
