package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
public interface ExpenseService {

  ExpenseResponse create(ExpenseRequest expenseRequest);

  Page<ExpenseResponse> readAllByUser(Pageable pageable);

  Page<ExpenseResponse> readByDescriptionAndUser(String description, Pageable pageable);

  ExpenseResponse readByIdAndUser(Long id);

  Page<ExpenseResponse> readByYearAndMonthAndUser(
      @Min(value = 1970, message = "year must be equals or greater than {value}")
      @Max(value = 2099, message = "year must be equals or less than {value}") int year,
      @Min(value = 1, message = "month must be equals or greater than {value}")
      @Max(value = 12, message = "month must be equals or less than {value}") int month,
      Pageable pageable);

  ExpenseResponse update(Long id, ExpenseRequest expenseRequest);

  void deleteById(Long id);

  List<ValueByCategoryResponse> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month);

}
