package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface IncomeService {

  IncomeDto create(IncomeForm incomeForm);

  Page<IncomeDto> readAllByUser(Pageable pageable);

  Page<IncomeDto> readByDescriptionAndUser(String description, Pageable pageable);

  IncomeDto readByIdAndUser(Long incomeId);

  Page<IncomeDto> readByYearAndMonthAndUser(int year, int month, Pageable pageable);

  IncomeDto update(Long id, IncomeForm incomeForm);

  void deleteById(Long id);

  BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month);

}
