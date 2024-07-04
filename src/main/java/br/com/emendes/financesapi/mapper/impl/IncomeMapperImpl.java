package br.com.emendes.financesapi.mapper.impl;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.mapper.IncomeMapper;
import br.com.emendes.financesapi.model.entity.Income;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * Implementação de {@link IncomeMapper}.
 */
@Component
public class IncomeMapperImpl implements IncomeMapper {

  @Override
  public Income toIncome(IncomeRequest incomeRequest) {
    Assert.notNull(incomeRequest, "incomeRequest must not be null.");

    return Income.builder()
        .description(incomeRequest.getDescription())
        .value(incomeRequest.getValue())
        .date(LocalDate.parse(incomeRequest.getDate()))
        .build();
  }

  @Override
  public IncomeResponse toIncomeResponse(Income income) {
    Assert.notNull(income, "income must not be null.");

    return IncomeResponse.builder()
        .id(income.getId())
        .description(income.getDescription())
        .value(income.getValue())
        .date(income.getDate())
        .build();
  }

  @Override
  public void merge(Income income, IncomeRequest incomeRequest) {
    Assert.notNull(income, "income must not be null.");
    Assert.notNull(incomeRequest, "incomeRequest must not be null.");

    income.setDescription(incomeRequest.getDescription());
    income.setValue(incomeRequest.getValue());
    income.setDate(LocalDate.parse(incomeRequest.getDate()));
  }

}
